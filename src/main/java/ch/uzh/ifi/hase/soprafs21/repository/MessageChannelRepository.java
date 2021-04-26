package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("messageChannelRepository")
public interface MessageChannelRepository extends JpaRepository<MessageChannel, Long> {
    MessageChannel findByMessageChannelId(Long messageChannelId);

}
