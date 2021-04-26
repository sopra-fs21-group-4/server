package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class MessageService {

    // TODO maybe this could be used
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(@Qualifier("messageRepository") MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * sets the correct index of a Message object and stores it in the repository
     * @param message
     * @param sender
     * @param messageChannel
     * @return Message object successfully stored in the repository
     */
    public Message postMessage(Message message, User sender, MessageChannel messageChannel) {
        message.setSender(sender);
        message.setMessageChannel(messageChannel);
        // avoid simultaneous posts to keep unique order
        synchronized (messageChannel) {
            message.setTimestamp(System.currentTimeMillis());
            try {
                Thread.sleep(1);    // wait for 1 ms, otherwise the mutex would be pointless
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        message = messageRepository.save(message);
        messageRepository.flush();
        messageChannel.messagePosted(message);
        return message;
    }

    /**
     * returns a sorted list of all messages referring to a given chatId
     * @param messageChannel
     */
    public List<Message> getMessages(MessageChannel messageChannel) {

        List<Message> list = messageRepository.findAllByMessageChannel(messageChannel);
        list.sort(null);
        return list;
    }

    /**
     * deletes a message from the repository
     * TODO not verified
     * TODO notify listeners
     * @param messageId not null
     */
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    /**
     * updates a message's text
     * TODO not verified
     * TODO notify listeners
     * @param messageId
     * @param text
     * @return the updated Message
     */
    public Message putMessage(Long messageId, String text) {
        Message message = messageRepository.findByMessageId(messageId);
        message.setText(text);
        return message;
    }

}
