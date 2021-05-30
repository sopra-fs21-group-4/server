package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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

    @MockBean
    private DTOMapper dtoMapper;

    //Input:
    //RequestBody --> UserPostDTO
    //ResponseBody --> UserLoginDTO
    @Test
    public void TestifUserLoginWorks() throws  Exception{
        //given
        User user1 = new User();
        user1.setStatus(UserStatus.IDLE);
        user1.setEmail("firstname@lastname");
        user1.setUserId(1L);
        user1.setUsername("Thomas");
        user1.setPassword("somePassword");
        user1.setToken("someToken");
        user1.setCurrentGameId(2L);

        User user2 = new User();
        user2.setStatus(UserStatus.IDLE);
        user2.setEmail("firstname@lastname");
        user2.setUserId(1L);
        user2.setUsername("Thomas");
        user2.setPassword("somePassword");
        user2.setToken("someotherToken");
        user2.setCurrentGameId(2L);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("Thomas");
        userPostDTO.setPassword("somePassword");
        userPostDTO.setEmail("firstname@lastname");

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserId(1L);
        userLoginDTO.setUsername("Thomas");
        userLoginDTO.setToken("someotherToken");

        //mock the UserService - returns user
        given(dtoMapper.convertUserPostDTOtoEntity(Mockito.any())).willReturn(user1);
        given(userService.loginUser(Mockito.any())).willReturn(user2);
        given(dtoMapper.convertEntityToUserLoginDTO(Mockito.any())).willReturn(userLoginDTO);


        // when - Input is given
        MockHttpServletRequestBuilder patchRequest = patch("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then - check the output
        mockMvc.perform(patchRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(user2.getUserId().intValue())))
                .andExpect(jsonPath("$.username", is(userLoginDTO.getUsername())))
                .andExpect(jsonPath("$.token", is(userLoginDTO.getToken())));
    }

    @Test
    public void TestifUserisCreatedCorrectly() throws Exception {
        // given
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setEmail("firstname@lastname");
        user.setPassword("somePassword");
        user.setToken("someToken");

        User user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("Thomas");
        user1.setEmail("firstname@lastname");
        user1.setPassword("somePassword");
        user1.setToken("someOtherToken");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("Thomas");
        userPostDTO.setPassword("somePassword");
        userPostDTO.setEmail("firstname@lastname");

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserId(1L);
        userLoginDTO.setUsername("Thomas");
        userLoginDTO.setToken("someOtherToken");

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(dtoMapper.convertUserPostDTOtoEntity(Mockito.any())).willReturn(user);
        given(userService.createUser(Mockito.any())).willReturn(user1);
        given(dtoMapper.convertEntityToUserLoginDTO(Mockito.any())).willReturn(userLoginDTO);


        // when
        MockHttpServletRequestBuilder postRequest = post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(user1.getUserId().intValue())))
                .andExpect(jsonPath("$.username", is(userLoginDTO.getUsername())))
                .andExpect(jsonPath("$.token", is(userLoginDTO.getToken())));
    }

    //Flow Control testing
    //Input: RequestHeader userId and RequestHeader token
    //Output: UserPrivateDTO
    //Status: OK
    @Test
    public void TestupdateUser() throws Exception{
        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(2L);

        User inBetween = new User();
        inBetween.setUsername("Markus");
        inBetween.setEmail("evenmorerandom");
        inBetween.setPassword("somePassword");

        User testUser2 = new User();
        testUser2.setEmail("evenmorerandom");
        testUser2.setUsername("Markus");
        testUser2.setToken("someToken");
        testUser2.setPassword("somePassword");
        testUser2.setUserId(2L);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setEmail("evenmorerandom");
        userPutDTO.setUsername("Markus");
        userPutDTO.setPassword("somePassword");


        //mock the UserService - returns user
        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        given(dtoMapper.convertUserPutDTOtoEntity(Mockito.any())).willReturn(inBetween);
        given(userService.updateUser(Mockito.any(), Mockito.any())).willReturn(testUser2);


        // when - Input is given
        MockHttpServletRequestBuilder putRequest = put("/user")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then - check the output
        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(testUser2.getUserId().intValue())))
                .andExpect(jsonPath("$.username", is(testUser2.getUsername())))
                .andExpect(jsonPath("$.token", is(testUser2.getToken())));
    }

    @Test
    public void TestFriendRequest() throws Exception{
        User testFriend = new User();
        testFriend.setEmail("randomx");
        testFriend.setToken("aToken");
        testFriend.setPassword("b");
        testFriend.setUserId(5L);
        testFriend.setUsername("YouWannaBeMyFriend");

        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(2L);

        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        doNothing().when(userService).sendFriendRequest(Mockito.any(), Mockito.any());

        // when - Input is given
        MockHttpServletRequestBuilder putRequest = put("/friends/sendRequest")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testFriend.getUsername()));

        // then - check the output
        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(userService).sendFriendRequest(testUser1, "\""+testFriend.getUsername()+"\"");
    }

    @Test
    public void TestFriendRemovalRequest() throws Exception{
        User testFriend = new User();
        testFriend.setEmail("randomx");
        testFriend.setUsername("Thom");
        testFriend.setToken("aToken");
        testFriend.setPassword("b");
        testFriend.setUserId(5L);

        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(2L);

        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        doNothing().when(userService).removeFriendRequest(Mockito.any(), Mockito.any());

        // when - Input is given
        MockHttpServletRequestBuilder putRequest = put("/friends/removeRequest")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testFriend.getUserId()));

        // then - check the output
        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(userService).removeFriendRequest(testUser1, testFriend.getUserId());
    }

    @Test
    public void TestFriendRemoval() throws Exception{
        User testFriend = new User();
        testFriend.setEmail("randomx");
        testFriend.setUsername("Thom");
        testFriend.setToken("aToken");
        testFriend.setPassword("b");
        testFriend.setUserId(5L);

        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(2L);


        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        doNothing().when(userService).removeFriend(Mockito.any(), Mockito.any());

        // when - Input is given
        MockHttpServletRequestBuilder putRequest = put("/friends/removeFriend")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testFriend.getUserId()));

        // then - check the output
        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(userService).removeFriend(testUser1, testFriend.getUserId());
    }

    @Test
    public void TestFriendAccepting() throws Exception{
        User testFriend = new User();
        testFriend.setEmail("randomx");
        testFriend.setUsername("Thom");
        testFriend.setToken("aToken");
        testFriend.setPassword("b");
        testFriend.setUserId(5L);

        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(2L);


        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        doNothing().when(userService).acceptFriendRequest(Mockito.any(), Mockito.any());

        // when - Input is given
        MockHttpServletRequestBuilder putRequest = put("/friends/acceptRequest")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testFriend.getUserId()));

        // then - check the output
        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(userService).acceptFriendRequest(testUser1, testFriend.getUserId());
    }

    @Test
    public void TestFriendRejection() throws Exception{
        User testFriend = new User();
        testFriend.setEmail("randomx");
        testFriend.setUsername("Thom");
        testFriend.setToken("aToken");
        testFriend.setPassword("b");
        testFriend.setUserId(5L);

        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(2L);


        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        doNothing().when(userService).rejectFriendRequest(Mockito.any(),Mockito.any());

        // when - Input is given
        MockHttpServletRequestBuilder putRequest = put("/friends/rejectRequest")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testFriend.getUserId()));

        // then - check the output
        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(userService).rejectFriendRequest(testUser1, testFriend.getUserId());
    }

    private String asJsonString ( final Object object){
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}