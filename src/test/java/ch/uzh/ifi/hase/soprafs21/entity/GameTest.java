package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.RoundPhase;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {




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
        List<Long> playerlist = new ArrayList<>();

        // testing if no players are ready
        assertEquals(playerlist, game.getReadyPlayers());

        // testing if player
        game.setPlayerReady(player1.getUserId(),true);
        playerlist.add(player1.getUserId());

        assertEquals(playerlist, game.getReadyPlayers());

    }

    @Test
    void promotePlayer() {
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

        //join game with new player
        game.enrollPlayer(player1, "");

        // testing if gameMaster is actually gameMaster
        assertEquals(gameMaster.getUserId(), game.getGameMaster());

        // promote new player
        game.promotePlayer(player1.getUserId());

        // test if new player is gameMaster
        assertEquals(player1.getUserId(),game.getGameMaster());

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
        List<Long> playerlist = new ArrayList<>();
        playerlist.add(gameMaster.getUserId());


        // testing if only gamemaster is enrolled
        assertEquals(playerlist, game.getEnrolledPlayers());


        // enrolling player
        game.enrollPlayer(player1, "");
        playerlist.add(player1.getUserId());

        // testing if both players are enrolled
        assertTrue(playerlist.containsAll(game.getEnrolledPlayers()));
    }

    @Test
    void dismissPlayer() {
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
        List<Long> playerlist = new ArrayList<>();
        playerlist.add(gameMaster.getUserId());


        // enrolling player (tested in a different test)
        game.enrollPlayer(player1, "");


        // remove player1
        game.dismissPlayer(player1);


        // testing if only gamemaster is enrolled
        assertEquals(playerlist, game.getEnrolledPlayers());

    }

    @Test
    void banPlayer() {
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
        List<Long> playerlist = new ArrayList<>();
        playerlist.add(gameMaster.getUserId());

        // enrolling player (tested in a different test)
        game.enrollPlayer(player1, "");

        // remove player1
        game.banPlayer(player1);

        // testing if only gamemaster is enrolled
        assertEquals(playerlist, game.getEnrolledPlayers());

        // test if banned player cannot join
        assertThrows(SecurityException.class, () -> game.enrollPlayer(player1, ""));


    }


    @Test
    void forgivePlayer() {
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
        List<Long> playerlist = new ArrayList<>();
        playerlist.add(gameMaster.getUserId());

        // enrolling player (tested in a different test)
        game.enrollPlayer(player1, "");

        // remove player1
        game.banPlayer(player1);

        // forgive player
        game.forgivePlayer(player1.getUserId());

        // enroll player
        game.enrollPlayer(player1, "");

        // test if player is enrolled
        playerlist.add(player1.getUserId());
        assertEquals(playerlist, game.getEnrolledPlayers());

    }

    @Test
    void adaptSettings() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        GameSettings gameSettings2 = new GameSettings();
        gameSettings2.setName("test2");
        gameSettings2.setPassword("2");
        gameSettings2.setMaxPlayers(10);
        gameSettings2.setTotalRounds(10);
        gameSettings2.setMemeSourceURL("test2");
        gameSettings2.setMemeType(MemeType.RANDOM);
        gameSettings2.setMaxSuggestSeconds(10);
        gameSettings2.setMaxAftermathSeconds(10);
        gameSettings2.setMaxVoteSeconds(10);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        // check if settings are set
        assertEquals(gameSettings.getMaxPlayers(), game.getMaxPlayers());
        assertEquals(gameSettings.getMaxAftermathSeconds(), game.getMaxAftermathSeconds());
        assertEquals(gameSettings.getMaxVoteSeconds(), game.getMaxVoteSeconds());
        assertEquals(gameSettings.getMaxSuggestSeconds(), game.getMaxSuggestSeconds());
        assertEquals(gameSettings.getTotalRounds(), game.getTotalRounds());
        assertEquals(gameSettings.getName(), game.getName());
        assertEquals(gameSettings.getMemeSourceURL(), game.getMemeSourceURL());
        assertEquals(gameSettings.getMemeType(), game.getMemeType());

        //change game settings
        game.adaptSettings(gameSettings2);

        // test changed settings
        assertEquals(gameSettings2.getMaxPlayers(), game.getMaxPlayers());
        assertEquals(gameSettings2.getMaxAftermathSeconds(), game.getMaxAftermathSeconds());
        assertEquals(gameSettings2.getMaxVoteSeconds(), game.getMaxVoteSeconds());
        assertEquals(gameSettings2.getMaxSuggestSeconds(), game.getMaxSuggestSeconds());
        assertEquals(gameSettings2.getTotalRounds(), game.getTotalRounds());
        assertEquals(gameSettings2.getName(), game.getName());
        assertEquals(gameSettings2.getMemeSourceURL(), game.getMemeSourceURL());
        assertEquals(gameSettings2.getMemeType(), game.getMemeType());
    }

    @Test
    void initialize() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setPassword("");
        gameSettings.setGameSettingsId(1l);
        gameSettings.setMaxPlayers(5);

        Game game = new Game();
        game.setGameId(1l);

        // test gamestate before initializing
        assertEquals(GameState.INIT, game.getGameState());


        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        // test gamestate after initializing
        assertEquals(GameState.LOBBY, game.getGameState());

    }

    @Test
    void closeLobbyWithoutForce() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        // test if error is thrown if not all players are ready
        assertThrows(IllegalStateException.class, () -> game.closeLobby(false));

        // setting the player to ready
        game.setPlayerReady(gameMaster.getUserId(),true);

        // closing the lobby to start the game
        game.closeLobby(false);

        // test if the game is starting
        assertEquals(GameState.STARTING, game.getGameState());


    }

    @Test
    void closeLobbyWithForce() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // test if the game is starting
        assertEquals(GameState.STARTING, game.getGameState());

    }

    @Test
    void start() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // start the game
        game.start();

        // test if game is now running
        assertEquals(GameState.RUNNING, game.getGameState());
        assertEquals(1, game.getRoundCounter());
    }

    @Test
    void pause() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);


        // test for error if game is not running
        assertThrows(IllegalStateException.class, ()->game.pause());

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // start the game
        game.start();
        //pause the game
        game.pause();

        // test if game is now running
        assertEquals(GameState.PAUSED, game.getGameState());



    }

    @Test
    void resume() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        // test if error if game is not running yet
        assertThrows(IllegalStateException.class, ()->game.resume());

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // start the game
        game.start();
        //pause the game
        game.pause();
        //resume game
        game.resume();

        // test if game is now running
        assertEquals(GameState.RUNNING, game.getGameState());

    }

    @Test
    void kill() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        // abort the game
        game.kill();

        //test if game is aborted
        assertEquals(GameState.ABORTED, game.getGameState());

        // TODO check if players are kicked
    }

    @Test
    void skipRound() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(2);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();

        int counter = game.getRoundCounter();

        // skip a round
        game.skipRound();

        // test if the next round has begun
        assertEquals(counter+1, game.getRoundCounter());
        game.update();

        // testing if the round is in the starting phase
        assertEquals(RoundPhase.STARTING, game.getCurrentRoundPhase());

        // skip another round so the game is finished
        game.skipRound();

        // test if the game is finished
        assertEquals(GameState.AFTERMATH, game.getGameState());
    }

    @Test
    void putSuggestion() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(2);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        String suggestion = "asd";

        //test exceptions
        assertThrows(IllegalStateException.class,()->game.putSuggestion(1l,suggestion));


        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();
        // go to starting phase
        game.getCurrentRound().nextPhase();
        // go to suggest phase
        game.getCurrentRound().nextPhase();
        //test exceptions
        assertThrows(SecurityException.class,()->game.putSuggestion(10l,suggestion));

        // creating expected suggestions
        Map<Long, String> expected = new HashMap<>();
        expected.put(gameMaster.getUserId(), suggestion);

        game.putSuggestion(gameMaster.getUserId(),suggestion);

        // test suggestions
        assertEquals(expected, game.getCurrentRound().getSuggestions());


    }

    @Test
    void putVote() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(2);
        gameSettings.setMemeSourceURL("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster.getUserId());
        game.adaptSettings(gameSettings);

        String suggestion = "asd";

        //test exceptions
        assertThrows(IllegalStateException.class,()->game.putSuggestion(1l,suggestion));


        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();
        // go to starting phase
        game.getCurrentRound().nextPhase();
        // go to suggest phase
        game.getCurrentRound().nextPhase();
        // suggest a title
        game.putSuggestion(gameMaster.getUserId(),suggestion);
        // go to vote phase
        game.getCurrentRound().nextPhase();
        //test exceptions
        assertThrows(SecurityException.class,()->game.putSuggestion(10l,suggestion));

        // creating expected suggestions
        Map<Long, Long> expected = new HashMap<>();
        expected.put(gameMaster.getUserId(), gameMaster.getUserId());

        // voting for the suggestion
        game.putVote(gameMaster.getUserId(),gameMaster.getUserId());

        // test suggestions
        assertEquals(expected, game.getCurrentRound().getVotes());

    }


}