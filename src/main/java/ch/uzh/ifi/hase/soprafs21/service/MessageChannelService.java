package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
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

    @Autowired
    public MessageChannelService(@Qualifier("messageChannelRepository") MessageChannelRepository messageChannelRepository) {
        this.messageChannelRepository = messageChannelRepository;
    }

    public MessageChannel createMessageChannel(User owner) {
        MessageChannel messageChannel = messageChannelRepository.save(new MessageChannel());
        messageChannel.addAdmin(owner);
        messageChannelRepository.flush();
        return messageChannel;
    }

    public MessageChannel getMessageChannel(Long messageChannelId) {
        if(!messageChannelRepository.existsById(messageChannelId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid messageChannelId!");
        MessageChannel messageChannel = messageChannelRepository.findByMessageChannelId(messageChannelId);
        return messageChannel;
    }

    public MessageChannel verifyReader(Long messageChannelId, User user) {
        MessageChannel messageChannel = getMessageChannel(messageChannelId);
        if (messageChannel.getConfidential() && !messageChannel.verifyParticipant(user))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "this is a private channel");
        return messageChannel;
    }

    public MessageChannel verifySender(Long messageChannelId, User user) {
        MessageChannel messageChannel = getMessageChannel(messageChannelId);
        if (messageChannel.getClosed())
            throw new ResponseStatusException(HttpStatus.GONE, "this channel is closed");
        if (messageChannel.getConfidential() && !messageChannel.verifyParticipant(user))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "this is a private channel");
        return messageChannel;
    }

    public MessageChannel verifyAdmin(Long messageChannelId, User user) {
        MessageChannel messageChannel = getMessageChannel(messageChannelId);
        if (!messageChannel.verifyAdmin(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have admin rights in this channel");
        return messageChannel;
    }

}
