package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * lobby Controller
 * This class is responsible for handling all REST request that are related to the lobby.
 * The controller will receive the request and delegate the execution to the LobbyService and finally return the result.
 */
@RestController
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    /**
     * create a new game
     */
    @PostMapping("/games/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetFullDTO createLobby(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody GameSettingsDTO gameSettingsDTO
    ) {
        User user = userService.verifyUser(userId, token);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        Game createdGame = gameService.createGame(user, gameSettings);
        return DTOMapper.INSTANCE.convertEntityToGameGetFullDTO(createdGame);
    }

    /**
     * update a game's settings
     */
    @PutMapping("/games/{gameId}/update")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetFullDTO updateGameSettings(
            @PathVariable(value="gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestBody GameSettingsDTO gameSettingsDTO
    ) {
        User user = userService.verifyUser(userId, token);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        Game updatedGame = gameService.adaptGameSettings(gameId, user, gameSettings);
        return DTOMapper.INSTANCE.convertEntityToGameGetFullDTO(updatedGame);
    }



    /**
     * start the game
     */
    @PutMapping("/games/{gameId}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void startGame(
            @PathVariable(value="gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        userService.verifyUser(userId, token);
        gameService.startGame(gameId, userId, true);
    }

    /**
     * join a game
     */
    @PutMapping("/games/{gameId}/join")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetFullDTO joinLobby(
            @PathVariable("gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token,
            @RequestHeader("password") Optional<String> password
    ) {
        User user = userService.verifyUser(userId, token);
        Game joinedGame = gameService.joinGame(gameId, user, password.isPresent()? password.get() : null);
        return DTOMapper.INSTANCE.convertEntityToGameGetFullDTO(joinedGame);
    }

    /**
     * leave a game
     */
    @PutMapping("/games/{gameId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void leaveLobby(
            @PathVariable("gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        User user = userService.verifyUser(userId, token);
        gameService.leaveGame(gameId, user);
    }

    /**
     * set a player ready
     */
    @PutMapping("/games/{gameId}/ready")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void putReady(
            @RequestBody Boolean ready,
            @PathVariable(value="gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        User user = userService.verifyUser(userId, token);
        gameService.setPlayerReady(gameId, user, ready);
    }

    /**
     * update game settings
     */
    @PutMapping("/games/{gameId}/updateSettings")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void updateSettings(
            @RequestBody GameSettingsDTO gameSettingsDTO,
            @PathVariable(value="gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        userService.verifyUser(userId, token);
        gameService.updateSettings(gameId, userId, gameSettings);
    }



    /**
     * suggest a meme title
     */
    @PutMapping("/games/{gameId}/suggest")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void putSuggestion(
            @RequestBody String suggestion,
            @PathVariable(value="gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        User user = userService.verifyUser(userId, token);
        gameService.putSuggestion(gameId, user, suggestion);
    }

    /**
     * vote for a player
     */
    @PutMapping("/games/{gameId}/vote")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void putVote(
            @RequestBody Long vote,
            @PathVariable(value="gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        User user = userService.verifyUser(userId, token);
        gameService.putVote(gameId, user, vote);
    }

    /**
     * get information about all lobbies
     * the information returned is restricted and doesn't contain player-explicit information.
     */
    @GetMapping("/games")
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @ResponseBody
    public List<GameGetRestrictedDTO> getAllGames(
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ) {
        userService.verifyUser(userId, token);
        Collection<Game> games = gameService.getRunningGames();
        List<GameGetRestrictedDTO> gameGetDTOs = new ArrayList<>();

        for(Game game : games){
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetRestrictedDTO(game));
        }
        return gameGetDTOs;
    }

    /**
    * get specific game
    */
    @GetMapping("/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetFullDTO getSingleGame(
            @PathVariable(value="gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ){
        User user = userService.verifyUser(userId, token);
        Game game = gameService.verifyPlayer(gameId, user);
        return DTOMapper.INSTANCE.convertEntityToGameGetFullDTO(game);
    }

    /**
     * get specific game summary
     */
    @GetMapping("/archive/games/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameSummaryDTO getGameSummary(
            @PathVariable(value="gameId") Long gameId,
            @RequestHeader("userId") Long userId,
            @RequestHeader("token") String token
    ){
        User user = userService.verifyUser(userId, token);
        GameSummary gameSummary = gameService.verifyReviewer(gameId, user);
        return DTOMapper.INSTANCE.convertEntityToGameSummaryDTO(gameSummary);
    }



}
