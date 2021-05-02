package ch.uzh.ifi.hase.soprafs21.controller;

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





    // getting users
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetLimitedDTO> getUsers(
            @RequestHeader("userIds") Optional<List<Long>> userIds,
            @RequestHeader("usernames") Optional<List<String>> usernames
    ) {
        if (userIds.isPresent() && usernames.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't specify both usernames and userIds!");

        List<User> users = new ArrayList<>();
        if (userIds.isPresent()) {
            // fetch users by userIds
            for (Long userId : userIds.get()) users.add(userService.getUserByUserId(userId));
        } else if (usernames.isPresent()) {
            // fetch users by usernames
            for (String username : usernames.get()) users.add(userService.getUserByUsername(username));
        } else {
            // fetch all users in the internal representation
            users = userService.getUsers();
        }

        // convert each user to the API representation
        List<UserGetLimitedDTO> userGetDTOs = new ArrayList<>();
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetLimitedDTO(user));
        }
        return userGetDTOs;
    }

    /**
     * get a single user
     */
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetLimitedDTO getUser(
            @RequestHeader("userId") Optional<Long> userId,
            @RequestHeader("username") Optional<String> username
    ) {
        if (userId.isPresent() == username.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please specify either username or userId!");

        // fetch user from internal representation
        User user = userId.isPresent()?
                userService.getUserByUserId(userId.get())
                :
                userService.getUserByUsername(username.get());

        return DTOMapper.INSTANCE.convertEntityToUserGetLimitedDTO(user);
    }

    /**
     * get own user
     */
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetCompleteDTO getOwnUser(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        User user = userService.verifyUser(userId, token);
        return DTOMapper.INSTANCE.convertEntityToUserGetCompleteDTO(user);
    }


    @PutMapping(value = "/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUser(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody UserPutDTO inputUserPutDTO
    ) {
        User user = userService.verifyUser(userId, token);
        userService.updateUser(user, DTOMapper.INSTANCE.convertUserPutDTOtoEntity(inputUserPutDTO));
    }

}
