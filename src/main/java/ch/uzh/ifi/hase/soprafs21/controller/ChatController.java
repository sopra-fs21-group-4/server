package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Chat;
import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.ChatService;
import ch.uzh.ifi.hase.soprafs21.service.MessageService;
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
    private final MessageService messageService;

    ChatController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }


    /** creating a new chat
     */
    @PostMapping("/chat/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ChatGetDTO createChat() {
        Chat newChat = chatService.createChat();
        return DTOMapper.INSTANCE.convertEntityToChatGetDTO(newChat);
    }

    /** get all messages from a chat
     * TODO maybe implement a method that only returns a requested amount of messages
     */
    @GetMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<MessageGetDTO> getMessages(@PathVariable("chatId") Long chatId) {

        // fetch all messages in the internal representation
        List<Message> messages = messageService.getMessages(chatId);
        List<MessageGetDTO> messageGetDTOs = new ArrayList<>();

        // convert each message to the API representation
        for (Message m : messages) {
            MessageGetDTO dto = DTOMapper.INSTANCE.convertEntityToMessageGetDTO(m);
            // need to manually set senderName by senderId.
            //dto.setSenderName(userService.getUserByUserId(m.getSenderId()).getUsername());
            messageGetDTOs.add(dto);
        }
        return messageGetDTOs;
    }

    /** post a message to a chat
     */
    @PostMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MessageGetDTO postMessage(@RequestBody MessagePostDTO messagePostDTO, @PathVariable("chatId") Long chatId){

        Message messageToPost = DTOMapper.INSTANCE.convertMessagePostDTOtoEntity(messagePostDTO);

        Chat targetChat = chatService.getChat(chatId);

        Message posted = messageService.postMessage(messageToPost, targetChat);

        MessageGetDTO response = DTOMapper.INSTANCE.convertEntityToMessageGetDTO(posted);
        //response.setSenderName(userService.getUserByUserId(posted.getSenderId()).getUsername());
        return response;
    }

}
