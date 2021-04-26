package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
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
 * testing if the messageChannelRepository properly stores channels and if they can be found by the methods
 */
@DataJpaTest
public class MessageChannelRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;


    @Qualifier("messageChannelRepository")
    @Autowired
    private MessageChannelRepository messageChannelRepository;

    @Test
    public void findByMessageChannelId_success() {

        // given
        MessageChannel messageChannel = new MessageChannel();


        entityManager.persist(messageChannel);
        entityManager.flush();

        // when
        MessageChannel found = messageChannelRepository.findByMessageChannelId(messageChannel.getMessageChannelId());

        // then
        assertNotNull(found.getMessageChannelId());
        assertEquals(found.getMessageChannelId(),messageChannel.getMessageChannelId());


    }
    




}
