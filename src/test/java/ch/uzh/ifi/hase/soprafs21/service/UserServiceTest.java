package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testUser2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setPassword("testpw");
        testUser.setUsername("testUsername");
        testUser.setToken("TestToken");
        testUser.setStatus(UserStatus.PLAYING);

        testUser2 = new User();
        testUser2.setUserId(2L);
        testUser2.setPassword("testpw2");
        testUser2.setUsername("testUsername2");
        testUser2.setToken("DifferentToken");
        testUser2.setStatus(UserStatus.PLAYING);

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findByUserId(1L)).thenReturn(testUser);
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getUserId(), createdUser.getUserId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.IDLE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void loginUser_throwsException(){
        // given -> a first user has already been created
        userService.createUser(testUser);
        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then test for thrown error
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser2));
    }

    @Test
    public void loginUser_success(){
        // given -> a first user has already been created
        userService.createUser(testUser);
        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        // then test for thrown error
        assertEquals(testUser, userService.loginUser(testUser));
    }

    @Test
    public void putSubscriber(){
        userService.putSubscriber(1L, new SseEmitter());
        assertEquals(UserStatus.IDLE, testUser.getStatus());
    }

    @Test
    public void removeSubscriber(){
        // putting a new subscriber
        userService.putSubscriber(1L, new SseEmitter());
        // checking if we can remove subscriber
        assertDoesNotThrow(()->userService.removeSubscriber(1L));
    }

    @Test
    public void verifyUser_success(){
        assertEquals(testUser, userService.verifyUser(testUser.getUserId(), testUser.getToken()));
    }

    @Test
    public void verifyUserUnsuccessfulDueToUserNull(){
        Mockito.when(userRepository.findByUserId(1L)).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> userService.verifyUser(testUser.getUserId(), testUser.getToken()));
    }

    @Test
    public void verifyUserUnsuccessfulDueToInvalidToken(){
        assertThrows(ResponseStatusException.class, () -> userService.verifyUser(testUser.getUserId(), testUser2.getToken()));
    }

    @Test
    public void verifyUserUnsuccessfulDueToOfflineUserStatus(){
        User testUser3 = new User();
        testUser3.setUserId(3L);
        testUser3.setPassword("testpw3");
        testUser3.setUsername("testUsername3");
        testUser3.setToken("DifferentToken3");
        testUser3.setStatus(UserStatus.OFFLINE);

        Mockito.when(userRepository.findByUserId(1L)).thenReturn(testUser3);
        assertThrows(ResponseStatusException.class, () -> userService.verifyUser(testUser3.getUserId(), testUser3.getToken()));
    }

    @Test
    public void verifyUsernameNotNull(){
        User testUser4 = new User();
        testUser4.setUserId(4L);
        testUser4.setPassword("testpw");
        testUser4.setToken("DifferentToken");
        testUser4.setStatus(UserStatus.IDLE);
        testUser4.setUsername(null);
        assertThrows(ResponseStatusException.class, () -> userService.checkUsernameConstraints(testUser4.getUsername()));
        testUser4.setUsername("admin");
        assertThrows(ResponseStatusException.class, () -> userService.checkUsernameConstraints(testUser4.getUsername()));
        testUser4.setUsername("adm,in");
        assertThrows(ResponseStatusException.class, () -> userService.checkUsernameConstraints(testUser4.getUsername()));
        testUser4.setUsername("AnyUsername");
        Mockito.when(userRepository.findByUsername(testUser4.getUsername())).thenReturn(testUser4);
        assertThrows(ResponseStatusException.class, () -> userService.checkUsernameConstraints(testUser4.getUsername()));
    }


    @Test
    public void verifyPasswordConstraintsFormatNotValid(){
        User testUser3 = new User();
        testUser3.setPassword(null);
        assertThrows(ResponseStatusException.class, () -> userService.checkPasswordConstraints(testUser3.getPassword()));
        testUser3.setPassword("");
        assertThrows(ResponseStatusException.class, () -> userService.checkPasswordConstraints(testUser3.getPassword()));
    }



    @Test
    public void verifyEmailConstraintsFormatNotValid(){
        User testUser3 = new User();
        testUser3.setEmail("abab@ab@asdf");
        assertThrows(ResponseStatusException.class, () -> userService.checkEmailConstraints(testUser3.getEmail()));
        testUser3.setEmail("ab@cd");
        assertThrows(ResponseStatusException.class, () -> userService.checkEmailConstraints(testUser3.getEmail()));
        testUser3.setEmail("ab@cd.");
        assertThrows(ResponseStatusException.class, () -> userService.checkEmailConstraints(testUser3.getEmail()));
    }



}
