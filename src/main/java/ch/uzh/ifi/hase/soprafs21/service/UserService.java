package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.SseUpdateDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

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

    //map key UI to SseEmitter
    private final Map<Long , SseEmitter> subscriberMapping = new HashMap<>();
    private final Map<Long , Map<Long, Long>> clientVersions = new HashMap<>();


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
    @Scheduled(fixedRate=100)
    public void updateUsers(){
        //list of all users who are in games (State PLAYING)
        long now = System.currentTimeMillis();

        for (User user : userRepository.findAllByStatus(UserStatus.PLAYING)) {
            //check if the User is actually subscribed
            if (!subscriberMapping.containsKey(user.getUserId())){
                user.setStatus(UserStatus.OFFLINE);
                continue;
            }
            // users without a game are set to IDLE
            if (user.getCurrentGameId() == null) {
                user.setStatus(UserStatus.IDLE);
                continue;
            }

            // the same goes for expired games and non-enrolled games
            Game game = gameRepository.findByGameId(user.getCurrentGameId());
            if (game == null || !game.getPlayerState(user.getUserId()).isEnrolled()) {
                user.setCurrentGameId(null);
                user.setStatus(UserStatus.IDLE);
            }
        }
        //list of all users wo are in State IDLE
        for (User user : userRepository.findAllByStatus(UserStatus.IDLE)) {
            // if the user has a game, set status to PLAYING
            if (user.getCurrentGameId()!= null){
                user.setStatus(UserStatus.PLAYING);
            }
            if (!subscriberMapping.containsKey(user.getUserId())){
                user.setStatus(UserStatus.OFFLINE);
            }
        }

        for (Long userId : subscriberMapping.keySet()) {
            // find users and corresponding sseEmitter in subscriber Map.
            User user = userRepository.findByUserId(userId);
            if (!clientVersions.containsKey(userId)) clientVersions.put(userId, new HashMap<>());
            Map<Long, Long> clientVersion = clientVersions.get(userId);
            SseEmitter sseEmitter = subscriberMapping.get(userId);
            SseUpdateDTO sseUpdateDTO = DTOMapper.INSTANCE.convertEntityToSseUpdateDTO(user);
            sseUpdateDTO.init();
            if (!sseUpdateDTO.filter(clientVersion, now)) continue;
            try {
                sseEmitter.send(SseEmitter.event().name("Update").data(sseUpdateDTO, MediaType.APPLICATION_JSON));
            } catch(IOException e) {
                log.error("could not update User " + userId);
            }
        }
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user credentials");
        }
        else if (!user.getPassword().equals(usertologin.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user credentials");
        }

        // setting new token and returning it
        user.setToken(UUID.randomUUID().toString());
        user.setStatus(UserStatus.IDLE);
        return user;

    }

    public User updateUser(User user, User updateEntity) {

        String newUsername = updateEntity.getUsername();
        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            checkUsernameConstraints(newUsername);
            user.setUsername(newUsername);
        }

        String newPassword = updateEntity.getPassword();
        if (newPassword != null && !newPassword.equals(user.getPassword())) {
            checkPasswordConstraints(newPassword);
            user.setPassword(newPassword);
        }

        String newEmail = updateEntity.getEmail();
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            checkEmailConstraints(newEmail);
            user.setEmail(newEmail);
        }
        userRepository.flush();
        return user;
    }

    public void observeEntity(User user, Long entityId) {
        user.observeEntity(entityId);
        userRepository.flush();
    }

    public void disregardEntity(User user, Long entityId) {
        user.disregardEntity(entityId);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid userId");
        if (!user.getToken().equals(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied");
        if (user.getStatus() == UserStatus.OFFLINE)
            user.setStatus(UserStatus.IDLE);

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
        if (email == null || email.equals("")) return;
        String[] atSplit = email.split("@");
        if (atSplit.length != 2 || !atSplit[1].contains(".") || atSplit[1].endsWith("."))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "invalid email format");

    }


    /**
     * Sending a new friend request, request is added to both users as incoming and outgoing
     * @param user the user sending the request
     * @param friendName the username of the user receiving the request
     */
    public void sendFriendRequest(User user, String friendName){

        User friend = userRepository.findByUsername(friendName);
        if (friend==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");

        friendChange(user, friend.getUserId(), CHANGE.ADD, LIST.OUTGOING);
        friendChange(friend, user.getUserId(), CHANGE.ADD, LIST.INCOMING);

    }

    /**
     * remove both users from friends list of each other
     * @param user the user removing a friend
     * @param friendId the userid of the friend to be removed
     */
    public void removeFriendRequest(User user, Long friendId){

        User friend = getFriend(friendId);

        friendChange(user, friendId, CHANGE.REMOVE, LIST.OUTGOING);
        friendChange(friend, user.getUserId(), CHANGE.REMOVE, LIST.INCOMING);

    }

    /**
     * remove both users from friends list of each other
     * @param user the user removing a friend
     * @param friendId the userid of the friend to be removed
     */
    public void removeFriend(User user, Long friendId){

        User friend = getFriend(friendId);

        friendChange(user, friendId, CHANGE.REMOVE, LIST.FRIENDS);
        friendChange(friend, user.getUserId(), CHANGE.REMOVE, LIST.FRIENDS);

    }

    /**
     * removing incoming/outgoing requests and adding friend to friends list of both users
     * @param user user who accepts the friend request
     * @param friendId userid of friend to accept request of
     */
    public void acceptFriendRequest(User user, Long friendId){

        User friend = getFriend(friendId);

        friendChange(user, friend.getUserId(), CHANGE.REMOVE, LIST.INCOMING);
        friendChange(friend, user.getUserId(), CHANGE.REMOVE, LIST.OUTGOING);
        friendChange(user, friend.getUserId(), CHANGE.ADD, LIST.FRIENDS);
        friendChange(friend, user.getUserId(), CHANGE.ADD, LIST.FRIENDS);

    }

    /**
     * removing incoming/outgoing requests
     * @param user user who rejects the friend request
     * @param friendId userid of friend to reject request of
     */
    public void rejectFriendRequest(User user, Long friendId){

        User friend = getFriend(friendId);

        friendChange(user, friend.getUserId(), CHANGE.REMOVE, LIST.INCOMING);
        friendChange(friend, user.getUserId(), CHANGE.REMOVE, LIST.OUTGOING);

    }

    /**
     * helper method to get friend or throw error if he does not exist
     * @param friendId id of the friend you want to get
     * @return friend user object
     */
    private User getFriend(long friendId){
        User friend = userRepository.findByUserId(friendId);
        if (friend==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        return friend;
    }

    /**
     * changing the friend list, incoming and outgoing request lists
     */
    private enum CHANGE{ADD,REMOVE}
    private enum LIST{FRIENDS,INCOMING,OUTGOING}
    private void friendChange(User toChangeUser, Long friendId, CHANGE change, LIST list){
        if(toChangeUser.getUserId().equals(friendId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot add yourself");
        }
        switch (change){
            case ADD:
                if(toChangeUser.getFriends().contains(friendId)){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is already a friend");
                }
                switch (list){
                    case FRIENDS:
                        toChangeUser.addFriend(friendId);
                        break;
                    case INCOMING:
                        if(toChangeUser.getIncomingFriendRequests().contains(friendId)){
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "incoming request already exists");
                        }
                        else if(toChangeUser.getOutgoingFriendRequests().contains(friendId)){
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user already has an outgoing request");
                        }
                        else{
                            toChangeUser.addIncomingFriendRequest(friendId);
                        }
                        break;
                    case OUTGOING:
                        if(toChangeUser.getOutgoingFriendRequests().contains(friendId)){
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "outgoing request already exists");
                        }
                        else if(toChangeUser.getIncomingFriendRequests().contains(friendId)){
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user already has an incoming request");
                        }
                        else{
                            toChangeUser.addOutgoingFriendRequest(friendId);
                        }
                        break;
                }
            break;
            case REMOVE:
                switch (list){
                    case FRIENDS:
                        if(toChangeUser.getFriends().contains(friendId)){
                            toChangeUser.removeFriend(friendId);
                        }
                        else{
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "friend does not exists");
                        }
                        break;
                    case INCOMING:
                        if(toChangeUser.getIncomingFriendRequests().contains(friendId)){
                            toChangeUser.removeIncomingFriendRequest(friendId);
                        }
                        else{
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "incoming request does not exists");
                        }
                        break;
                    case OUTGOING:
                        if(toChangeUser.getOutgoingFriendRequests().contains(friendId)){
                            toChangeUser.removeOutgoingFriendRequest(friendId);
                        }
                        else{
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "outgoing request does not exists");
                        }
                        break;
                }
            break;
        }

    }


    /**
     * The subscriber method for our SSEController
     */
    public void putSubscriber(Long userId, SseEmitter emitter) {
        subscriberMapping.put(userId, emitter);
        userRepository.findByUserId(userId).setStatus(UserStatus.IDLE);
    }

    public void removeSubscriber(Long userId) {
        subscriberMapping.remove(userId);
        clientVersions.remove(userId);
    }




}
