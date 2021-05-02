package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSummary;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Autowired
    public UserService(
            @Qualifier("userRepository") UserRepository userRepository,
            @Qualifier("gameRepository") GameRepository gameRepository
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    /**
     * Loop running every 1000ms to update user states
     */
    @Scheduled(fixedRate=1000)
    public void updateUsers(){
        long now = System.currentTimeMillis();

        for (User user : userRepository.findAllByStatus(UserStatus.PLAYING)) {
            // users without a game are set to IDLE
            if (user.getCurrentGameId() == null) user.setStatus(UserStatus.IDLE);
            // the same goes for expired games and non-enrolled games
            Game game = gameRepository.findByGameId(user.getCurrentGameId());
            if (game == null || !game.getPlayerState(user.getUserId()).isEnrolled()) {
                user.setCurrentGameId(null);
                user.setStatus(UserStatus.IDLE);
            }
        }

        for (User user : userRepository.findAllByStatus(UserStatus.IDLE)) {
            // if the user has a game, set status to PLAYING
            if (user.getCurrentGameId()!= null) user.setStatus(UserStatus.PLAYING);
            // if no request was received for more than 3 seconds, set status to offline
            else if (now - user.getLastRequest() > 3000) user.setStatus(UserStatus.OFFLINE);
            // note that as long as a user will not be set to OFFLINE as long as they are part of a game
        }

        // offline users are not updated. A user can get out of the offline status by sending any verifying request
    }


    public List<User> getUsers() {
        return this.userRepository.findAll();
    }
    public User getUserByUsername(String username){
        return this.userRepository.findByUsername(username);
    }
    public User getUserByUserId(Long id){
        return this.userRepository.findByUserId(id);
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.IDLE);

        checkUsernameConstraints(newUser.getUsername());
        checkPasswordConstraints(newUser.getPassword());
        checkEmailConstraints(newUser.getEmail());

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User loginUser(User usertologin){

        User userByUsername = userRepository.findByUsername(usertologin.getUsername());
        User user = getUserByUsername(usertologin.getUsername());

        // check authorization
        if (userByUsername == null )  {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Invalid user credentials"));
        }
        else if (!user.getPassword().equals(usertologin.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Invalid user credentials"));
        }

        // setting new token and returning it
        user.setToken(UUID.randomUUID().toString());
        user.setStatus(UserStatus.IDLE);
        return user;

    }

    public void updateUser(User user, User updateEntity) {
        String newUsername = updateEntity.getUsername();
        if (newUsername != null && user.getUsername().equals(newUsername)) {
            checkUsernameConstraints(newUsername);
            user.setUsername(newUsername);
        }
        String newPassword = updateEntity.getPassword();
        if (newPassword != null && user.getPassword().equals(newPassword)) {
            checkPasswordConstraints(newPassword);
            user.setPassword(newPassword);
        }
        String newEmail = updateEntity.getEmail();
        if (newEmail != null && user.getEmail().equals(newEmail)) {
            checkEmailConstraints(newEmail);
            user.setEmail(newEmail);
        }
        userRepository.flush();
    }

    public void updateUsername(String newUsername, User user) {
        checkUsernameConstraints(newUsername);
        user.setUsername(newUsername);
        userRepository.flush();
    }
    public void updatePassword(String newPassword, User user) {
        user.setPassword(newPassword);
        userRepository.flush();

    }
    public void updateEmail(String newEmail, User user) {
        user.setEmail(newEmail);
        userRepository.flush();

    }

    /**
     * check if user id and token are correct (if user is logged in).
     * also sets the user's lastRequest timestamp to now if successful.
     * @return the found user.
     */
    public User verifyUser(Long id, String token){
        User user = userRepository.findByUserId(id);
        if (user==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("invalid userId"));
        if (!user.getToken().equals(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Access denied"));
        if (user.getStatus() == UserStatus.OFFLINE)
            user.setStatus(UserStatus.IDLE);

        user.setLastRequest(System.currentTimeMillis());
        return user;
    }

    /**
     * This is a helper method that will check the following criteria for a username:
     * 1) not null
     * 2) no forbidden usernames
     * 3) no forbidden substrings
     * 4) unique
     * The method will do nothing if the input is valid and throw an error otherwise.
     *
     * @param username the name a user wants to take
     * @see User
     */
    public void checkUsernameConstraints(String username) {
        if (username == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "username cannot be null");
        String[] illegalUsernames = {
                "", "Botterfly", "all", "admin", "admins", "gm"
        };
        for (String str : illegalUsernames) {
            if (username.equals(str))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "illegal username");
        }
        String[] illegalSubstrings = {
                " ", ","
        };
        for (String str : illegalSubstrings) {
            if (username.contains(str))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "username cannot contain "+str);
        }

        User userWithEqualName = userRepository.findByUsername(username);
        if (userWithEqualName != null )
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username must be unique");
    }

    /**
     * This is a helper method that will check the following criteria for a password:
     * 1) not null
     * 2) no forbidden passwords
     * The method will do nothing if the input is valid and throw an error otherwise.
     *
     * @param password the password a user wants to take
     * @see User
     */
    public void checkPasswordConstraints(String password) {
        if (password == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "password cannot be null");
        String[] illegalPasswords = {
                ""
        };
        for (String str : illegalPasswords) {
            if (password.equals(str))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "illegal password");
        }
    }

    /**
     * This is a helper method that will check the following criteria for an email:
     * 1) null or
     * 2) email format
     * The method will do nothing if the input is valid and throw an error otherwise.
     *
     * @param email the email a user wants to take
     * @see User
     */
    public void checkEmailConstraints(String email) {
        if (email == null) return;
        String[] atSplit = email.split("@");
        if (atSplit.length != 2 || !atSplit[1].contains(".") || atSplit[1].endsWith("."))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "invalid email format");

    }
}
