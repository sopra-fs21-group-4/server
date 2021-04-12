package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Chat;
import ch.uzh.ifi.hase.soprafs21.repository.ChatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
