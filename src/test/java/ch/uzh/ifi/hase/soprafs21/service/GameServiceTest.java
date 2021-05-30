package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.constant.RoundPhase;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameRound;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRoundRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameSettingsRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;

import org.junit.After;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for the GameService
 *
 */

class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRoundRepository gameRoundRepository;

    @Mock
    private GameSettingsRepository gameSettingsRepository;

    private User gameMaster;
    private User player1;
    private GameSettings gameSettings;
    private Game game;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        gameMaster = new User();
        gameMaster.setUserId(1L);
        gameMaster.setPassword("testPW");
        gameMaster.setUsername("testUsername");

        player1 = new User();
        player1.setUserId(2L);
        player1.setPassword("testPW2");
        player1.setUsername("testUsername2");

        gameSettings = new GameSettings();
        gameSettings.setGameSettingsId(1L);
        gameSettings.setName("testName");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        game = new Game();
        game.setGameId(1L);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);
    }

    @Test
    void adaptGameSettingsSuccess() {

        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        assertEquals(game, gameService.adaptGameSettings(game.getGameId(), gameMaster, gameSettings));

    }

    @Test
    void adaptGameSettingsError() {

        game.closeLobby(true);
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        assertThrows(ResponseStatusException.class,
                () -> gameService.adaptGameSettings(game.getGameId(), gameMaster, gameSettings));
    }

    @Test
    void joinGameTest() {

        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);

        assertEquals(game, gameService.joinGame(game.getGameId(), player1, ""));
        gameService.joinGame(game.getGameId(), player1, "");
        assertEquals(game, gameService.joinGame(game.getGameId(), player1, ""));

    }

    @Test
    void joinGameWrongPasswordTest() {
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(game.getGameId(), player1, "as"));
    }

    @Test
    void joinGameGameIsRunningTest() {
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        game.closeLobby(true);
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(game.getGameId(), player1, "as"));
    }

    @Test
    void joinGameBannedFromGameTest() {
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        game.banPlayer(player1);
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(game.getGameId(), player1, "as"));
    }

    @Test
    void putSuggestionErrorTest(){
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        assertThrows(ResponseStatusException.class, () -> gameService.putSuggestion(game.getGameId(), gameMaster, "suggestion"));
    }

    @Test
    void putVoteErrorTest(){
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        assertThrows(ResponseStatusException.class, () -> gameService.putVote(game.getGameId(), gameMaster, 2l));
    }

    @Test
    void joinGameLobbyFullTest() {
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        gameSettings.setMaxPlayers(1);
        game.adaptSettings(gameSettings);
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(game.getGameId(), player1, "as"));
    }

    @Test
    void leaveGameTest() {
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);
        game.dismissPlayer(gameMaster);
        assertEquals(null, gameMaster.getCurrentGameId());
    }

    @Test
    void setPlayerReadyTest() {
        Mockito.when(gameRepository.findByGameId(Mockito.any())).thenReturn(game);

        assertEquals(game, gameService.joinGame(game.getGameId(), player1, ""));
        gameService.joinGame(game.getGameId(), player1, "");

        gameService.setPlayerReady(game.getGameId(), player1, true);
        assertEquals(game.getPlayerState(player1.getUserId()), PlayerState.READY);
    }

}