package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

//    @Test
    public void TestifUserisCreatedCorrectly() throws Exception {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setEmail("firstname@lastname");
        user.setPassword("somePassword");
        user.setToken("someToken");

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.createUser(Mockito.any())).willReturn(user);


        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("Thomas");
        userPostDTO.setPassword("somePassword");
        userPostDTO.setEmail("firstname@lastname");

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.userId", is(user.getUserId().intValue())))
                .andExpect(jsonPath("$.token", is(user.getToken())));
    }
    //Input: UserPostDTO
    //Output: UserLoginDTO
    //Status: OK
//    @Test
    public void TestifUserLoginWorksCorrectlywithUserId() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Mark");
        user.setEmail("firstname@lastname");
        user.setPassword("somePassword");
        user.setToken("someToken");

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.loginUser(Mockito.any())).willReturn(user);


        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("Mark");
        userPostDTO.setPassword("somePassword");
        userPostDTO.setEmail("firstname@lastname");

        // when
        MockHttpServletRequestBuilder patchRequest = patch("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(patchRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.userId", is(user.getUserId().intValue())))
                .andExpect(jsonPath("$.token", is(user.getToken())));
    }


    //Input: RequestHeader userId and RequestHEader token
    //Output: UserPrivateDTO
    //Status: OK
 //   @Test
    public void TestifUserGetsReturnedCorrectly() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setToken("someToken");
        user.setCurrentGameId(2L);
        //Map<Long, MessageChannelGetDTO> set some MessageChannelGetDTO values
        //Map<Long, UserPublicDTO> set some UserPublicDTO values
        //Map<Long, GameSummaryDTO> set some GameSummaryDTO values

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);


        //what will be returned --> has several sub DTOs; might have to create them as well
        UserPrivateDTO userPrivateDTO = new UserPrivateDTO();


        // when
        MockHttpServletRequestBuilder getRequest = get("/me")
                .contentType(MediaType.APPLICATION_JSON);


        // then compare PrivateDTOValues with the user values
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.", is(user.getToken())))
                .andExpect(jsonPath("$.", is(user.getToken())))
                .andExpect(jsonPath("$.", is(user.getToken())));

    }
    /**
        //GetRequest
        //Input: Optional<List<Long>> userIds or Optional<List<String>> usernames
        //Output: List<UserPublicDTO>
        //Status: OK
        //TODOo: Friends still missing
        Test
        public void TestifgettingUserWorks () throws Exception {
            List<User> testList = new ArrayList<>();

            User user1 = new User();
            user1.setUserId(1L);
            user1.setUsername("U1");
            user1.setStatus(UserStatus.IDLE);
            user1.setCurrentGameId(2L);

            User user2 = new User();
            user1.setUserId(2L);
            user1.setUsername("U2");
            user1.setStatus(UserStatus.IDLE);
            user1.setCurrentGameId(2L);

            testList.add(user1);
            testList.add(user1);

            // this mocks the UserService -> we define above what the userService should return when getUsers() is called
            given(userService.getUsers()).willReturn(testList);


            // when
            MockHttpServletRequestBuilder getRequest = get("/users")
                    .contentType(MediaType.APPLICATION_JSON);

            // then
            mockMvc.perform(getRequest).andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].testList", is(user1)))
                    .andExpect(jsonPath("$[1].testList", is(user2)));
            //.andExpect(jsonPath("$[0].testList.username", is(user1.getUsername())));
        }

         * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
         * Input will look like this: {"name": "Test User", "username": "testUsername"}
         * param object
         * return string
         */
    private String asJsonString ( final Object object){
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}