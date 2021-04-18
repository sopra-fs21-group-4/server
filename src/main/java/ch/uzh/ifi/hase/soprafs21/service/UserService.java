package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
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
        newUser.setStatus(UserStatus.OFFLINE);

        checkIfUserExists(newUser);

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
        user.setStatus(UserStatus.ONLINE);
        return user;

    }
    public void updateUsername(String newUsername, User user) {

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
     * check if user id and token are correct (if user is logged in)
     */
    public void verifyUser(Long id, String token){

        User user = userRepository.findByUserId(id);

        //check for token
        if (user==null || !user.getToken().equals(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Access denied"));
        }
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    public void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Please choose another one!";
        if (userByUsername != null ) { //&& userByName != null
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }

    }
}
