package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Lobby Service
 * responsible for all actions relating the lobbies
 */
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final GameSummaryRepository gameSummaryRepository;
    private final GameSettingsRepository gameSettingsRepository;
    private final GameRoundRepository gameRoundRepository;
    private final GameRoundSummaryRepository gameRoundSummaryRepository;
    private final MessageChannelRepository messageChannelRepository;
    private final UserRepository userRepository;


    @Autowired
    public GameService(
            @Qualifier("gameRepository") GameRepository gameRepository,
            @Qualifier("gameSummaryRepository") GameSummaryRepository gameSummaryRepository,
            @Qualifier("gameSettingsRepository") GameSettingsRepository gameSettingsRepository,
            @Qualifier("gameRoundRepository") GameRoundRepository gameRoundRepository,
            @Qualifier("gameRoundSummaryRepository") GameRoundSummaryRepository gameRoundSummaryRepository,
            @Qualifier("messageChannelRepository") MessageChannelRepository messageChannelRepository,
            @Qualifier("userRepository") UserRepository userRepository
    ) {
        this.gameRepository = gameRepository;
        this.gameSettingsRepository = gameSettingsRepository;
        this.gameSummaryRepository = gameSummaryRepository;
        this.gameRoundRepository = gameRoundRepository;
        this.gameRoundSummaryRepository = gameRoundSummaryRepository;
        this.messageChannelRepository = messageChannelRepository;
        this.userRepository = userRepository;
    }

    /**
     * Loop running every 200ms to update lobby states
     */
    @Scheduled(fixedRate=200)
    public void updateLobbies(){
        for (Game game : getRunningGames()) {
            switch(game.update()) {
                case UPDATED:       for (GameRound gameRound : game.getGameRounds()) {
                                        gameRoundRepository.save(gameRound);
                                    }
                                    break;
                case DEAD:          GameSummary summary = game.getGameSummary();
                                    gameSummaryRepository.save(summary);
                                    for (GameRoundSummary roundSummary : summary.getRounds()) {
                                        gameRoundSummaryRepository.save(roundSummary);
                                    }
                                    gameRepository.delete(game);
                                    break;
                default:            break;
            }
        }
        messageChannelRepository.flush();
        gameRoundSummaryRepository.flush();
        gameSummaryRepository.flush();
        gameRoundRepository.flush();
        gameRepository.flush();
    }

    public Collection<Game> getRunningGames() {
        return gameRepository.findAll();
    }

    /**
     * finds a game in the repository
     * @param gameId
     * @return game
     * @throws ResponseStatusException 404 if not found
     */
    public Game findRunningGame(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
        return game;
    }

    /**
     * creates a new game
     * @param gameMaster
     * @param gameSettings
     * @return
     */
    public Game createGame(User gameMaster, GameSettings gameSettings) {

        Game game = new Game();
        game.adaptSettings(gameSettings);
        game.setGameId(randomGameId());
        game.initialize(gameMaster.getUserId());

        // put chat bot to repo
        User chatBot = game.getChatBot();
        userRepository.save(chatBot);
        userRepository.flush();
        // put game chat to repo
        MessageChannel gameChat = game.getGameChat();
        messageChannelRepository.save(gameChat);
        messageChannelRepository.flush();
        // put game settings to repository
        gameSettings = game.getGameSettings();
        gameSettingsRepository.save(gameSettings);
        gameSettingsRepository.flush();
        // put game rounds to repository
        List<GameRound> gameRounds = game.getGameRounds();
        for (GameRound gameRound : gameRounds) gameRoundRepository.save(gameRound);
        gameRoundRepository.flush();
        // put game to repo
        gameRepository.save(game);

        log.debug("Created new Game: {}", game);
        return game;
    }

    /**
     * join a game
     * @param user
     * @param gameId
     * @param password
     * @return
     */
    public Game joinGame(Long gameId, User user, String password) {
        try {
            Game gameToJoin = findRunningGame(gameId);
            if (gameToJoin.getPlayerState(user.getUserId()).isEnrolled()) return gameToJoin;
            Game previousGame = user.getCurrentGame();
            if (previousGame != null) previousGame.dismissPlayer(user);
            gameToJoin.enrollPlayer(user, password);
            user.setCurrentGame(gameToJoin);
            return gameToJoin;

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong password");
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.GONE, "game is already running");
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "you are banned from this game");
        } catch (IndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "game is full");
        }
    }

    /**
     * removes a user from a game
     * @param gameId
     * @param user
     */
    public void leaveGame(Long gameId, User user) {
        Game game = findRunningGame(gameId);
        game.dismissPlayer(user);
    }

    /**
     * verifies that a user is enrolled for a game
     * @param gameId
     * @param user
     * @return the game instance found in the repository
     */
    public Game verifyPlayer(Long gameId, User user) {
        Game game = findRunningGame(gameId);
        if (!game.getPlayerState(user.getUserId()).isEnrolled())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not enrolled for this game");
        return game;
    }

    /**
     * starts a game
     * @param gameId
     * @param user game master
     */
    public void startGame(Long gameId, User user, boolean force) {
        Game game = findRunningGame(gameId);

        if (!game.getGameMaster().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the game master can start the game");

        try {
            game.closeLobby(force);
        } catch(IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.GONE, "game is already running");
        }
    }

    /**
     * puts a suggested meme title for the specified user to the specified game's current round
     * @param gameId
     * @param user
     * @param suggestion suggested meme title
     */
    public void putSuggestion(Long gameId, User user, String suggestion) {
        Game game = verifyPlayer(gameId, user);
        try {
            game.putSuggestion(user.getUserId(), suggestion);

        } catch(SecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not enrolled for this game");
        } catch(IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "the game's current state doesn't allow suggestions");
        }
    }

    /**
     * puts a vote for the specified user to the specified game's current round
     * @param gameId
     * @param user
     * @param vote userId of the user to vote for
     */
    public void putVote(Long gameId, User user, Long vote) {
        Game game = verifyPlayer(gameId, user);
        try {
            game.putVote(user.getUserId(), vote);

        } catch(SecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not enrolled for this game");
        } catch(IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "the game's current state doesn't allow votes");
        }
    }

    public Long randomGameId() {
        Random r = new Random();
        long randomId;
        do {
            randomId = r.nextLong() & 0xFFFFFFFFFFL;
        } while (gameRepository.existsById(randomId) || gameSummaryRepository.existsById(randomId));
        return randomId;
    }

}
