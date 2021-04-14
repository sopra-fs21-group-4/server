package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Chat;
import ch.uzh.ifi.hase.soprafs21.repository.ChatRepository;
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
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class ChatService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final ChatRepository chatRepository;

    private ConcurrentMap<Long, Long> syncableIds = new ConcurrentHashMap<Long, Long>();

    @Autowired
    public ChatService(@Qualifier("chatRepository") ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat createChat() {
        return chatRepository.save(new Chat());
    }

    public Chat getChat(Long chatId) {
        return chatRepository.findByChatId(chatId);
    }

    /**
     * checks if a chat exists in the repository and returns a Long that can be used for synchronization.
     * @param chatId
     * @return a (possibly different) Long instance with the same value.
     * same argument values will always return the same instance.
     */
    public Long syncableChatId(Long chatId) {
        if(!chatRepository.existsById(chatId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("invalid chatId!"));
        syncableIds.putIfAbsent(chatId, chatId);
        return syncableIds.get(chatId);
    }

}
