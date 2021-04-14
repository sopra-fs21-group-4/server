package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Chat;
import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class MessageService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(@Qualifier("messageRepository") MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * sets the correct index of a Message object and stores it in the repository
     * @param message the message to post
     * @param targetChat the chat to post it in
     * @return Message object successfully stored in the repository
     */
    public Message postMessage(Message message, Chat targetChat) {
        if (message == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("message may not be null!"));
        if (targetChat == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("invalid chatId!"));

        message.setChatId(targetChat.getChatId());
        // avoid simultaneous posts to keep unique order
        synchronized (targetChat) {
            message.setTimestamp(System.currentTimeMillis());
            try {
                Thread.sleep(1);    // wait for 1 ms, otherwise the mutex would be pointless
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return messageRepository.save(message);
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
