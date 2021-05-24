package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void updateUsername_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        testUser.setEmail("test@doyouevenmeme.com");
        testUser.setToken("token");
        testUser.setStatus(UserStatus.IDLE);

        User updateEntity = new User();
        updateEntity.setUsername("newname");

        // when
        User createdUser = userService.createUser(testUser);
        userService.updateUser(testUser, updateEntity);

        // then
        assertEquals(testUser.getUserId(), createdUser.getUserId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
    }



    @Test
    public void updatePassword_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        testUser.setEmail("test@doyouevenmeme.com");
        testUser.setToken("token");
        testUser.setStatus(UserStatus.IDLE);

        User updateEntity = new User();
        updateEntity.setPassword("newpw");

        // when
        User createdUser = userService.createUser(testUser);
        userService.updateUser(testUser, updateEntity);

        // then
        assertEquals(testUser.getUserId(), createdUser.getUserId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
    }

    @Test
    public void updateEmail_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        testUser.setEmail("test@doyouevenmeme.com");
        testUser.setToken("token");
        testUser.setStatus(UserStatus.IDLE);

        User updateEntity = new User();
        updateEntity.setEmail("new@doyouevenmeme.com");

        // when
        User createdUser = userService.createUser(testUser);
        userService.updateUser(testUser, updateEntity);

        // then
        assertEquals(testUser.getUserId(), createdUser.getUserId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
    }

//     @Test
//    public void test() {
//        assertNull(userRepository.findByUsername("testUsername"));
//
//        User testUser = new User();
//        testUser.setPassword("testName");
//        testUser.setUsername("testUsername");
//        User createdUser = userService.createUser(testUser);
//
//        // attempt to create second user with same username
//        User testUser2 = new User();
//
//        // change the name but forget about the username
//        testUser2.setPassword("testName2");
//        testUser2.setUsername("testUsername");
//
//        // check that an error is thrown
//        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
//    }
}
