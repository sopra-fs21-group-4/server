package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameSettingsRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;

import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.*;

/**
 * Test class for the MessageService
 *
 */

public class ChatServiesTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private MessageChannelRepository messageChannelRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    private MessageChannel messageChannel_test = new MessageChannel();
    private User gameMaster;
    private User player1;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        gameMaster = new User();
        gameMaster.setUserId(1L);
        gameMaster.setPassword("testPW");
        gameMaster.setUsername("testUser");

        player1 = new User();
        player1.setUserId(2L);
        player1.setPassword("testPW2");
        player1.setUsername("testUser2");

    }

    @Test
    void createMessageChannel_Test() {
        MessageChannel messageChannel = new MessageChannel();
        Mockito.when(messageChannelRepository.save(Mockito.any())).thenReturn(messageChannel);
        ChatService chatService = new ChatService(messageChannelRepository, messageRepository, userRepository);
        assertEquals(messageChannel, chatService.createMessageChannel(gameMaster.getUserId()));
    }

    @Test
    void getMessageChannel_Test() {
        MessageChannel messageChannel = new MessageChannel();
        Mockito.when(messageChannelRepository.save(Mockito.any())).thenReturn(messageChannel);
        Mockito.when(messageChannelRepository.existsById(Mockito.any())).thenReturn(true);
        Mockito.when(messageChannelRepository.findByMessageChannelId(Mockito.any())).thenReturn(messageChannel);
        ChatService chatService = new ChatService(messageChannelRepository, messageRepository, userRepository);
        chatService.createMessageChannel(gameMaster.getUserId());
        assertEquals(messageChannel, chatService.getMessageChannel(messageChannel.getMessageChannelId()));
    }

    @Test
    void verifyReaderTestPos() {
        // given
        MessageChannel messageChannel1 = new MessageChannel();
        MessageChannel messageChannel = Mockito.spy(messageChannel1);
        messageChannel.setMessageChannelId(1L);
        ChatService chatService = new ChatService(messageChannelRepository, messageRepository, userRepository);
        Mockito.when(messageChannelRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(messageChannelRepository.findByMessageChannelId(Mockito.anyLong())).thenReturn(messageChannel);
        Mockito.when(messageChannel.verifyParticipant(Mockito.any())).thenReturn(true);

        assertEquals(messageChannel,
                chatService.verifyReader(messageChannel.getMessageChannelId(), gameMaster.getUserId()));
    }

    /**
     * @Test void verifyReaderTestNeg() { MessageChannel messageChannel = new
     *       MessageChannel();
     *       Mockito.when(messageChannelRepository.findByMessageChannelId(Mockito.anyLong())).thenReturn(messageChannel);
     *       Mockito.when(messageChannel.verifyParticipant(Mockito.anyLong())).thenThrow(ResponseStatusException.HttpStatus.UNAUTHORIZED));//,
     *       "this is a private channel"))); }
     */
}
