package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * chat Service
 * This class is the "worker" and responsible for all functionality related to chats
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class MessageChannelService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final MessageChannelRepository messageChannelRepository;

    private ConcurrentMap<Long, Long> syncableIds = new ConcurrentHashMap<Long, Long>();

    @Autowired
    public MessageChannelService(@Qualifier("messageChannelRepository") MessageChannelRepository messageChannelRepository) {
        this.messageChannelRepository = messageChannelRepository;
    }

    public MessageChannel createMessageChannel() {
        MessageChannel messageChannel = messageChannelRepository.save(new MessageChannel());
        messageChannelRepository.flush();
        return messageChannel;
    }

    public MessageChannel getMessageChannel(Long channelId) {
        return messageChannelRepository.findByMessageChannelId(channelId);
    }

    /**
     * checks if a chat exists in the repository and returns a Long that can be used for synchronization.
     * @param messageChannelId
     * @return a (possibly different) Long instance with the same value.
     * same argument values will always return the same instance.
     */
    public Long syncableMessageChannelId(Long messageChannelId) {
        if(!messageChannelRepository.existsById(messageChannelId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("invalid chatId!"));
        syncableIds.putIfAbsent(messageChannelId, messageChannelId);
        return syncableIds.get(messageChannelId);
    }

}
