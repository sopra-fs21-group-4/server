package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Message;
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
        Message message = new Message();

        message.setMessageChannelId(2l);
        message.setText("test");
        message.setTimestamp(2l);
        message.setUserId(1l);


        entityManager.persist(message);
        entityManager.flush();

        // when
        Message found = messageRepository.findByMessageId(message.getMessageId());

        // then
        assertNotNull(found.getMessageId());
        assertEquals(found.getMessageChannelId(), message.getMessageChannelId());
        assertEquals(found.getText(), message.getText());
        assertEquals(found.getTimestamp(), message.getTimestamp());
        assertEquals(found.getUserId(), message.getUserId());

    }
    

    @Test
    public void findAllByMessageChannelId_success() {
        // given

        Message message = new Message();

        message.setMessageChannelId(2l);
        message.setText("test");
        message.setTimestamp(2l);
        message.setUserId(1l);

        Message message2 = new Message();

        message2.setMessageChannelId(2l);
        message2.setText("test");
        message2.setTimestamp(2l);
        message2.setUserId(1l);

        Message message3 = new Message();

        message3.setMessageChannelId(3l);
        message3.setText("test");
        message3.setTimestamp(2l);
        message3.setUserId(1l);

        entityManager.persist(message);
        entityManager.persist(message2);
        entityManager.persist(message3);
        entityManager.flush();


        // when
        List<Message> found = messageRepository.findAllByMessageChannelId(message.getMessageChannelId());

        // then

        List<Message> expected = new ArrayList<Message>();
        expected.add(message);
        expected.add(message2);

        assertNotNull(found);
        assertEquals(found, expected);

    }


}
