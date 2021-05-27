package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.ChatService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    ChatController(ChatService messageChannelService, UserService userService) {
        this.chatService = messageChannelService;
        this.userService = userService;
    }


    /** creating a new chat
     */
    @PostMapping("/chat/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public MessageChannelDTO createMessageChannel(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        // authenticate
        User user = userService.verifyUser(userId, token);
        // create
        MessageChannel newMessageChannel = chatService.createMessageChannel(userId);
        return DTOMapper.INSTANCE.convertEntityToMessageChannelDTO(newMessageChannel);
    }

    /**
     * get messages from a chat, can use queries
     */
    @GetMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<MessageDTO> getMessages(
            @PathVariable("chatId") Long chatId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
            /*@RequestParam Optional<String> sender,
            @RequestParam Optional<String> content,
            @RequestParam Optional<Long> since,
            @RequestParam Optional<Long> before,
            @RequestParam Optional<Integer> first,
            @RequestParam Optional<Integer> latest*/
    ) {
        // authenticate
        userService.verifyUser(userId, token);
        MessageChannel messageChannel = chatService.verifyReader(chatId, userId);
        // fetch all messages in the internal representation
        List<Message> messages = messageChannel.getMessages();
        // filter
        /*if (sender.isPresent())
            messages.removeIf(m -> !m.getSender().getUsername().equals(sender.get()));
        if (content.isPresent())
            messages.removeIf(m -> !m.getText().contains(content.get()));
        if (since.isPresent())
            messages.removeIf(m -> m.getTimestamp() < since.get());
        if (before.isPresent())
            messages.removeIf(m -> m.getTimestamp() > before.get());
        if (first.isPresent() && latest.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("bad query"));
        if (first.isPresent())
            messages = messages.subList(0, Math.min(first.get(), messages.size()));
        if (latest.isPresent())
            messages = messages.subList(Math.max(messages.size() - latest.get(), 0), messages.size());*/

        return convertMessageListToDTOList(messages);
    }

    private List<MessageDTO> convertMessageListToDTOList(List<Message> messages) {
        List<MessageDTO> messageGetDTOs = new ArrayList<>();

        // convert each message to the API representation
        for (Message m : messages) {
            messageGetDTOs.add(DTOMapper.INSTANCE.convertEntityToMessageDTO(m));
        }
        return messageGetDTOs;
    }

    /**
     * post a message to a chat
     */
    @PostMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageDTO postMessage(
            @PathVariable("chatId") Long chatId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody MessagePostDTO messagePostDTO
    ) {
        // authenticate
        User sender = userService.verifyUser(userId, token);
        MessageChannel messageChannel = chatService.verifySender(chatId, userId);
        // post
        Message messageToPost = DTOMapper.INSTANCE.convertMessagePostDTOtoEntity(messagePostDTO);
        Message posted = chatService.postMessage(messageToPost, sender, messageChannel);
        return DTOMapper.INSTANCE.convertEntityToMessageDTO(posted);
    }

}
