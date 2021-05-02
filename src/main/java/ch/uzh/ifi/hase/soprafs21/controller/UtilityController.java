package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UtilityController {

    private final UserService userService;

    UtilityController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserLoginDTO chat(@RequestBody UserPostDTO userPostDTO){
        return null;
    }




}
