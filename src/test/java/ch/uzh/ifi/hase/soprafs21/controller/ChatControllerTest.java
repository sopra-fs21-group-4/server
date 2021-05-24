package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.MessageChannelService;
import ch.uzh.ifi.hase.soprafs21.service.MessageService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        given(messageChannelService.createMessageChannel(Mockito.any())).willReturn(newMessageChannel);

        MockHttpServletRequestBuilder postRequest = post("/chat/create")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }







}
