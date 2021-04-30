package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.MessageChannelService;
import ch.uzh.ifi.hase.soprafs21.service.MessageService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Chat Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class ChatController {

    private final MessageChannelService messageChannelService;
    private final MessageService messageService;
    private final UserService userService;

    ChatController(MessageChannelService messageChannelService, MessageService messageService, UserService userService) {
        this.messageChannelService = messageChannelService;
        this.messageService = messageService;
        this.userService = userService;
    }


    /** creating a new chat
     */
    @PostMapping("/chat/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public MessageChannelGetDTO createMessageChannel(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        // authenticate
        User user = userService.verifyUser(userId, token);
        // create
        MessageChannel newMessageChannel = messageChannelService.createMessageChannel(user);
        return DTOMapper.INSTANCE.convertEntityToMessageChannelGetDTO(newMessageChannel);
    }

    /**
     * get messages from a chat, can use queries
     */
    @GetMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<MessageGetDTO> getMessages(
            @PathVariable("chatId") Long chatId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestParam Optional<String> sender,
            @RequestParam Optional<String> content,
            @RequestParam Optional<Long> since,
            @RequestParam Optional<Long> before,
            @RequestParam Optional<Integer> first,
            @RequestParam Optional<Integer> latest
    ) {
        // authenticate
        User user = userService.verifyUser(userId, token);
        MessageChannel messageChannel = messageChannelService.verifyReader(chatId, user);
        // fetch all messages in the internal representation
        List<Message> messages = messageService.getMessages(messageChannel);
        // filter
        if (sender.isPresent())
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
            messages = messages.subList(Math.max(messages.size() - latest.get(), 0), messages.size());

        return convertMessageListToDTOList(messages);
    }

    private List<MessageGetDTO> convertMessageListToDTOList(List<Message> messages) {
        List<MessageGetDTO> messageGetDTOs = new ArrayList<>();

        // convert each message to the API representation
        for (Message m : messages) {
            messageGetDTOs.add(DTOMapper.INSTANCE.convertEntityToMessageGetDTO(m));
        }
        return messageGetDTOs;
    }

    /**
     * post a message to a chat
     */
    @PostMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageGetDTO postMessage(
            @PathVariable("chatId") Long chatId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody MessagePostDTO messagePostDTO
    ) {
        // authenticate
        User sender = userService.verifyUser(userId, token);
        MessageChannel messageChannel = messageChannelService.verifySender(chatId, sender);
        // post
        Message messageToPost = DTOMapper.INSTANCE.convertMessagePostDTOtoEntity(messagePostDTO);
        Message posted = messageService.postMessage(messageToPost, sender, messageChannel);
        return DTOMapper.INSTANCE.convertEntityToMessageGetDTO(posted);
    }

}
