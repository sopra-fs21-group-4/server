package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
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
    public UserLoginDTO loginUser(@RequestBody UserPostDTO userPostDTO){

        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        User user = userService.loginUser(userInput);

        UserLoginDTO userLoginDTO = DTOMapper.INSTANCE.convertEntityToUserLoginDTO(user);
        return userLoginDTO;
    }

    /**
     * creating a new user
     */
    @PostMapping("/users/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserLoginDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(createdUser);
    }





    // getting all users TODO for debugging purposes at the moment
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getUsers(
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
        List<UserGetDTO> userGetDTOs = new ArrayList<>();
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    /**
     * get a single user
     */
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(
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

        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }


    @PutMapping(value = "/user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUser(@PathVariable("id") Long userId, @RequestBody UserPutDTO inputUserPutDTO) {
        // fetch all users in the internal representation
        User user = userService.getUserByUserId(userId);

        User user1 = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(inputUserPutDTO);
        if (user1.getUsername() == user.getUsername()){
            if(user1.getPassword() != null){
                userService.updatePassword(user1.getPassword(), user);
            }
        }
        else {
            userService.checkIfUserExists(user1);
            if (user1.getUsername() != null) {
                userService.updateUsername(user1.getUsername(), user);
            }
            if (user1.getEmail() != null) {
                userService.updateEmail(user1.getEmail(), user);
            }
            if (user1.getPassword() != null) {
                userService.updatePassword(user1.getPassword(), user);
            }
        }
    }

}
