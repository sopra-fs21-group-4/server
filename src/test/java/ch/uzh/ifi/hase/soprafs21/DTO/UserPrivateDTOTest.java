package ch.uzh.ifi.hase.soprafs21.DTO;


import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class UserPrivateDTOTest {



    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);

        // given
        user1 = new User();
        user1.setUserId(1L);
        user1.setPassword("testPW");
        user1.setUsername("testUsername");
        user1.setEmail("test@test.test");

        user2 = new User();
        user2.setUserId(2L);
        user2.setPassword("testPW2");
        user2.setUsername("testUsername2");
        user2.setEmail("test2@test.test");

        Mockito.when(userRepository.findByUsername(user1.getUsername())).thenReturn(user1);
        Mockito.when(userRepository.findByUsername(user2.getUsername())).thenReturn(user2);

        Mockito.when(userRepository.findByUserId(user1.getUserId())).thenReturn(user1);
        Mockito.when(userRepository.findByUserId(user2.getUserId())).thenReturn(user2);


    }

//    @Test
    public void sendFriendRequest(){


        userService.sendFriendRequest(user1, user2.getUsername());

        Assertions.assertTrue(user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(user2.getIncomingFriendRequests().contains(user1.getUserId()));

    }

//    @Test
    public void errorFriendAlreadyExists(){
        user1.addFriend(user2.getUserId());
        assertThrows(ResponseStatusException.class, ()->userService.acceptFriendRequest(user1, user2.getUserId()));
    }



}
