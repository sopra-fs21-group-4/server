package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.constant.RoundPhase;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class GameTest {



    /**
     * Unit testing the Game class
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
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // enrolling player and setting ready
        game.enrollPlayer(player1, "");

        // list of players
        List<Long> playerlist = new ArrayList<>();

        // testing if no players are ready
        assertEquals(playerlist, game.getReadyPlayers());

        // testing if player
        game.setPlayerReady(player1.getUserId(), true);
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
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // join game with new player
        game.enrollPlayer(player1, "");

        // testing if gameMaster is actually gameMaster
        assertEquals(gameMaster.getUserId(), game.getGameMaster());

        // promote new player and test return value
        assertEquals(PlayerState.GAME_MASTER, game.promotePlayer(player1.getUserId()));

        // test if new player is gameMaster
        assertEquals(player1.getUserId(), game.getGameMaster());

        // test if old user is not gamemaster anymore
        assertNotEquals(PlayerState.GAME_MASTER, game.getPlayerState(gameMaster.getUserId()));
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
        game.initialize(gameMaster);
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
    void enrollPlayerErrors() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        User player1 = new User();
        player1.setUserId(2l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);




        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);

        User player4 = new User();
        player4.setUserId(4l);


        // enrolling the players to the game
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        assertEquals(game.getPlayerState(3l), game.enrollPlayer(player3, ""));

        assertThrows(IllegalArgumentException.class, () -> game.enrollPlayer(player4, "wrongPW"));

        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();


        // enrolling player
        assertThrows(IllegalStateException.class, () -> game.enrollPlayer(player4, ""));



    }

    @Test
    void dismissPlayer() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        User player1 = new User();
        player1.setUserId(2l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(1);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        User player4 = new User();
        player4.setUserId(4l);

        // list of players and adding gamemaster who is already enrolled
        List<Long> playerlist = new ArrayList<>();
        playerlist.add(gameMaster.getUserId());

        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        playerlist.add(player2.getUserId());
        playerlist.add(player3.getUserId());


        assertEquals(PlayerState.STRANGER, game.getPlayerState(player4.getUserId()));

        game.enrollPlayer(player4, "");
        playerlist.add(player4.getUserId());


        // testing new game master if game master leaves
        game.dismissPlayer(gameMaster);
        playerlist.remove(gameMaster.getUserId());

        assertEquals(player2.getUserId(), game.getGameMaster());

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // start the game
        game.start();


        // testing enrolled players
        assertEquals(playerlist, game.getEnrolledPlayers());


        // remove player2
        assertEquals(PlayerState.ABORTED, game.dismissPlayer(player2));





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
        game.initialize(gameMaster);
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

        //test if you can not ban the gameMaster
        assertThrows(IllegalArgumentException.class, () -> game.banPlayer(gameMaster));

    }

    @Test
    void forgivePlayer() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        User player1 = new User();
        player1.setUserId(2l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(7);
        gameSettings.setTotalRounds(5);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
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

        // test if player is not banned
        assertEquals(PlayerState.ENROLLED, game.getPlayerState(player1.getUserId()));

        User player3 = new User();
        player3.setUserId(3l);
        User player4 = new User();
        player4.setUserId(4l);
        User player5 = new User();
        player5.setUserId(5l);

        game.enrollPlayer(player3, "");
        game.enrollPlayer(player4, "");
        game.enrollPlayer(player5, "");

        game.banPlayer(player3);

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // start the game
        game.start();

        // testing if you cannot forgive later
        assertThrows(IllegalStateException.class, () -> game.forgivePlayer(player3.getUserId()));

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
        gameSettings.setSubreddit("memes");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        GameSettings gameSettings2 = new GameSettings();
        gameSettings2.setName("test2");
        gameSettings2.setPassword("2");
        gameSettings2.setMaxPlayers(10);
        gameSettings2.setTotalRounds(10);
        gameSettings2.setSubreddit("cats");
        gameSettings2.setMemeType(MemeType.RISING);
        gameSettings2.setMaxSuggestSeconds(10);
        gameSettings2.setMaxAftermathSeconds(10);
        gameSettings2.setMaxVoteSeconds(10);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // check if settings are set
        assertEquals(gameSettings.getMaxPlayers(), game.getMaxPlayers());
        assertEquals(gameSettings.getMaxAftermathSeconds(), game.getMaxAftermathSeconds());
        assertEquals(gameSettings.getMaxVoteSeconds(), game.getMaxVoteSeconds());
        assertEquals(gameSettings.getMaxSuggestSeconds(), game.getMaxSuggestSeconds());
        assertEquals(gameSettings.getTotalRounds(), game.getTotalRounds());
        assertEquals(gameSettings.getName(), game.getName());
        assertEquals(gameSettings.getSubreddit(), game.getSubreddit());
        assertEquals(gameSettings.getMemeType(), game.getMemeType());

        // change game settings
        game.adaptSettings(gameSettings2);

        // test changed settings
        assertEquals(gameSettings2.getMaxPlayers(), game.getMaxPlayers());
        assertEquals(gameSettings2.getMaxAftermathSeconds(), game.getMaxAftermathSeconds());
        assertEquals(gameSettings2.getMaxVoteSeconds(), game.getMaxVoteSeconds());
        assertEquals(gameSettings2.getMaxSuggestSeconds(), game.getMaxSuggestSeconds());
        assertEquals(gameSettings2.getTotalRounds(), game.getTotalRounds());
        assertEquals(gameSettings2.getName(), game.getName());
        assertEquals(gameSettings2.getSubreddit(), game.getSubreddit());
        assertEquals(gameSettings2.getMemeType(), game.getMemeType());


        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, gameSettings2.getPassword());
        game.enrollPlayer(player3, gameSettings2.getPassword());

        // test for error if game is not running
        assertThrows(IllegalStateException.class, () -> game.pause());

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // start the game
        game.start();

        // test for error when game has started
        assertThrows(IllegalStateException.class, () -> game.adaptSettings(gameSettings));


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

        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // test gamestate after initializing
        assertEquals(GameState.LOBBY, game.getGameState());

    }

    @Test
    void closeLobbyWithoutForce() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // test if lobby is not closed when players are not ready
        assertEquals(false, game.closeLobby(false));
        assertEquals(GameState.LOBBY, game.getGameState());

        // setting the player to ready
        game.setPlayerReady(gameMaster.getUserId(), true);
        game.enrollPlayer(player2, "");
        game.setPlayerReady(player2.getUserId(), true);
        game.enrollPlayer(player3, "");
        game.setPlayerReady(player3.getUserId(), true);

        // closing the lobby to start the game
        assertEquals(true, game.closeLobby(false));

        // test if the game is starting
        assertEquals(GameState.STARTING, game.getGameState());
    }

    @Test
    void closeLobbyWithForce() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(5);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // enroll 2 more players
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // closing the lobby to start the game with force (without the players being
        // ready)
        assertEquals(true, game.closeLobby(true));

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
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // test for error when lobby isn't closed
        assertThrows(IllegalStateException.class, () -> game.start());

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
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // test for error if game is not running
        assertThrows(IllegalStateException.class, () -> game.pause());

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // start the game
        game.start();
        // pause the game
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
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // test if error if game is not running yet
        assertThrows(IllegalStateException.class, () -> game.resume());

        // closing the lobby to start the game with force
        game.closeLobby(true);

        // start the game
        game.start();
        // pause the game
        game.pause();
        // resume game
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
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // abort the game
        game.kill();

        // test if game is aborted
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
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();

        int counter = game.getRoundCounter();

        // skip a round
        game.skipRound();

        // test if the next round has begun
        assertEquals(counter + 1, game.getRoundCounter());

    }

    @Test
    void putSuggestion() {
        // creating given objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(2);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        String suggestion = "asd";

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // test exception when not in title giving Phase
        assertThrows(IllegalStateException.class, () -> game.putSuggestion(1l, suggestion));

        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();
        // go to starting phase
        game.getCurrentRound().nextPhase();
        // go to suggest phase
        game.getCurrentRound().nextPhase();
        // test exceptions
        assertThrows(SecurityException.class, () -> game.putSuggestion(10l, suggestion));

        // creating expected suggestions
        Map<Long, String> expected = new HashMap<>();
        expected.put(gameMaster.getUserId(), suggestion);

        // suggesting title
        game.putSuggestion(gameMaster.getUserId(), suggestion);

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
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        String suggestion = "asd";

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // test exceptions
        assertThrows(IllegalStateException.class, () -> game.putSuggestion(1l, suggestion));

        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();
        // go to starting phase
        game.getCurrentRound().nextPhase();
        // go to suggest phase
        game.getCurrentRound().nextPhase();
        // suggest a title
        game.putSuggestion(gameMaster.getUserId(), suggestion);
        // go to vote phase
        game.getCurrentRound().nextPhase();
        // test exceptions
        assertThrows(SecurityException.class, () -> game.putSuggestion(10l, suggestion));

        // creating expected suggestions
        Map<Long, Long> expected = new HashMap<>();
        expected.put(player2.getUserId(), gameMaster.getUserId());

        // voting for the suggestion
        assertThrows(IllegalArgumentException.class,
                () -> game.putVote(gameMaster.getUserId(), gameMaster.getUserId()));

        game.putVote(player2.getUserId(), gameMaster.getUserId());

        // test suggestions
        assertEquals(expected, game.getCurrentRound().getVotes());

    }

    /**
     * This test checks if the max player setting takes affect
     */
    @Test
    void checkMaxPlayerSetting() throws IndexOutOfBoundsException {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("MaxPlayerTest");
        gameSettings.setPassword("");
        // Max Player setting
        gameSettings.setMaxPlayers(3);
        gameSettings.setTotalRounds(2);
        gameSettings.setSubreddit("memes");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        User player4 = new User();
        player4.setUserId(4l);

        // enrolling the players to the game
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // the lobby should now be full so when we try to enroll a player this should
        // return an error
        try {
            game.enrollPlayer(player4, "");
            fail("expected exception did not occur");
        } catch (IndexOutOfBoundsException e) {
            // nothing to see here we want the exeception
        }

    }


    @Test
    void getRoundCounterTest() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("MaxPlayerTest");
        gameSettings.setPassword("");
        // Max Player setting
        gameSettings.setMaxPlayers(3);
        gameSettings.setTotalRounds(2);
        gameSettings.setSubreddit("memes");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // testing virgin state
        assertEquals(0, game.getRoundCounter());

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        User player4 = new User();
        player4.setUserId(4l);

        // enrolling the players to the game
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();

        assertEquals(1, game.getRoundCounter());

    }

    @Test
    void getCurrentRoundTest() {
        // creating objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("MaxPlayerTest");
        gameSettings.setPassword("");
        // Max Player setting
        gameSettings.setMaxPlayers(3);
        gameSettings.setTotalRounds(2);
        gameSettings.setSubreddit("memes");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        // testing virgin state
        assertEquals(null, game.getCurrentRound());

        User player2 = new User();
        player2.setUserId(2l);
        User player3 = new User();
        player3.setUserId(3l);
        User player4 = new User();
        player4.setUserId(4l);

        // enrolling the players to the game
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");

        // closing the lobby to start the game with force
        game.closeLobby(true);
        game.start();

        assertEquals(game.getGameRounds().get(0), game.getCurrentRound());

    }

    @Test
    void skipRoundError() {

        // creating given objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(2);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);

        assertThrows(IllegalStateException.class, () -> game.skipRound());
    }

    @Mock
    private UserRepository userRepository;

    @Test
    void commandsAndAdvancingGameIntegrationTest() {



        // creating given objects
        User gameMaster = new User();
        gameMaster.setUserId(1l);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(3);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);

        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);


        User player2 = new User();
        player2.setUserId(2l);
        player2.setUsername("name");
        User player3 = new User();
        player3.setUserId(3l);
        // enroll 2 more players so the game can start
        game.enrollPlayer(player2, "");
        game.enrollPlayer(player3, "");


        // initializing message to test chat commands
        Message message = new Message();
        message.setSenderId(gameMaster.getUserId());

        // testing ready
        message.setText("/r");
        game.runCommand(message);
        game.update();
        assertEquals(PlayerState.GM_READY, game.getPlayerState(gameMaster.getUserId()));


        // testing ban
        message.setText("/ban @name");
        game.runCommand(message);
        game.update();
        // testing forgiving
        message.setText("/forgive @name");
        game.runCommand(message);
        game.update();


        // initializing the game
        message.setText("/start");
        game.runCommand(message);
        game.update();
        assertEquals(GameState.STARTING, game.getGameState());

        // starting the game
        game.start();
        assertEquals(GameState.RUNNING, game.getGameState());




        // advancing round phase to starting
        message.setText("/a");
        game.runCommand(message);
        game.update();
        assertEquals(RoundPhase.STARTING, game.getCurrentRoundPhase());

        message.setText("/a");
        game.runCommand(message);
        game.update();
        assertEquals(RoundPhase.SUGGEST, game.getCurrentRoundPhase());

        // testing setting a suggestion
        message.setText("/s suggestion");
        game.runCommand(message);
        game.update();
        assertEquals("suggestion", game.getCurrentSuggestions().get(gameMaster.getUserId()));



        // testing pause and resume
        message.setText("/pause");
        game.runCommand(message);
        game.update();
        assertEquals(GameState.PAUSED, game.getGameState());

        message.setText("/resume");
        game.runCommand(message);
        game.update();
        assertEquals(GameState.RUNNING, game.getGameState());


        // further advancing the game
        message.setText("/a");
        game.runCommand(message);
        game.update();
        assertEquals(RoundPhase.VOTE, game.getCurrentRoundPhase());

        // testing voting
        message.setText("/v "+gameMaster.getUserId().toString());
        message.setSenderId(player2.getUserId());
        game.runCommand(message);
        game.update();
        game.getCurrentRound().putVote(player2.getUserId(), gameMaster.getUserId());

        message.setText("/a");
        message.setSenderId(gameMaster.getUserId());
        game.runCommand(message);
        game.update();
        assertEquals(RoundPhase.AFTERMATH, game.getCurrentRoundPhase());


        // skipping a round
        message.setText("/skip");
        game.runCommand(message);
        game.update();
        assertEquals(RoundPhase.STARTING, game.getCurrentRoundPhase());


        // killing the game
        message.setText("/kill");
        game.runCommand(message);
        game.update();
        assertEquals(GameState.ABORTED, game.getGameState());



    }
}