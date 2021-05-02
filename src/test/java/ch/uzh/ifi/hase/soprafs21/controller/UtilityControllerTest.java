package ch.uzh.ifi.hase.soprafs21.controller;

public class UtilityControllerTest {
    
}

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