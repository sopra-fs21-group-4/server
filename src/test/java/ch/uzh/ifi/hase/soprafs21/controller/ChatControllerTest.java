package ch.uzh.ifi.hase.soprafs21.controller;

import antlr.debug.MessageAdapter;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.MessageGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.MessagePostDTO;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.MessageChannelService;
import ch.uzh.ifi.hase.soprafs21.service.MessageService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageChannelService messageChannelService;

    @MockBean
    private MessageService messageService;

    @Test
    public void TestMessageChannelCreation() throws Exception{
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        MessageChannel newMessageChannel = new MessageChannel();
        newMessageChannel.setAssociatedGameId(1L);
        newMessageChannel.setMessageChannelId(5L);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        given(messageChannelService.createMessageChannel(Mockito.any())).willReturn(newMessageChannel);


        MockHttpServletRequestBuilder postRequest = post("/chat/create")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }

    @Test
    public void TestGetMessages() throws Exception{
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        User sender1 = new User();
        sender1.setStatus(UserStatus.IDLE);
        sender1.setEmail("firstname@lastname1");
        sender1.setUserId(2L);
        sender1.setUsername("Thoma");
        sender1.setPassword("somePassworde");
        sender1.setToken("someTokenn");
        sender1.setCurrentGameId(2L);

        User sender2 = new User();
        sender2.setStatus(UserStatus.IDLE);
        sender2.setEmail("firstname@lastname2");
        sender2.setUserId(3L);
        sender2.setUsername("Thomasn");
        sender2.setPassword("somePassworden");
        sender2.setToken("someTokennn");
        sender2.setCurrentGameId(2L);

        MessageChannel newMessageChannel = new MessageChannel();
        newMessageChannel.setAssociatedGameId(1L);
        newMessageChannel.setMessageChannelId(5L);

        Message Message1 = new Message();
        Message1.setMessageChannel(newMessageChannel);
        Message1.setText("Test Message 1");
        Message1.setSender(sender1);
        Message1.setTimestamp(20L);
        Message1.setMessageId(11L);

        Message Message2 = new Message();
        Message2.setMessageChannel(newMessageChannel);
        Message2.setText("Test Message 2");
        Message2.setSender(sender2);
        Message2.setTimestamp(25L);
        Message2.setMessageId(10L);

        List<Message> messageList = new ArrayList<>();
        messageList.add(Message1);
        messageList.add(Message2);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        given(messageChannelService.verifyReader(Mockito.any(), Mockito.any())).willReturn(newMessageChannel);
        given(messageService.getMessages(Mockito.any())).willReturn(messageList);

        MockHttpServletRequestBuilder getRequest = get("/chat/" + newMessageChannel.getMessageChannelId())
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                                    .andExpect(jsonPath("$[0].messageId", is(Message1.getMessageId().intValue())))
                                    .andExpect(jsonPath("$[0].username", is(sender1.getUsername())))
                                    .andExpect(jsonPath("$[1].messageId", is(Message2.getMessageId().intValue())))
                                    .andExpect(jsonPath("$[1].username", is(sender2.getUsername())))
                                    .andExpect(jsonPath("$[0].messageChannelId", is(newMessageChannel.getMessageChannelId().intValue())));
    }

    @Test
    public void TestPostMessage() throws Exception{
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        MessageChannel newMessageChannel = new MessageChannel();
        newMessageChannel.setAssociatedGameId(1L);
        newMessageChannel.setMessageChannelId(5L);

        Message newMessage = new Message();
        newMessage.setMessageChannel(newMessageChannel);
        newMessage.setText("Test Message 1");
        newMessage.setSender(user);
        newMessage.setTimestamp(20L);
        newMessage.setMessageId(11L);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        given(messageChannelService.verifySender(Mockito.any(),Mockito.any())).willReturn(newMessageChannel);
        given(messageService.postMessage(Mockito.any(),Mockito.any(), Mockito.any())).willReturn(newMessage);

        MessagePostDTO messagePostDTO = new MessagePostDTO();
        messagePostDTO.setText("Test for messagePostDTO");

        MockHttpServletRequestBuilder postRequest = post("/chat/" + newMessageChannel.getMessageChannelId())
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(messagePostDTO));

        mockMvc.perform(postRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId", is(newMessage.getMessageId().intValue())))
                .andExpect(jsonPath("$.timestamp", is(newMessage.getTimestamp().intValue())))
                .andExpect(jsonPath("$.text", is(newMessage.getText())));
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
