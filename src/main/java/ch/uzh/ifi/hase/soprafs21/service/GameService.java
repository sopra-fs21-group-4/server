package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
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

    private final Random random = new Random();

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
        List<Game> deleteList = new ArrayList<>();
        for (Game game : getRunningGames()) {
            switch(game.update()) {
                case MODIFIED:      gameRoundRepository.flush();
                                    break;
                case DEAD:          deleteList.add(game);
                case COMPLETE:      GameSummary summary = game.getGameSummary();
                                    if (summary == null) break;
                                    for (Long userId : summary.getScores().keySet()) {
                                        User user = userRepository.findByUserId(userId);
                                        if (user != null) {
                                            user.addToGameHistory(summary.getGameSummaryId());
                                            userRepository.flush();
                                        }
                                    }
                default:            break;
            }
        }
        gameRepository.deleteAll(deleteList);
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

        Long previousGameId = gameMaster.getCurrentGameId();
        Game previousGame = previousGameId == null? null : gameRepository.findByGameId(previousGameId);
        if (previousGame != null) previousGame.dismissPlayer(gameMaster);

        Game game = new Game()
                .adaptSettings(gameSettings)
                .setGameId(randomGameId())
                .initialize(gameMaster);

        gameMaster.setCurrentGameId(game.getGameId());
        userRepository.flush();

        // put chat bot to repo // unused feature
//        User chatBot = game.getChatBot();
//        userRepository.save(chatBot);
//        userRepository.flush();
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
        gameRoundRepository.saveAll(gameRounds);
        gameRoundRepository.flush();
        // put game to repo
        gameRepository.save(game);
        gameRepository.flush();

        log.debug("Created new Game: {}", game);
        return game;
    }

    /**
     * updates a game's settings
     * @param gameId
     * @param gameMaster
     * @param gameSettings
     * @return the game
     */
    public Game adaptGameSettings(Long gameId, User gameMaster, GameSettings gameSettings) {
        Game game = verifyGameMaster(gameId, gameMaster.getUserId());
        try {
            game = game.adaptSettings(gameSettings);
            gameSettingsRepository.flush();
        } catch(IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.GONE, "game is already running");
        }
        return game;
    }

    /**
     * join a game
     * @param user the user who wants to join
     * @param gameId id of the game to join
     * @param password password of the game (doesn't matter if not demanded by the game)
     * @return the game
     */
    public Game joinGame(Long gameId, User user, String password) {
        try {
            Game gameToJoin = findRunningGame(gameId);
            if (gameToJoin.getPlayerState(user.getUserId()).isEnrolled()) return gameToJoin;
            Long previousGameId = user.getCurrentGameId();
            Game previousGame = previousGameId == null? null : gameRepository.findByGameId(previousGameId);
            if (previousGame != null) previousGame.dismissPlayer(user);
            gameToJoin.enrollPlayer(user, password);
            user.setCurrentGameId(gameId);
            gameRepository.flush();
            userRepository.flush();
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
        user.setCurrentGameId(null);
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
     * verifies that a user is the game master of a game
     * @param gameId
     * @param userId
     * @return the game instance found in the repository
     */
    public Game verifyGameMaster(Long gameId, Long userId) {
        Game game = findRunningGame(gameId);
        if (!game.getPlayerState(userId).isPromoted())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you are not the game master of this game");
        return game;
    }

    /**
     * starts a game
     * @param gameId
     * @param userId game master
     */
    public void startGame(Long gameId, Long userId, boolean force) {
        Game game = verifyGameMaster(gameId, userId);
        try {
            game.closeLobby(force);
        } catch(IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.GONE, "game is already running");
        }
    }

    /**
     * sets a player to the ready state or to the unready state respectively
     * @param gameId
     * @param user
     * @param ready whether the player is ready or not
     */
    public void setPlayerReady(Long gameId, User user, boolean ready) {
        Game game = verifyPlayer(gameId, user);
        game.setPlayerReady(user.getUserId(), ready);
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
            gameRoundRepository.flush();
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
            gameRoundRepository.flush();
        } catch(SecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not enrolled for this game");
        } catch(IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "the game's current state doesn't allow votes");
        }
    }

    /**
     * bans a player from a game
     * @param gameId
     * @param userId game master
     */
    public void banPlayer(Long gameId, Long gameMasterId, Long userId) {
        Game game = verifyGameMaster(gameId, gameMasterId);
        User user = userRepository.findByUserId(userId);
        try {
            game.banPlayer(user);
            gameRepository.flush();
        } catch(IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    public Long randomGameId() {
        long randomId;
        do {
            randomId = random.nextLong() & 0xFFFFFFFFC0L;
        } while (!availableGameId(randomId));
        return randomId;
    }

    private boolean availableGameId(Long id) {
        if (id < 0xFFFFFFFL) return false;
        for (int i = 0; i < 64; i++) if (EntityType.get(id) != EntityType.UNKNOWN) return false;
        return true;
    }
}
