package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
//@WebAppConfiguration
@SpringBootTest
//@RunWith(SpringRunner.class)
public class UserServiceFriendsIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        // given
        user1 = new User();
        user1.setUserId(1L);
        user1.setPassword("testPW");
        user1.setUsername("testUsername");

        userService.createUser(user1);

        user2 = new User();
        user2.setUserId(2L);
        user2.setPassword("testPW2");
        user2.setUsername("testUsername2");

        userService.createUser(user2);

    }


//    @Test
    public void sendFriendRequest(){

//        System.out.println("test1-");
//        System.out.println(user1.getOutgoingFriendRequests());
//        System.out.println(user1.getIncomingFriendRequests());
//        System.out.println(user2.getOutgoingFriendRequests());
//        System.out.println(user2.getIncomingFriendRequests());

        userService.sendFriendRequest(user1, user2.getUsername());

        System.out.println("test2-");
        System.out.println(user1.getOutgoingFriendRequests());
        System.out.println(user1.getIncomingFriendRequests());
        System.out.println(user2.getOutgoingFriendRequests());
        System.out.println(user2.getIncomingFriendRequests());
        System.out.println(user2.getIncomingFriendRequests().contains(user1.getUserId()));



        Assertions.assertTrue(user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(user2.getIncomingFriendRequests().contains(user1.getUserId()));
//        System.out.println(user1.getOutgoingFriendRequests());
    }


//    @Test
    public void acceptFriendRequest(){

        userService.sendFriendRequest(user1, user2.getUsername());
        userService.acceptFriendRequest(user2, user1.getUserId());

        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(user1.getFriends().contains(user2.getUserId()));


    }

//    @Test
    public void rejectFriendRequest(){

        userService.sendFriendRequest(user1, user2.getUsername());
        userService.rejectFriendRequest(user2, user1.getUserId());

        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(!user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getFriends().contains(user2.getUserId()));

    }

    //    @Test
    public void removeFriendRequest(){

        userService.sendFriendRequest(user1, user2.getUsername());
        userService.removeFriendRequest(user1, user2.getUserId());

        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(!user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getFriends().contains(user2.getUserId()));

    }

//    @Test
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
    public void errorTesting(){

        assertThrows(ResponseStatusException.class, ()->userService.acceptFriendRequest(user2, user1.getUserId()));
        assertThrows(ResponseStatusException.class, ()->userService.rejectFriendRequest(user2, user1.getUserId()));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriendRequest(user2, user1.getUserId()));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriend(user2, user1.getUserId()));

        assertThrows(ResponseStatusException.class, ()->userService.sendFriendRequest(user2, "nonexistantname"));
        assertThrows(ResponseStatusException.class, ()->userService.acceptFriendRequest(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.rejectFriendRequest(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriendRequest(user2, 3L));
        assertThrows(ResponseStatusException.class, ()->userService.removeFriend(user2, 3L));

    }

}
