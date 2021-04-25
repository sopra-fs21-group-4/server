package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.*;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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


    @Autowired
    public GameService(
            @Qualifier("gameRepository") GameRepository gameRepository,
            @Qualifier("gameSummaryRepository") GameSummaryRepository gameSummaryRepository,
            @Qualifier("gameSettingsRepository") GameSettingsRepository gameSettingsRepository,
            @Qualifier("gameRoundRepository") GameRoundRepository gameRoundRepository,
            @Qualifier("gameRoundSummaryRepository") GameRoundSummaryRepository gameRoundSummaryRepository,
            @Qualifier("messageChannelRepository") MessageChannelRepository messageChannelRepository
    ) {
        this.gameRepository = gameRepository;
        this.gameSettingsRepository = gameSettingsRepository;
        this.gameSummaryRepository = gameSummaryRepository;
        this.gameRoundRepository = gameRoundRepository;
        this.gameRoundSummaryRepository = gameRoundSummaryRepository;
        this.messageChannelRepository = messageChannelRepository;
    }

    /**
     * Loop running every 200ms to update lobby states
     */
    @Scheduled(fixedRate=200)
    public void updateLobbies(){
        List<Game> games = gameRepository.findAll();
        for (Game game : games) {
            GameSummary summary = game.update();
            // summary returned means game is dead
            if (summary != null) {
                gameSummaryRepository.save(summary);
                gameRepository.delete(game);
            }
        }
        gameSummaryRepository.flush();
        gameRepository.flush();
        // TODO not sure if we need to flush other repositories as well if objects are modified by a game
    }


    public List<Game> getGames() {
        return gameRepository.findAll();
    }

    /**
     * finds a game in the repository
     * @param gameId
     * @return game
     * @throws ResponseStatusException 404 if not found
     */
    public Game findExistingGame(Long gameId) {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
        return game;
    }

    /**
     * creates a new game
     * @param user creator, hence game master
     * @param gameSettings
     * @return
     */
    public Game createGame(User user, GameSettings gameSettings) {

        Game game = new Game();
        game.initialize(user);
        game.adaptSettings(gameSettings);

        // put game chat to repository
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
        // put game to repository
        game = gameRepository.save(game);
        gameRepository.flush();
        // TODO do these repositories update automatically when the game modifies its objects?

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
            Game gameToJoin = findExistingGame(gameId);
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
        Game game = findExistingGame(gameId);
        game.dismissPlayer(user);
    }

    /**
     * verifies that a user is enrolled for a game
     * @param gameId
     * @param user
     * @return the game instance found in the repository
     */
    public Game verifyPlayer(Long gameId, User user) {
        Game game = findExistingGame(gameId);
        if (!game.getEnrolledPlayers().contains(user))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not enrolled for this game");
        return game;
    }

    /**
     * starts a game
     * @param gameId
     * @param user game master
     */
    public void startGame(Long gameId, User user, boolean force) {
        Game game = findExistingGame(gameId);

        if (!game.getGameMaster().equals(user))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the game master can start the game");

        try {
            game.closeLobby(force);
        } catch(IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.GONE, "game is already running");
        }
    }

    /**
     * TODO this is not the best solution, maybe replace it at some point
     * runs a command that only the game master can.
     * eg: force start, ban, forgive TODO anything else?
     * @param gameId
     * @param gameMaster
     * @param command
     * @param target user targeted by the command
     */
    public void runGameMasterCommand(Long gameId, User gameMaster, String command, Optional<User> target) {
        Game game = findExistingGame(gameId);

        if (!game.getGameMaster().equals(gameMaster))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the game master can use this command");

        String[] commandSegment = command.split(" ");

        try {
            switch(commandSegment[0]) {
                case "/start":      startGame(gameId, gameMaster, true);
                                    break;
                case "/ban":        game.banPlayer(target.get());
                                    break;
                case "/forgive":    game.forgivePlayer(target.get());
                                    break;

                default: throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unknown command");
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this command needs to specify a target");
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
            game.putSuggestion(user, suggestion);

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
            game.putVote(user, vote);

        } catch(SecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not enrolled for this game");
        } catch(IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "the game's current state doesn't allow votes");
        }
    }



}
