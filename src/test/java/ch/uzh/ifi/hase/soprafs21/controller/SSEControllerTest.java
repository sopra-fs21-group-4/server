package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.EntityDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserDTO;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;


import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SSEController.class)
public class SSEControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


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


    private String asJsonString ( final Object object){
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }

}