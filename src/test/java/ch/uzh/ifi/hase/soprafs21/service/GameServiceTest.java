package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.Application;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameRound;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.*;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RunWith(SpringRunner.class)
class GameServiceTest {


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
        gameSettings.setName("testname");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);


    }



    @Test
    void createGame() {
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
    void startGame() {
        //workaround because we cannot persist gamerounds
        gameSettings.setTotalRounds(0);
        game.adaptSettings(gameSettings);
        Game game = gameService.createGame(gameMaster, gameSettings);

        // test for error when not gamemaster
        assertThrows(ResponseStatusException.class, ()->gameService.startGame(game.getGameId(), player1.getUserId(), true));

        // test for working starting
        assertDoesNotThrow(()->gameService.startGame(game.getGameId(), gameMaster.getUserId(), true));

    }

    @Test
    void verifyPlayer() {

    }

    @Test
    void findRunningGame() {



    }



    @Test
    void joinGame() {
    }

    @Test
    void leaveGame() {
    }





    @Test
    void putSuggestion() {
    }

    @Test
    void putVote() {
    }

//    @Test
//    void randomGameId() {
//    }
}