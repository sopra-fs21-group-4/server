package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * @param message message to post
     * @param userId userId of the sender
     * @param chatId chatId of the chat to post it in
     * @return Message object successfully stored in the repository
     */
    public Message postMessage(Message message, Long userId, Long chatId) {
        message.setUserId(userId);
        message.setChatId(chatId);
        // avoid simultaneous posts to keep unique order
        synchronized (chatId) {
            message.setTimestamp(System.currentTimeMillis());
            try {
                Thread.sleep(1);    // wait for 1 ms, otherwise the mutex would be pointless
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        message = messageRepository.save(message);
        messageRepository.flush();
        return message;
    }

    /**
     * returns a sorted list of all messages referring to a given chatId
     * @param chatId
     */
    public List<Message> getMessages(Long chatId) {

        List<Message> list = messageRepository.findAllByChatId(chatId);
        list.sort(null);
        return list;
    }

    /**
     * deletes a message from the repository
     * TODO not verified
     * @param messageId not null
     */
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    /**
     * updates a message's text
     * TODO not verified
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
