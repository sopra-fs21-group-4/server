package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.MemeTitle;
import ch.uzh.ifi.hase.soprafs21.entity.MemeVote;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * lobby Controller
 * This class is responsible for handling all REST request that are related to the lobby.
 * The controller will receive the request and delegate the execution to the LobbyService and finally return the result.
 */
@RestController
public class LobbyController {

    private final LobbyService lobbyService;


    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    /**
     * create a new lobby
     */
    @PostMapping("/lobbies/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody LobbyPostDTO lobbyPostDTO
    ) {
        Lobby lobbyToCreate = DTOMapper.INSTANCE.convertLobbyPostDTOToEntity(lobbyPostDTO);
        Lobby createdLobby = lobbyService.createLobby(userId, token, lobbyToCreate);
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(createdLobby);
    }

    /**
     * start the game
     */
    @PutMapping("/lobbies/{lobbyId}/start")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void startGame(@PathVariable(value="lobbyId") long lobbyId, @RequestHeader("token") String token, @RequestHeader("userId") String id) {
        Long userId = Long.parseLong(id);
        lobbyService.startGame(lobbyId, userId, token);
        return;
    }

    /**
     * joining a lobby
     */
    @PutMapping("/lobbies/{lobbyId}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO joinLobby(
            @PathVariable("lobbyId") Long lobbyId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestHeader("password") Optional<String> password
    ) {
        Lobby joinedLobby = lobbyService.joinLobby(lobbyId, userId, token, password);
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(joinedLobby);
    }

    /**
     * adding a title
     */
    @PutMapping("/lobbies/{lobbyId}/title")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void newMemeTitle(@RequestBody LobbyMemeTitlePutDTO lobbyMemeTitlePutDTO, @PathVariable(value="lobbyId") long lobbyId, @RequestHeader("token") String token, @RequestHeader("userId") String id) {
        MemeTitle memeTitle = DTOMapper.INSTANCE.convertLobbyMemeTitlePutDTOToEntity(lobbyMemeTitlePutDTO);
        memeTitle.setLobbyId(lobbyId);

        Long userId = Long.parseLong(id);

        memeTitle.setUserId(userId);
        lobbyService.newTitle(memeTitle, userId, token);
    }

    /**
     * voting
     */
    @PutMapping("/lobbies/{lobbyId}/vote")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void newMemeVote(@RequestBody LobbyMemeVotePutDTO lobbyMemeVotePutDTO, @PathVariable(value="lobbyId") long lobbyId, @RequestHeader("token") String token, @RequestHeader("userId") String id) {
        MemeVote memeVote = DTOMapper.INSTANCE.convertLobbyMemeVotePutDTOToEntity(lobbyMemeVotePutDTO);
        memeVote.setLobbyId(lobbyId);
        Long userId = Long.parseLong(id);
        memeVote.setFromUserId(userId);
        lobbyService.newVote(memeVote, userId, token);
    }





//    @GetMapping("/lobbies/getmeme")
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public String getmeme() {
//
//        String memelink = lobbyService.getMemeLink("subreddithere");
//        return memelink;
//    }


    /**
     * get information about all lobbies
     * the information returned is restricted and doesn't contain player-explicit information.
     */
    @GetMapping("/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyOverviewGetDTO> getAllLobbies() {

        List<Lobby> lobbies = lobbyService.getLobbies();
        List<LobbyOverviewGetDTO> lobbyGetDTOs = new ArrayList<>();

        for(Lobby lobby : lobbies){
            lobbyGetDTOs.add(DTOMapper.INSTANCE.convertEntityToLobbyOverviewGetDTO(lobby));
        }

        return lobbyGetDTOs;
    }
    /**
    * get specific lobby
    */
    @GetMapping("/lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getSingleLobby(
            @PathVariable("lobbyId") Long lobbyId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ){
        lobbyService.verifyUserIsInLobby(userId, token, lobbyId);
        Lobby lobby = lobbyService.getLobbyByLobbyId(lobbyId);
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }



}
