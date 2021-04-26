package ch.uzh.ifi.hase.soprafs21.nonpersistent;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.nonpersistent.GameSettings;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

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

        Game game = new Game(1l, gameMaster, gameSettings);


        // enrolling player and setting ready
        game.enrollPlayer(player1, "");

        // list of players
        List<User> playerlist = new ArrayList<>();

        // testing if no players are ready
        assertEquals(game.getReadyPlayers(), playerlist);

        // testing if player
        game.setPlayerReady(player1,true);
        playerlist.add(player1);

        assertEquals(game.getReadyPlayers(), playerlist);
    }

    @Test
    void promotePlayer() {
    }

    @Test
    void enrollPlayer() {
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