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
        // avoid simultaneous posts to keep order persistent
        synchronized (targetChat) {
            message.setTimestamp(System.currentTimeMillis());
            try {
                Thread.sleep(1);
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
     * returns a sorted list of messages referring to a given chatId, filtered by a given query
     * @param chatId
     * @param query
     */
    public List<Message> getMessages(Long chatId, String query) {

        List<Message> messages = messageRepository.findAllByChatId(chatId);
        messages.sort(null);

        try {

            int first = 0, last = 0;

            for (String subQuery : query.split("&")){
                String[] queryPart = subQuery.split("=");
                String key = queryPart[0];
                String requirement = queryPart[1];
                switch(key) {
                    case "since":
                        Long sinceTime = Long.parseLong(requirement);
                        messages.removeIf(m -> m.getTimestamp() < sinceTime);
                        break;
                    case "before":
                        Long beforeTime = Long.parseLong(requirement);
                        messages.removeIf(m -> m.getTimestamp() > beforeTime);
                        break;
                    case "sender":
                        messages.removeIf(m -> !m.getSenderUsername().equals(requirement));
                        break;
                    case "contains":
                        messages.removeIf(m -> !m.getText().contains(requirement));
                        break;
                    case "first":
                        first = Integer.parseInt(requirement);
                        break;
                    case "last":
                        last = Integer.parseInt(requirement);
                        break;
                }
            }

            if (first * last != 0) throw new Exception();
            else if (first != 0) messages = messages.subList(0, first);
            else if (last != 0) messages = messages.subList(messages.size() - last, messages.size());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("bad query"));
        }

        return messages;
    }

}
