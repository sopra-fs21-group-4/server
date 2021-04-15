package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
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
     * TODO this should be a patch request
     */
    @PostMapping("/users/login")
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
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
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
            @RequestParam Optional<Long> userId,
            @RequestParam Optional<String> username
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


}
