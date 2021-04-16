package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Chat;
import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.rest.dto.ChatGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.MessageGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.MessagePostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.ChatService;
import ch.uzh.ifi.hase.soprafs21.service.MessageService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to games.
 * The controller will receive the request and delegate the execution to the GameService and finally return the result.
 */
@RestController
public class GameController {

    private final ChatService chatService;
    private final UserService userService;

    GameController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    /**
     * get game info
     * requires userId and token of a user that has already joined the game
     * TODO
     */
    @GetMapping("/game/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChatGetDTO getGame(
            @PathVariable("gameId") Long gameId,
            @RequestParam Long userId,
            @RequestParam String token
    ) {
        // TODO we actually want a game instead of a chat, as soon as games exist
        Chat chat = chatService.getChat(gameId);
        return DTOMapper.INSTANCE.convertEntityToChatGetDTO(chat);
    }

    /**
     * join a game
     * requires userId, token and password (if the game has a password)
     * TODO
     */
    @PutMapping("/game/{gameId}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChatGetDTO joinGame(
            @PathVariable("gameId") Long gameId,
            @RequestParam Long userId,
            @RequestParam String token,
            @RequestParam Optional<String> password
    ) {
        return null;
    }

}
