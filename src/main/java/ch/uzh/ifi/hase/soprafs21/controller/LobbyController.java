package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.MemeTitle;
import ch.uzh.ifi.hase.soprafs21.entity.MemeVote;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyMemeTitlePutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyMemeVotePutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public LobbyGetDTO createLobby(@RequestHeader("token") String token, @RequestHeader("userId") String id) {
        Long userId = Long.parseLong(id);
        Lobby createdLobby = lobbyService.createLobby(userId, token);
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
     * joining a lobby TODO password?
     */
    @PostMapping("/lobbies/{lobbyId}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO joinLobby(@PathVariable(value="lobbyId") long lobbyId, @RequestHeader("token") String token, @RequestHeader("userId") String id) {
        Long userId = Long.parseLong(id);
        Lobby joinedLobby = lobbyService.joinLobby(lobbyId, userId, token);
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
    get information about all lobbies TODO mainly for debugging purposes at the moment
     */
    @GetMapping("/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyGetDTO> getAllLobbies() {

        List<Lobby> lobbies = lobbyService.getLobbies();
        List<LobbyGetDTO> lobbyGetDTOs = new ArrayList<>();

        for(Lobby lobby : lobbies){
            lobbyGetDTOs.add(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby));
        }

        return lobbyGetDTOs;
    }
    /**
    * get specific lobby TODO mainly for debugging purposes at the moment
    */
    @GetMapping("/lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getSingleLobby(@PathVariable(value="lobbyId") long id){ // , @RequestHeader("token") String token, @RequestHeader("id") String Userid

        Lobby lobby = lobbyService.getLobbyByLobbyId(id);

        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }



}
