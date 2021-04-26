package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("messageRepository")
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByMessageId(Long messageId);
    List<Message> findAllByMessageChannel(MessageChannel messageChannel);

}
