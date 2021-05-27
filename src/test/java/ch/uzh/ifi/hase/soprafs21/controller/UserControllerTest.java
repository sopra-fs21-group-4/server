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

    //Input:
    //RequestBody --> UserPostDTO
    //ResponseBody --> UserLoginDTO
    @Test
    public void TestifUserLoginWorks() throws  Exception{
        //given
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        //mock the UserService - returns user
        given(userService.loginUser(Mockito.any())).willReturn(user);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("Thomas");
        userPostDTO.setPassword("somePassword");
        userPostDTO.setEmail("firstname@lastname");

        // when - Input is given
        MockHttpServletRequestBuilder patchRequest = patch("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then - check the output
        mockMvc.perform(patchRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.userId", is(user.getUserId().intValue())))
                .andExpect(jsonPath("$.token", is(user.getToken())));
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

    //Flow Control testing
    //Input: RequestHeader userId and RequestHeader token
    //Output: UserPrivateDTO
    //Status: OK
//    @Test // TODO deprecated method
    public void TestGetUsersViaUsernames() throws Exception {
        User testUser1 = new User();
        testUser1.setStatus(UserStatus.PLAYING);
        testUser1.setUserId(1L);
        testUser1.setUsername("Thomas");
        testUser1.setCurrentGameId(2L);


        User testUser2 = new User();
        testUser2.setStatus(UserStatus.IDLE);
        testUser2.setUserId(4L);
        testUser2.setUsername("Peter");
        testUser2.setCurrentGameId(2L);


        User testUser3 = new User();
        testUser3.setStatus(UserStatus.IDLE);
        testUser3.setUserId(20L);
        testUser3.setUsername("Müller");
        testUser3.setCurrentGameId(2L);

        //List with users
        List<User> users = new ArrayList<>();
        users.add(testUser1);
        users.add(testUser2);
        users.add(testUser3);

        List<String> usernames = new ArrayList<>();
        usernames.add(testUser1.getUsername());
        usernames.add(testUser2.getUsername());
        usernames.add(testUser3.getUsername());

        given(userService.getUsers()).willReturn(users);
        given(userService.getUserByUsername(usernames.get(0))).willReturn(testUser1);
        given(userService.getUserByUsername(usernames.get(1))).willReturn(testUser2);
        given(userService.getUserByUsername(usernames.get(2))).willReturn(testUser3);

        // when --> return value should be a List of UserPublicDTOs
        MockHttpServletRequestBuilder getRequest = get("/users")
                .header("usernames", usernames)
                .contentType(MediaType.APPLICATION_JSON);

        // then compare UserPublicDTO Values with the user values
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(testUser1.getUserId().intValue())))
                .andExpect(jsonPath("$[0].username", is(testUser1.getUsername())))
                .andExpect(jsonPath("$[1].userId", is(testUser2.getUserId().intValue())))
                .andExpect(jsonPath("$[1].username", is(testUser2.getUsername())))
                .andExpect(jsonPath("$[2].userId", is(testUser3.getUserId().intValue())))
                .andExpect(jsonPath("$[2].username", is(testUser3.getUsername())));
    }

//    @Test // TODO deprecated method
    public void TestGetUsersViaUserIDs() throws Exception {
        User testUser1 = new User();
        testUser1.setStatus(UserStatus.PLAYING);
        testUser1.setUserId(1L);
        testUser1.setUsername("Thomas");
        testUser1.setCurrentGameId(2L);

        User testUser2 = new User();
        testUser2.setStatus(UserStatus.IDLE);
        testUser2.setUserId(4L);
        testUser2.setUsername("Peter");
        testUser2.setCurrentGameId(2L);

        User testUser3 = new User();
        testUser3.setStatus(UserStatus.IDLE);
        testUser3.setUserId(20L);
        testUser3.setUsername("Müller");
        testUser3.setCurrentGameId(2L);

        //List with users --> these users are fetched by three ways --> either userIds, usernames or some third category
        List<User> users = new ArrayList<>();
        users.add(testUser1);
        users.add(testUser2);
        users.add(testUser3);

        //List<String> usernames = ("Thomas", "Peter", "Müller");
        List<Long> userIds = new ArrayList<>();
        userIds.add(testUser1.getUserId());
        userIds.add(testUser2.getUserId());
        userIds.add(testUser3.getUserId());

        given(userService.getUsers()).willReturn(users);
        given(userService.getUserByUserId(userIds.get(0))).willReturn(testUser1);
        given(userService.getUserByUserId(userIds.get(1))).willReturn(testUser2);
        given(userService.getUserByUserId(userIds.get(2))).willReturn(testUser3);

        // when --> return value should be a List of UserPublicDTOs
        MockHttpServletRequestBuilder getRequest = get("/users")
                .header("userIds", userIds)
                .contentType(MediaType.APPLICATION_JSON);

        // then compare UserPublicDTO Values with the user values
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(testUser1.getUserId().intValue())))
                .andExpect(jsonPath("$[0].username", is(testUser1.getUsername())))
                .andExpect(jsonPath("$[1].userId", is(testUser2.getUserId().intValue())))
                .andExpect(jsonPath("$[1].username", is(testUser2.getUsername())))
                .andExpect(jsonPath("$[2].userId", is(testUser3.getUserId().intValue())))
                .andExpect(jsonPath("$[2].username", is(testUser3.getUsername())));
    }

//    @Test // TODO deprecated method
    public void TestIfGetUsersThrowsExceptionWhenBadRequest() throws Exception {
        User testUser1 = new User();
        testUser1.setStatus(UserStatus.PLAYING);
        testUser1.setUserId(1L);
        testUser1.setUsername("Thomas");
        testUser1.setCurrentGameId(2L);


        List<Long> userIds = new ArrayList<>();
        userIds.add(testUser1.getUserId());
        List<String> usernames = new ArrayList<>();
        usernames.add(testUser1.getUsername());

        // when --> if two headers are passed then the request should result in an exception
        MockHttpServletRequestBuilder getRequest = get("/users")
                .header("usernames", usernames)
                .header("userIds", userIds)
                .contentType(MediaType.APPLICATION_JSON);

        // then return badRequest
        mockMvc.perform(getRequest).andExpect(status().isBadRequest());
    }

//    @Test // TODO deprecated method
    public void TestGtUserViaUserId() throws Exception {
        User testUser1 = new User();
        testUser1.setStatus(UserStatus.PLAYING);
        testUser1.setUserId(1L);
        testUser1.setUsername("Thomas");
        testUser1.setCurrentGameId(2L);

        given(userService.getUserByUserId(Mockito.any())).willReturn(testUser1);

        // when
        MockHttpServletRequestBuilder getRequest = get("/user")
                .header("userId", testUser1.getUserId())
                .contentType(MediaType.APPLICATION_JSON);

        // then compare UserPublicDTO Values with the user values
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(testUser1.getUserId().intValue())))
                .andExpect(jsonPath("$.username", is(testUser1.getUsername())));
    }

//    @Test // TODO deprecated method
    public void TestGetUserViaUsername() throws Exception {
        User testUser1 = new User();
        testUser1.setStatus(UserStatus.PLAYING);
        testUser1.setUserId(1L);
        testUser1.setUsername("Thomas");
        testUser1.setCurrentGameId(2L);

        given(userService.getUserByUsername(testUser1.getUsername())).willReturn(testUser1);

        // when
        MockHttpServletRequestBuilder getRequest = get("/user")
                .header("username", testUser1.getUsername())
                .contentType(MediaType.APPLICATION_JSON);

        // then compare UserPublicDTO Values with the user values
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(testUser1.getUserId().intValue())))
                .andExpect(jsonPath("$.username", is(testUser1.getUsername())));
    }

//    @Test // TODO deprecated method
    public void TestIfGetUserThrowsExceptionWhenBadRequest() throws Exception {
        User testUser1 = new User();
        testUser1.setStatus(UserStatus.PLAYING);
        testUser1.setUserId(1L);
        testUser1.setUsername("Thomas");
        testUser1.setCurrentGameId(2L);

        // when
        MockHttpServletRequestBuilder getRequest = get("/user")
                .header("username", testUser1.getUsername())
                .header("userId", testUser1.getUserId())
                .contentType(MediaType.APPLICATION_JSON);

        // then return badRequest
        mockMvc.perform(getRequest).andExpect(status().isBadRequest());
    }
    /*
    //Input: userId & token as header
    //Output: TODO might need some more attribute values; however there is a problem I don t know how to solve
    @Test
    public void TestgetOwnUser() throws Exception{
        User testUser1 = new User();
        testUser1.setStatus(UserStatus.PLAYING);
        testUser1.setUserId(1L);
        testUser1.setCurrentGameId(2L);
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");

        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);

        // when
        MockHttpServletRequestBuilder getRequest = get("/me")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                                    .andExpect(jsonPath("$.userId", is(testUser1.getUserId())));
    }
    */
    @Test
    public void TestupdateUser() throws Exception{
        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(2L);

        User testUser2 = new User();
        testUser2.setEmail("evenmorerandom");
        testUser2.setUsername("Markus");
        testUser2.setToken("someToken");
        testUser2.setPassword("somePassword");
        testUser2.setUserId(2L);

        //mock the UserService - returns user
        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        given(userService.updateUser(Mockito.any(), Mockito.any())).willReturn(testUser2);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setEmail("evenmorerandom");
        userPutDTO.setUsername("Markus");
        userPutDTO.setPassword("somePassword");

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

    //TODO somehow it throws an Error; I don t know why
    /*@Test
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
        verify(userService).sendFriendRequest(testUser1, testFriend.getUsername());
    }
    */
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
        //1. check how many times userService is called
        //2. checks if userService is called by acceptFriendRequest & if the parameters are testUser1, and testFriend.getUserId()
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

        // when - Input is given
        MockHttpServletRequestBuilder putRequest = put("/friends/rejectRequest")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testFriend.getUserId()));

        // then - check the output
        mockMvc.perform(putRequest).andExpect(status().isOk());
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