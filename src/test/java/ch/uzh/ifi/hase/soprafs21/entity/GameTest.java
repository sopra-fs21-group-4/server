package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    /**
    Unit testing the Game class
     */
    @Test
    void setPlayerReady() {

        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);


        User player1 = new User();
        player1.setUserId(2l);


        GameSettings gameSettings = new GameSettings();
        gameSettings.setPassword("");
        gameSettings.setGameSettingsId(1l);
        gameSettings.setMaxPlayers(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);


        // enrolling player and setting ready
        game.enrollPlayer(player1, "");


        // list of players
        List<User> playerlist = new ArrayList<>();

        // testing if no players are ready
        assertEquals(playerlist, game.getReadyPlayers());

        // testing if player
        game.setPlayerReady(player1.getUserId(),true);
        playerlist.add(player1);

        assertEquals(playerlist, game.getReadyPlayers());

    }

    @Test
    void promotePlayer() {
    }

    @Test
    void enrollPlayer() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);


        User player1 = new User();
        player1.setUserId(2l);


        GameSettings gameSettings = new GameSettings();
        gameSettings.setPassword("");
        gameSettings.setGameSettingsId(1l);
        gameSettings.setMaxPlayers(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);


        // list of players and adding gamemaster who is already enrolled
        List<User> playerlist = new ArrayList<>();
        playerlist.add(gameMaster);


        // testing if no players are ready
        assertEquals(playerlist, game.getEnrolledPlayers());


        // enrolling player
        game.enrollPlayer(player1, "");
        playerlist.add(player1);

        // testing if both players are enrolled
        assertEquals(playerlist, game.getEnrolledPlayers());
    }

    @Test
    void dismissPlayer() {
    }

    @Test
    void banPlayer() {
    }

    @Test
    void forgivePlayer() {
    }

    @Test
    void adaptSettings() {
    }

    @Test
    void initialize() {
    }

    @Test
    void closeLobby() {
    }

    @Test
    void start() {
    }

    @Test
    void pause() {
    }

    @Test
    void resume() {
    }

    @Test
    void kill() {
    }

    @Test
    void update() {
    }

    @Test
    void skipRound() {
    }

    @Test
    void putSuggestion() {
    }

    @Test
    void putVote() {
    }

    @Test
    void summarizePastRounds() {
    }
}