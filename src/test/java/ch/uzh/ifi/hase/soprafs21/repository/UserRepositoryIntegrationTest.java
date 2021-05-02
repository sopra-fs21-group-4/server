package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * testing if the userRepository properly stores users and if they can be found by the methods
 */

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;


    @Test
    public void findByUsername_success() {
        // given
        User user = new User();

        user.setUsername("firstname@lastname");
        user.setToken("1");
        user.setEmail("firstname@lastname");
        user.setStatus(UserStatus.IDLE);
        user.setPassword("pw");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername(user.getUsername());

        // then
        assertNotNull(found.getUserId());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
        assertEquals(found.getEmail(), user.getEmail());
        assertEquals(found.getStatus(), user.getStatus());
        assertEquals(found.getPassword(), user.getPassword());
    }


    @Test
    public void findByUserId_success() {
        // given
        User user = new User();

        user.setUsername("firstname@lastname");
        user.setToken("1");
        user.setEmail("firstname@lastname");
        user.setStatus(UserStatus.IDLE);
        user.setPassword("pw");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUserId(user.getUserId());

        // then
        assertNotNull(found.getUserId());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
        assertEquals(found.getEmail(), user.getEmail());
        assertEquals(found.getStatus(), user.getStatus());
        assertEquals(found.getPassword(), user.getPassword());
    }


}
