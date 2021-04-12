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

import java.util.Comparator;
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
        // synchronized method
        message.setIndex(targetChat.incrementLength());
        message.setTimestamp(System.currentTimeMillis());

        return messageRepository.save(message);
    }

    /**
     * returns a sorted list of messages referring to a given chatId
     * TODO not sure if this implementation of Example<Message> works. Needs to be tested.
     */
    public List<Message> getMessages(Long chatId) {

        List<Message> list = messageRepository.findAllByChatId(chatId);
        list.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        return list;
    }

}
