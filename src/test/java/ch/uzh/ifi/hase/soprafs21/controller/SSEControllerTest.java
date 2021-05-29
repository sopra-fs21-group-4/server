package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.EntityDTO;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SSEController.class)
public class SSEControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    //all the entites(userId, gameId, ...) have different Ids which are all different


        @Test
        public void TestSubscribeUpdate() throws Exception{
            User user = new User();
            user.setUserId(10L);

            SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
            //mock token
            String emitterToken = "SomeToken";
            //create emitterKEy out of userId and mocked token
            String emitterKey = String.format("%d | %s", user.getUserId(), emitterToken);
            //create map
            Map<String, SseEmitter> unclaimedEmitters = new HashMap<>();
            //put mock in map
            unclaimedEmitters.put(emitterKey, sseEmitter);
            //mock the execution of the executor Service --> verify after the actual mock
            doNothing().when(executorService).execute(Mockito.any());


            //what about onTimeout, onError, onCompletion? --> basically when the emitter is not sending anymore

            //mock the request
            MockHttpServletRequestBuilder getRequest = get("/createEmitter/" + user.getUserId())
                    .header("userId", user.getUserId())
                    .contentType(MediaType.APPLICATION_JSON);

            //how does the return look like; like what is in there if we cannot test for a status
            mockMvc.perform(getRequest).andExpect(status().isOk());
            verify(executorService).execute(Mockito.any());
        }
    }



    @Test
    public void TestActivateEmitterValid() throws Exception{
        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(5L);

        SseEmitter sseEmitter = new SseEmitter();
        Map unclaimedEmitters = Mockito.mock(Map.class);


        String emitterToken = UUID.randomUUID().toString();
        String key = String.format("%d | %s", testUser1.getUserId(), emitterToken);
        //unclaimedEmitters.put(key,sseEmitter);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(testUser1);
        given(unclaimedEmitters.remove(Mockito.any())).willReturn(sseEmitter);
        doNothing().when(userService).putSubscriber(Mockito.any(),Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/activateEmitter")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .content(asJsonString(emitterToken))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(userService).putSubscriber(testUser1.getUserId(),sseEmitter);
    }

    @Test
    public void TestActivateEmitterNotPresent() throws Exception{
        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(5L);

        Map unclaimedEmitters = Mockito.mock(Map.class);

        String emitterToken = UUID.randomUUID().toString();

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(testUser1);
        Mockito.when(unclaimedEmitters.remove(Mockito.any())).thenReturn(null);

        MockHttpServletRequestBuilder putRequest = put("/activateEmitter")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .content(asJsonString(emitterToken))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }

    @Test
    public void TestObserveEntity() throws Exception {
        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(5L);

        Long testEntityId = 5L;

        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        doNothing().when(userService).observeEntity(Mockito.any(),Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/observeEntity")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .content(asJsonString(testEntityId))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(userService).observeEntity(testUser1,testEntityId);
    }

    @Test
    public void TestDisregardEntity() throws Exception{
        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(5L);

        Long testEntityId = 6L;

        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);
        doNothing().when(userService).disregardEntity(Mockito.any(),Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/disregardEntity")
                .header("userId", testUser1.getUserId())
                .header("token", testUser1.getToken())
                .header("entityId", testEntityId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(userService).disregardEntity(testUser1,testEntityId);
    }


    @Test
    public void TestGetEntityValid() throws Exception{
        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(5L);

        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);



    }

    @Test
    public void TestGetEntityInalid() throws Exception{
        User testUser1 = new User();
        testUser1.setEmail("random");
        testUser1.setUsername("Thomas");
        testUser1.setToken("someToken");
        testUser1.setPassword("a");
        testUser1.setUserId(5L);

        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(testUser1);


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
