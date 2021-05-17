package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;



public class UserServiceFriendsIntegrationTest {



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

    @Test
    public void sendFriendRequest(){


        userService.sendFriendRequest(user1, user2.getUsername());

        Assertions.assertTrue(user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(user2.getIncomingFriendRequests().contains(user1.getUserId()));

    }


    @Test
    public void acceptFriendRequest(){



        userService.sendFriendRequest(user1, user2.getUsername());
        userService.acceptFriendRequest(user2, user1.getUserId());

        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(user1.getFriends().contains(user2.getUserId()));


    }

    @Test
    public void rejectFriendRequest(){



        userService.sendFriendRequest(user1, user2.getUsername());
        userService.rejectFriendRequest(user2, user1.getUserId());


        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(!user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getFriends().contains(user2.getUserId()));

    }

    @Test
    public void removeFriendRequest(){




        userService.sendFriendRequest(user1, user2.getUsername());
        userService.removeFriendRequest(user1, user2.getUserId());


        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(!user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getFriends().contains(user2.getUserId()));

    }

    @Test
    public void removeFriend(){


        userService.sendFriendRequest(user1, user2.getUsername());
        userService.acceptFriendRequest(user2, user1.getUserId());
        userService.removeFriend(user1, user2.getUserId());


        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(!user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getFriends().contains(user2.getUserId()));

    }


    @Test
    public void errorCannotAddNonexistent(){
        assertThrows(ResponseStatusException.class, ()->userService.sendFriendRequest(user2, "nonexistantname"));
        assertThrows(ResponseStatusException.class, ()->userService.acceptFriendRequest(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.rejectFriendRequest(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriend(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriendRequest(user2, 3L));
    }

    @Test
    public void errorCannotAddYourself(){
        assertThrows(ResponseStatusException.class, ()->userService.sendFriendRequest(user2, user2.getUsername()));
        assertThrows(ResponseStatusException.class, ()->userService.acceptFriendRequest(user2, user2.getUserId()));
        assertThrows(ResponseStatusException.class, ()->userService.rejectFriendRequest(user2, user2.getUserId()));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriend(user2, user2.getUserId()));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriendRequest(user2, user2.getUserId()));
    }

    @Test
    public void errorFriendAlreadyExists(){
        user1.addFriend(user2.getUserId());
        assertThrows(ResponseStatusException.class, ()->userService.acceptFriendRequest(user1, user2.getUserId()));
    }


    @Test
    public void errorIncomingAlreadyExists(){
        user1.addIncomingFriendRequest(user2.getUserId());
        assertThrows(ResponseStatusException.class, ()->userService.sendFriendRequest(user1, user2.getUsername()));
    }

    @Test
    public void errorOutgoingAlreadyExists(){
        user1.addOutgoingFriendRequest(user2.getUserId());
        assertThrows(ResponseStatusException.class, ()->userService.sendFriendRequest(user1, user2.getUsername()));
    }

    // removal error tests
    @Test
    public void errorIncomingNonexistent(){
        assertThrows(ResponseStatusException.class, ()->userService.rejectFriendRequest(user1, user2.getUserId()));
    }

    @Test
    public void errorOutgoingNonexistent(){
        user1.addIncomingFriendRequest(user2.getUserId());
        assertThrows(ResponseStatusException.class, ()->userService.rejectFriendRequest(user1, user2.getUserId()));
    }

    @Test
    public void errorFriendNotInList(){
        assertThrows(ResponseStatusException.class, ()->userService.removeFriend(user1, user2.getUserId()));
    }

    @Test
    public void errorTesting(){
        assertThrows(ResponseStatusException.class, ()->userService.acceptFriendRequest(user2, user1.getUserId()));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriendRequest(user2, user1.getUserId()));

        assertThrows(ResponseStatusException.class, ()->userService.acceptFriendRequest(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.rejectFriendRequest(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriendRequest(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriend(user2, 3L));

    }


}
