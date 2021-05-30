package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * chat Service
 * This class is the "worker" and responsible for all functionality related to chats
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class ChatService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final MessageChannelRepository messageChannelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(@Qualifier("messageChannelRepository") MessageChannelRepository messageChannelRepository,
                       @Qualifier("messageRepository") MessageRepository messageRepository,
                       @Qualifier("userRepository") UserRepository userRepository
    ) {
        this.messageChannelRepository = messageChannelRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * creates a new message channel and gives the owner the admin role
     * @param userId userId of the owner
     * @return newly created message channel
     */
    public MessageChannel createMessageChannel(Long userId) {
        MessageChannel messageChannel = messageChannelRepository.save(new MessageChannel());
        messageChannel.addRole(userId, "@admin");
        messageChannelRepository.flush();
        return messageChannel;
    }

    public MessageChannel getMessageChannel(Long messageChannelId) {
        if(!messageChannelRepository.existsById(messageChannelId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid messageChannelId!");
        MessageChannel messageChannel = messageChannelRepository.findByMessageChannelId(messageChannelId);
        return messageChannel;
    }

    public MessageChannel verifyReader(Long messageChannelId, Long userId) {
        MessageChannel messageChannel = getMessageChannel(messageChannelId);
        if (!messageChannel.verifyParticipant(userId))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "this is a private channel");
        return messageChannel;
    }

    public MessageChannel verifySender(Long messageChannelId, Long userId) {
        MessageChannel messageChannel = getMessageChannel(messageChannelId);
//        if (messageChannel.getClosed())
//            throw new ResponseStatusException(HttpStatus.GONE, "this channel is closed"); // unused feature
        if (!messageChannel.verifyParticipant(userId))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "this is a private channel");
        return messageChannel;
    }

//    public MessageChannel verifyAdmin(Long messageChannelId, Long userId) {
//        MessageChannel messageChannel = getMessageChannel(messageChannelId);
//        if (!messageChannel.verifyAdmin(userId))
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have admin rights in this channel");
//        return messageChannel;
//    } // unused feature

    /**
     * posts a message to a message channel
     * @param message
     * @param sender
     * @param messageChannel
     * @return Message object successfully stored in the repository
     */
    public Message postMessage(Message message, User sender, MessageChannel messageChannel) {
        message.setSenderId(sender.getUserId());
        message.setTimestamp(System.currentTimeMillis());

        for (String word : message.getText().split(" ")) {
            if (word.startsWith("@")) {
                String username = word.substring(1);
                User user = userRepository.findByUsername(username);
                if (user != null) message.setText(message.getText().replaceAll(word, "@#"+user.getUserId()));
            }
        }

        message = messageRepository.save(message);
        messageRepository.flush();
        messageChannel.addMessage(message);
        messageChannelRepository.flush();
        return message;
    }

}
