package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the GameService
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

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
    void createGame_success() {
        assertDoesNotThrow(()-> gameService.createGame(gameMaster, gameSettings));
        Game newgame = gameService.createGame(gameMaster, gameSettings);
        assertEquals(game.getName(), newgame.getName());
    }

    @Test
    void getRunningGames_success() {

        Game game = gameService.createGame(gameMaster, gameSettings);

        Collection<Game> runningGames = gameService.getRunningGames();
        for(Game gm : runningGames){
            assertEquals(game.getName(), gm.getName());
        }

    }

    @Test
    void startGame_errorAndSuccess() {
        //workaround because we cannot persist gamerounds
        gameSettings.setTotalRounds(0);
        gameSettings.setSubreddit("cats");
        game.adaptSettings(gameSettings);
        Game game = gameService.createGame(gameMaster, gameSettings);

        // test for error when not gamemaster
        assertThrows(ResponseStatusException.class, ()->gameService.startGame(game.getGameId(), player1.getUserId(), true));

        // test for working starting
        assertDoesNotThrow(()->gameService.startGame(game.getGameId(), gameMaster.getUserId(), true));

    }

    @Test
    void verifyPlayer_errorAndSuccess() {

        Game game = gameService.createGame(gameMaster, gameSettings);
        // test for error when user is not enrolled
        assertThrows(ResponseStatusException.class, () ->gameService.verifyPlayer(game.getGameId(), player1));
        // test when user is enrolled
        assertDoesNotThrow(() ->gameService.verifyPlayer(game.getGameId(), gameMaster));

    }

    @Test
    void findRunningGame_errorAndSuccess() {
        // test for error when game does not exist
        assertThrows(ResponseStatusException.class, () -> gameService.findRunningGame(1L));
        // create game
        Game game = gameService.createGame(gameMaster, gameSettings);
        // test for finding game
        assertDoesNotThrow(() ->gameService.findRunningGame(game.getGameId()));
        assertEquals(game.getName(), gameService.findRunningGame(game.getGameId()).getName());
    }




}