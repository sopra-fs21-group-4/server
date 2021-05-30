package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result..
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * login a user by id
     */
    @PatchMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserLoginDTO loginUser(
            @RequestBody UserPostDTO userPostDTO
    ) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User user = userService.loginUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(user);
    }

    /**
     * creating a new user
     */
    @PostMapping("/users/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserLoginDTO createUser(
            @RequestBody UserPostDTO userPostDTO
    ) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        // create user
        User createdUser = userService.createUser(userInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(createdUser);
    }

    @PutMapping(value = "/user")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserLoginDTO updateUser(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody UserPutDTO userPutDTO
    ) {
        User user = userService.verifyUser(userId, token);
        User updateEntity = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        user = userService.updateUser(user, updateEntity);
        return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(user);
    }

    // Friend requests:
    /**
     * sending a new friend request
     */
    @PutMapping(value = "/friends/sendRequest")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void sendFriendRequest(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody String friendName
    ) {
        User user = userService.verifyUser(userId, token);
        userService.sendFriendRequest(user, friendName);

    }

    /**
     * remove an existing friend request
     */
    @PutMapping(value = "/friends/removeRequest")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removeFriendRequest(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody Long friendId
    ) {
        User user = userService.verifyUser(userId, token);
        userService.removeFriendRequest(user, friendId);
    }

    /**
     * remove an existing friend
     */
    @PutMapping(value = "/friends/removeFriend")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removeFriend(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody Long friendId
    ) {
        User user = userService.verifyUser(userId, token);
        userService.removeFriend(user, friendId);
    }

    /**
     * accepting a friend request
     */
    @PutMapping(value = "/friends/acceptRequest")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void acceptFriendRequest(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody Long friendId
    ) {
        User user = userService.verifyUser(userId, token);
        userService.acceptFriendRequest(user, friendId);
    }

    /**
     * rejecting a friend request
     */
    @PutMapping(value = "/friends/rejectRequest")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void rejectFriendRequest(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody Long friendId
    ) {
        User user = userService.verifyUser(userId, token);
        userService.rejectFriendRequest(user, friendId);
    }

}
