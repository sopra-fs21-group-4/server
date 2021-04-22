package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.constant.RoundPhase;
import util.MemeUrlSupplier;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal Game Representation.
 * handles itself as long as update() is called regularly.
 * inner data structures are hidden from the outside. One can get copies, though.
 * can be manipulated through public methods.
 *
 * how to use this class:
 * 0)   instantiate. From then on update() regularly
 * 1)   initialize()
 * 2a)  change the settings with adaptSettings()
 * 2b)  let players come and go:
 *      * join with enrollPlayer()
 *      * leave with dismissPlayer()
 *      * ban/forgive with banPlayer() / forgivePlayer()
 *      * change game master with promotePlayer()
 *      * note that if the game master leaves, a new one is elected automatically
 * 3)   closeLobby()
 * 4)   start() (or don't, it's called automatically after a fixed time)
 * 5)   pause() / resume() / skipRound ()
 * 6)   as soon as the game has finished, update() returns a GameSummary object. Save it and delete the game.
 * ! the game finishes automatically as soon as all players are dismissed. kill() aborts the game.
 *
 * TODO actually this class doesn't really need user details, it could as well work with the userId.
 */
@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    /* FIELDS */

    @Id
    @GeneratedValue
    private Long gameId; // get only

    @ElementCollection
    private final Map<User, PlayerState> playerStates = new HashMap<>(); // get, put

    @ElementCollection
    private final Map<User, Integer> points = new HashMap<>(); // get // TODO calculate on end of round

    @OneToOne(targetEntity = MessageChannel.class)
    private final MessageChannel gameChat = new MessageChannel(); // get only

    @OneToOne(targetEntity = GameSettings.class)
    private final GameSettings gameSettings = new GameSettings(); // get, adapt

    @Column(nullable = false)
    private GameState gameState = GameState.INIT; // get only

    @OneToMany(targetEntity = GameRound.class, cascade = CascadeType.ALL)
    private final List<GameRound> rounds = new ArrayList<>(); // getCurrentRound

    @Column(nullable = false)
    private Integer roundCounter = 0; // get, skipRound

    @Column
    private Long currentCountdown; // get only

    @Column
    private Long lastUpdateTime; // intern use only

    @OneToOne(targetEntity = GameSummary.class)
    private GameSummary gameSummary;

    /* GETTERS AND SETTERS */
    /* scroll down for special methods */

    public Long getGameId() {
        return gameId;
    }

    public Map<User, PlayerState> getPlayerStates() {
        return new HashMap<>(playerStates);
    } // only returns a copy

    public Map<User, Integer> getPoints() {
        return new HashMap<>(points);
    } // only returns a copy

    public MessageChannel getGameChat() {
        return gameChat;
    }

    public GameSettings getGameSettings() {
        return gameSettings.clone();
    } // only returns a copy

    public GameState getGameState() {
        return gameState;
    }

    public Long getCurrentCountdown() {
        return currentCountdown;
    }

    public Integer getRoundCounter() {
        return gameState.isVirgin()? 0 : roundCounter +1;
    } // add +1 for frontend

    public GameRound getCurrentRound() {
        return rounds.get(roundCounter);
    }

    public GameSummary getGameSummary() {
        return gameSummary;
    }

    public String getName() {
        return gameSettings.getName();
    }

    public Integer getTotalRounds() {
        return gameSettings.getTotalRounds();
    }

    public String getMemeSourceURL() {
        return gameSettings.getMemeSourceURL();
    }

    public MemeType getMemeType() {
        return gameSettings.getMemeType();
    }

    public Integer getMaxSuggestSeconds() {
        return gameSettings.getMaxSuggestSeconds();
    }

    public Integer getMaxVoteSeconds() {
        return gameSettings.getMaxVoteSeconds();
    }

    public Integer getMaxAftermathSeconds() {
        return gameSettings.getMaxAftermathSeconds();
    }

    public Integer getMaxPlayers() {
        return gameSettings.getMaxPlayers();
    }

    /* SPECIAL METHODS */

    /**
     * finds all the players that are enrolled for this game
     * @return a list of the players found
     */
    public synchronized List<User> getEnrolledPlayers() {
        List<User> playerList = new ArrayList<>();
        for (User player : playerStates.keySet()) {
            if (currentPlayerState(player).isEnrolled()) playerList.add(player);
        }
        return playerList;
    }

    /**
     * finds all the players that are enrolled for this game and still present
     * @return a list of the players found
     */
    public synchronized List<User> getPresentPlayers() {
        List<User> playerList = new ArrayList<>();
        for (User player : playerStates.keySet()) {
            if (currentPlayerState(player).isPresent()) playerList.add(player);
        }
        return playerList;
    }

    /**
     * finds all the players that are waiting for the game to start
     * @return a list of the players found
     */
    public synchronized List<User> getReadyPlayers() {
        List<User> playerList = new ArrayList<>();
        for (User player : playerStates.keySet()) {
            if (currentPlayerState(player).isReady()) playerList.add(player);
        }
        return playerList;
    }

    /**
     * finds the game master of this game.
     * note that if the game master left the game, the game will automatically try to elect a new one.
     * @return the player that is game master, or null if there is currently no game master
     */
    public synchronized User getGameMaster() {
        for (User player : playerStates.keySet()){
            if (currentPlayerState(player).isPromoted()) return player;
        }
        return null;
    }

    /**
     * returns a player's current state in this game.
     * never returns null.
     * always use this method instead of playerStates.get(player).
     * @param player the player to check (not null)
     * @return the player's current state in this game
     */
    private synchronized PlayerState currentPlayerState(User player) {
        PlayerState currentPlayerState = playerStates.get(player);
        return (currentPlayerState == null)? PlayerState.STRANGER : currentPlayerState;
    }

    /**
     * marks a player as ready or not
     * @param player the player to switch state (not null)
     * @param ready whether to set ready or to set not ready
     * @return the player's new state in this game
     */
    public synchronized PlayerState setPlayerReady(User player, boolean ready) {
        playerStates.put(player, currentPlayerState(player).readyState(ready));
        return currentPlayerState(player);
    }

    /**
     * promotes a player to game master.
     * demotes the current game master (if any)
     * @param player the player to promote (not null)
     * @return the player's new state in this game
     */
    public synchronized PlayerState promotePlayer(User player) {
        User currentGameMaster = getGameMaster();
        if (currentGameMaster != null)
            playerStates.put(currentGameMaster, currentPlayerState(currentGameMaster).promotedState(false));
        playerStates.put(player, currentPlayerState(player).promotedState(true));
        enrollPlayer(player, null);
        return currentPlayerState(player);
    }

    /**
     * enrolls a player for this game.
     * does nothing if the player joined already.
     * @param player the user to enroll (not null)
     * @return the player's new state in this game
     * @throws IllegalArgumentException if password not accepted
     * @throws IllegalStateException if game is not in the lobby state
     * @throws SecurityException if player is banned
     * @throws IndexOutOfBoundsException if game is full
     */
    public synchronized PlayerState enrollPlayer(User player, String password) {
        if (!gameSettings.acceptsPassword(password))
            throw new IllegalArgumentException("wrong password");
        if (gameState != GameState.LOBBY)
            throw new IllegalStateException("players can only join during the lobby state");
        if (currentPlayerState(player).isBanned())
            throw new SecurityException("player is banned");
        if (getEnrolledPlayers().size() >= gameSettings.getMaxPlayers())
            throw new IndexOutOfBoundsException("game is full");

        PlayerState currentPlayerState = currentPlayerState(player);
        // ignore request if user is already enrolled
        if (currentPlayerState.isEnrolled()) return currentPlayerState;
        // otherwise allow user to join
        playerStates.put(player, PlayerState.ENROLLED);
        gameChat.addParticipant(player);
        return PlayerState.ENROLLED;
    }

    /**
     * removes a player from this game.
     * ignores users that are not enrolled.
     * @param player the user to dismiss (not null)
     * @return the player's new state in this game
     */
    public synchronized PlayerState dismissPlayer(User player) {
        // failing cases
        PlayerState currentPlayerState = currentPlayerState(player);
        if (!currentPlayerState.isEnrolled()) return currentPlayerState;

        // decide how to remove player
        if (gameState == GameState.LOBBY) {
            removePlayer(player, PlayerState.VANISHED);
        } else if (gameState.isOver()) {
            removePlayer(player, PlayerState.LEFT);
        } else {
            removePlayer(player, PlayerState.ABORTED);
        }
        return currentPlayerState(player);
    }

    /**
     * bans a player from this game
     * @param player the player to ban (not null)
     * @return the player's new state in this game: BANNED
     */
    public synchronized PlayerState banPlayer(User player) {
        PlayerState bannedState = (gameState == GameState.LOBBY)?
                PlayerState.BANNED_FROM_LOBBY : PlayerState.BANNED_FROM_GAME;
        removePlayer(player, bannedState);
        return bannedState;
    }

    /**
     * makes a banned player able to join again, given that the game is still in the lobby state.
     * does nothing if the player isn't actually banned.
     * @param player the player to forgive (not null)
     * @return the player's new state in this game
     * @throws IllegalStateException if the game is not in the lobby state
     */
    public synchronized PlayerState forgivePlayer(User player) {
        if (gameState != GameState.LOBBY)
            throw new IllegalStateException("cannot forgive players once the game started");
        PlayerState currentPlayerState = currentPlayerState(player);
        if (!currentPlayerState.isBanned()) return currentPlayerState;
        playerStates.put(player, PlayerState.VANISHED);
        return PlayerState.VANISHED;
    }

    /**
     * removes a player from the game and from the game chat.
     * don't call this method directly! use dismissPlayer or banPlayer instead.
     * @param player the player to remove (not null)
     * @param playerState the state to assign to the player
     */
    private synchronized void removePlayer(User player, PlayerState playerState) {
        assert(!playerState.isPresent());
        playerStates.put(player, playerState);
        this.gameChat.removeParticipant(player);
        checkPlayerList();
    }

    /**
     * makes sure that there's enough players and a game master.
     * if there are not enough players to continue the game, termination is initialized.
     * should be called after removing players in any way.
     */
    private synchronized void checkPlayerList() {
        List<User> remainingPlayers = getPresentPlayers();
        if (remainingPlayers.size() < gameState.minPlayers()) {
            gameState = gameState.abandoningState();
        } else if (getGameMaster() == null) {
            promotePlayer(remainingPlayers.get(0));
        }
    }

    /**
     * adapts all non-null settings of a GameSettings instance
     * @param settings the instance to adapt from
     */
    public synchronized void adaptSettings(GameSettings settings) {
        if (settings.getName() != null)
            this.gameSettings.setName(settings.getName());
        if (settings.getPassword() != null)
            this.gameSettings.setPassword(settings.getPassword());
        if (settings.getMaxPlayers() != null)
            this.gameSettings.setMaxPlayers(settings.getMaxPlayers());
        if (settings.getTotalRounds() != null)
            this.gameSettings.setTotalRounds(settings.getTotalRounds());
        if (settings.getMemeSourceURL() != null)
            this.gameSettings.setMemeSourceURL(settings.getMemeSourceURL());
        if (settings.getMemeType() != null)
            this.gameSettings.setMemeType(settings.getMemeType());
        if (settings.getMaxSuggestSeconds() != null)
            this.gameSettings.setMaxSuggestSeconds(settings.getMaxSuggestSeconds());
        if (settings.getMaxVoteSeconds() != null)
            this.gameSettings.setMaxVoteSeconds(settings.getMaxVoteSeconds());
        if (settings.getMaxAftermathSeconds() != null)
            this.gameSettings.setMaxAftermathSeconds(settings.getMaxAftermathSeconds());
    }

    /**
     * sets the creator of this game as its game master (without asking for a password)
     * and opens the lobby for this game
     * @param creator
     */
    public synchronized void initialize(User creator) {
        if (gameState != GameState.INIT)
            throw new IllegalStateException("game was already initialized");
        playerStates.put(creator, PlayerState.GAME_MASTER);
        gameState = GameState.LOBBY;
    }

    /**
     * leaves the lobby phase and initializes the game rounds.
     * does nothing if the game is not in the lobby state
     * @param force set true if the lobby should be closed regardless of whether players are ready or not.
     * @throws IllegalStateException if not forced and not all players are ready.
     */
    public synchronized void closeLobby(boolean force) {
        if (gameState != GameState.LOBBY) return;
        if (!force && (getReadyPlayers().size() < getPresentPlayers().size()))
            throw new IllegalStateException("not all players ready");

        gameState = GameState.STARTING;
        String baseURL = gameSettings.getMemeSourceURL();
        String config = gameSettings.getMemeType().toString();
        MemeUrlSupplier memeUrlSupplier = MemeUrlSupplier.create(baseURL, config);

        for (int i = 0; i < gameSettings.getTotalRounds(); i++) {
            GameRound round = new GameRound();
            round.setTitle(String.format("Round %d",(i+1)));
            round.setMemeURL(memeUrlSupplier.get());
            this.rounds.add(round);
        }
        // 5 seconds until game starts
        setCountdown(5000L);
    }

    /**
     * starts the game.
     * sets the game state to running and all player states to active
     * @throws IllegalStateException if the game is not in the starting state
     */
    public synchronized void start() {
        if (gameState != GameState.STARTING)
            throw new IllegalStateException("can only start from starting state");
        // set all player states to active
        for (User player : getEnrolledPlayers()) {
            playerStates.put(player, currentPlayerState(player).activeState());
        }
        // go to running state
        this.gameState = GameState.RUNNING;
        // set countdown to 0, so game will advance on next update
        currentCountdown = 0L;
    }

    /**
     * sets the game on pause
     * @throws IllegalStateException if the game is not in an active state
     */
    public synchronized void pause() {
        if (!gameState.isActive())
            throw new IllegalStateException("cannot pause game in phase "+ gameState.toString());

        // need to update the countdown before going to sleep
        // time measure will be lost while paused
        updateCountdown();
        this.gameState = GameState.PAUSED;
    }

    /**
     * resumes the game if it was paused
     * @throws IllegalStateException if the game is not in an active state
     */
    public synchronized void resume() {
        if (!gameState.isActive())
            throw new IllegalStateException("cannot resume game in phase "+ gameState.toString());

        if (gameState == GameState.RUNNING) return;
        gameState = GameState.RUNNING;
        // start measuring time again
        setCountdown(currentCountdown);
    }

    /**
     * terminates the game instantly and removes all the players.
     */
    public synchronized void kill() {
        if (!gameState.isOver()) gameState = GameState.ABORTED;
        for (User player : getPresentPlayers()) dismissPlayer(player);
    }

    /**
     * if the game is dead, returns a summary of it
     * if the game is running, updates the countdown and advances if the latter ran out.
     */
    public synchronized GameSummary update() {
        switch(gameState) {
            case STARTING:  // update countdown. If it ran out, start.
                            if (updateCountdown() < 0) start();
                            return null;

            case RUNNING:   // update countdown. If it ran out, advance.
                            if (updateCountdown() < 0) advance();
                            return null;

            case ABORTED:
            case ABANDONED:
            case FINISHED:  if (gameSummary != null) return gameSummary;
                            // summarize
                            GameSummary summary = new GameSummary();
                            summary.adapt(this);
                            return summary;

            default:        return null;
        }
    }

    /**
     * sets the countdown to a given value and resets time measure.
     * @param millis milliseconds to set the countdown to
     */
    private synchronized void setCountdown(long millis) {
        currentCountdown = millis;
        lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * subtracts all the measured time passed since the last countdown update.
     * @return the new timer value (milliseconds until game should advance)
     */
    private synchronized long updateCountdown() {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - this.lastUpdateTime;
        this.currentCountdown -= timeElapsed;
        this.lastUpdateTime = currentTime;
        return currentCountdown;
    }

    /**
     * Will advance the current round into the next phase
     * or issue the next round if the current one has ended.
     * Automatically sets the countdown.
     */
    private void advance() {
        assert (gameState == GameState.RUNNING);
        getCurrentRound().nextPhase();
        // set timer for next round phase or go to next round
        switch(getCurrentRound().getPhase()) {
            case QUEUED:    throw new AssertionError("current round should not be in phase QUEUED");

            case STARTING:  setCountdown(3000L);    // 3 seconds for players to prepare for new round
                            break;
            case SUGGEST:   setCountdown(gameSettings.getMaxSuggestSeconds() * 1000L);
                            break;
            case VOTE:      setCountdown(gameSettings.getMaxVoteSeconds() * 1000L);
                            break;
            case AFTERMATH: setCountdown(gameSettings.getMaxAftermathSeconds() * 1000L);
                            break;
            case CLOSED:    skipRound();
                            break;
        }
    }

    /**
     * Closes the current round and advances the next one.
     * Sets the game phase to AFTERMATH if this was the last round
     * @throws IllegalStateException if the game is not in an active state
     */
    public synchronized void skipRound() {
        if (!gameState.isActive())
            throw new IllegalStateException("can only skip rounds in an active game state");

        // close current round
        getCurrentRound().close();

        // issue next round
        this.roundCounter++;

        if (gameSettings.getTotalRounds().equals(roundCounter)){
            this.gameState = GameState.AFTERMATH;
        } else {
            assert(getCurrentRound().getPhase() == RoundPhase.QUEUED);
            // set timer to 0, so game will advance on next update
            currentCountdown = 0L;
        }
    }

    /**
     * puts a suggestion of a player in the current round
     * @param player the player who suggests
     * @param suggestion suggested meme title
     * @throws SecurityException if the player is not enrolled
     * @throws IllegalStateException if either the game's state or the current round's state doesn't allow suggestions
     */
    public synchronized void putSuggestion(User player, String suggestion) {
        if (!currentPlayerState(player).isEnrolled())
            throw new SecurityException("player is not enrolled");
        if (!gameState.isActive())
            throw new IllegalStateException("can only suggest in an active state");
        if (!getCurrentRound().getPhase().allowsSuggestions())
            throw new IllegalStateException("current round phase doesn't allow suggestions");

        getCurrentRound().putSuggestion(player, suggestion);
    }

    /**
     * puts a vote of a player in the current round
     * @param player the player who votes
     * @param vote userId of the player to vote for
     * @throws SecurityException if the player is not enrolled
     * @throws IllegalStateException if either the game's state or the current round's state doesn't allow suggestions
     */
    public synchronized void putVote(User player, Long vote) {
        if (!currentPlayerState(player).isEnrolled())
            throw new SecurityException("player is not enrolled");
        if (!gameState.isActive())
            throw new IllegalStateException("can only suggest in an active state");
        if (!getCurrentRound().getPhase().allowsVotes())
            throw new IllegalStateException("current round phase doesn't allow suggestions");

        getCurrentRound().putVote(player, vote);
    }

    /**
     * creates a list of game round summaries up to the current round counter
     * @return list of game round summaries
     */
    public List<GameRoundSummary> summarizePastRounds() {
        List<GameRoundSummary> summaries = new ArrayList<>();
        for (int i = 0; i < this.roundCounter; i++) {
            GameRoundSummary summary = new GameRoundSummary();
            summary.adapt(rounds.get(i));
            summaries.add(summary);
        }
        return summaries;
    }

}
