package ch.uzh.ifi.hase.soprafs21.service;


import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceFriendsIntegrationTest {

//    @Qualifier("userRepository")
//    @Autowired
//    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;


    @Test
    @Transactional
    public void sendFriendRequest(){

        // given
        user1 = new User();
        user1.setUserId(1L);
        user1.setPassword("testPW");
        user1.setUsername("testUsername");
        user1.setEmail("test@test.test");

        user1 = userService.createUser(user1);

        user2 = new User();
        user2.setUserId(2L);
        user2.setPassword("testPW2");
        user2.setUsername("testUsername2");
        user2.setEmail("test2@test.test");

        user2 = userService.createUser(user2);

        userService.sendFriendRequest(user1, user2.getUsername());

        Assertions.assertTrue(user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(user2.getIncomingFriendRequests().contains(user1.getUserId()));

    }


    @Test
    @Transactional
    public void acceptFriendRequest(){

        // given
        user1 = new User();
        user1.setUserId(1L);
        user1.setPassword("testPW");
        user1.setUsername("testUsername");
        user1.setEmail("test@test.test");

        user1 = userService.createUser(user1);

        user2 = new User();
        user2.setUserId(2L);
        user2.setPassword("testPW2");
        user2.setUsername("testUsername2");
        user2.setEmail("test2@test.test");

        user2 = userService.createUser(user2);


        userService.sendFriendRequest(user1, user2.getUsername());
        userService.acceptFriendRequest(user2, user1.getUserId());

        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(user1.getFriends().contains(user2.getUserId()));


    }

    @Test
    @Transactional
    public void rejectFriendRequest(){


        // given
        user1 = new User();
        user1.setUserId(1L);
        user1.setPassword("testPW");
        user1.setUsername("testUsername");
        user1.setEmail("test@test.test");

        user1 = userService.createUser(user1);

        user2 = new User();
        user2.setUserId(2L);
        user2.setPassword("testPW2");
        user2.setUsername("testUsername2");
        user2.setEmail("test2@test.test");

        user2 = userService.createUser(user2);

        userService.sendFriendRequest(user1, user2.getUsername());
        userService.rejectFriendRequest(user2, user1.getUserId());


        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(!user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getFriends().contains(user2.getUserId()));

    }

    @Test
    @Transactional
    public void removeFriendRequest(){



        // given
        user1 = new User();
        user1.setUserId(1L);
        user1.setPassword("testPW");
        user1.setUsername("testUsername");
        user1.setEmail("test@test.test");

        user1 = userService.createUser(user1);

        user2 = new User();
        user2.setUserId(2L);
        user2.setPassword("testPW2");
        user2.setUsername("testUsername2");
        user2.setEmail("test2@test.test");

        user2 = userService.createUser(user2);


        userService.sendFriendRequest(user1, user2.getUsername());
        userService.removeFriendRequest(user1, user2.getUserId());


        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(!user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getFriends().contains(user2.getUserId()));

    }

    @Test
    @Transactional
    public void removeFriend(){

        // given
        user1 = new User();
        user1.setUserId(1L);
        user1.setPassword("testPW");
        user1.setUsername("testUsername");
        user1.setEmail("test@test.test");

        user1 = userService.createUser(user1);

        user2 = new User();
        user2.setUserId(2L);
        user2.setPassword("testPW2");
        user2.setUsername("testUsername2");
        user2.setEmail("test2@test.test");

        user2 = userService.createUser(user2);



        userService.sendFriendRequest(user1, user2.getUsername());

        userService.acceptFriendRequest(user2, user1.getUserId());

        userService.removeFriend(user1, user2.getUserId());


        Assertions.assertTrue(!user2.getIncomingFriendRequests().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getOutgoingFriendRequests().contains(user2.getUserId()));
        Assertions.assertTrue(!user2.getFriends().contains(user1.getUserId()));
        Assertions.assertTrue(!user1.getFriends().contains(user2.getUserId()));

    }

    @Test
    @Transactional
    public void errorTesting(){

        // given
        user1 = new User();
        user1.setUserId(1L);
        user1.setPassword("testPW");
        user1.setUsername("testUsername");
        user1.setEmail("test@test.test");

        user1 = userService.createUser(user1);

        user2 = new User();
        user2.setUserId(2L);
        user2.setPassword("testPW2");
        user2.setUsername("testUsername2");
        user2.setEmail("test2@test.test");

        user2 = userService.createUser(user2);

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
