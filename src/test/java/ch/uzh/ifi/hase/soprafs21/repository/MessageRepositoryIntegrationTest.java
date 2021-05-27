package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * testing if the messageRepository properly stores messages and if they can be found by the methods
 */
@DataJpaTest
public class MessageRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;


    @Qualifier("messageRepository")
    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void findByMessageId_success() {

        // given
        User user = new User();

        user.setUsername("firstname@lastname");
        user.setToken("1");
        user.setEmail("firstname@lastname");
        user.setStatus(UserStatus.IDLE);
        user.setPassword("pw");

        MessageChannel messageChannel = new MessageChannel();

        Message message = new Message();

        message.setText("test");
        message.setTimestamp(2l);
        message.setSenderId(user.getUserId());
        messageChannel.addMessage(message);

        entityManager.persist(user);
        entityManager.persist(message);
        entityManager.persist(messageChannel);
        entityManager.flush();

        // when
        Message found = messageRepository.findByMessageId(message.getMessageId());

        // then
        assertNotNull(found.getMessageId());
        assertEquals(found.getText(), message.getText());
        assertEquals(found.getTimestamp(), message.getTimestamp());
        assertEquals(found.getSenderId(), message.getSenderId());

    }
    

//    @Test
    public void findAllByMessageChannelId_success() {
        // given
        User user = new User();

        user.setUsername("firstname@lastname");
        user.setToken("1");
        user.setEmail("firstname@lastname");
        user.setStatus(UserStatus.IDLE);
        user.setPassword("pw");

        MessageChannel messageChannel = new MessageChannel();

        Message message = new Message();

        message.setText("test");
        message.setTimestamp(2l);
        message.setSenderId(user.getUserId());
        messageChannel.addMessage(message);

        Message message2 = new Message();

        message2.setText("test");
        message2.setTimestamp(2l);
        message.setSenderId(user.getUserId());
        messageChannel.addMessage(message2);

        Message message3 = new Message();

        message3.setText("test");
        message3.setTimestamp(2l);
        message.setSenderId(user.getUserId());
        messageChannel.addMessage(message3);

        entityManager.persist(user);
        entityManager.persist(messageChannel);
        entityManager.persist(message);
        entityManager.persist(message2);
        entityManager.persist(message3);
        entityManager.flush();

        List<Message> expected = new ArrayList<Message>();
        expected.add(message);
        expected.add(message2);
        expected.add(message3);


        // TODO messages don't know their messageChannel anymore => move this test to MessageChannelRepositoryIntegrationTest
//        // when
//        List<Message> found = messageRepository.findAllByMessageChannel(messageChannel);
//
//        // then
//        assertNotNull(found);
//        assertEquals(found, expected);

    }


}
