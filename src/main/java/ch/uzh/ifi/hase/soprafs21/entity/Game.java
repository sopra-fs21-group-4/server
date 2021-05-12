package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.*;
import util.MemeUrlSupplier;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

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
 */
@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    // TODO sort attributes, getters and setters

    /* FIELDS */

    @Id
    private Long gameId; // get only

    @ElementCollection
    private final Map<Long, PlayerState> playerStates = new HashMap<>(); // get

    @ElementCollection
    private final Map<Long, Integer> scores = new HashMap<>(); // get

    @OneToOne(targetEntity = MessageChannel.class, cascade = CascadeType.PERSIST)
    private final MessageChannel gameChat = new MessageChannel(); // get only

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL)    // TODO cascade delete
    private final User chatBot = new User();

    @OneToOne(targetEntity = GameSettings.class)
    private final GameSettings gameSettings = new GameSettings(); // get, adapt

    @Column(nullable = false)
    private GameState gameState = GameState.INIT; // get only

    @OneToMany(targetEntity = GameRound.class, cascade = CascadeType.ALL)
    private final List<GameRound> gameRounds = new ArrayList<>(); // get, getCurrentRound

    @Column
    private Integer roundCounter = -1; // get, skipRound

    @Column
    private Long advanceTargetTime; // get only

    @Column
    private Long remainingCountdown; // internal use only

    @OneToOne(targetEntity = GameSummary.class)
    private GameSummary gameSummary;

    @Column
    private Long lastModified;

    /* CONSTRUCTOR */

    public Game() {
        super();
    }

    /* GETTERS AND SETTERS */
    /* scroll down for special methods */

    public Long getGameId() {
        return gameId;
    }

    public Game setGameId(Long gameId) {
        if (this.gameId != null) throw new IllegalStateException();
        this.gameId = gameId;
        this.lastModified = System.currentTimeMillis();
        return this;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public MessageChannel getGameChat() {
        return gameChat;
    }

    public User getChatBot() {
        return chatBot;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    } // hope nobody modifies the settings through this

    public GameState getGameState() {
        return gameState;
    }

    private Long getCurrentCountdown() {
        return advanceTargetTime - System.currentTimeMillis();
    }

    public Long getAdvanceTargetTime() {
        return advanceTargetTime;
    }

    public Integer getRoundCounter() {
        return gameState.isVirgin()? 0 : roundCounter +1;
    } // add +1 for frontend

    public List<GameRound> getGameRounds() {
        return this.gameRounds;
    }

    public GameRound getCurrentRound() {
        return gameState.isActive()? gameRounds.get(roundCounter) : null;
    }

    public GameSummary getGameSummary() {
        if (gameState.isOver() && gameSummary == null) {
            gameSummary = new GameSummary().adapt(this);
        }
        return gameSummary;
    }

    public String getName() {
        return gameSettings.getName();
    }

    public Integer getTotalRounds() {
        return gameSettings.getTotalRounds();
    }

    public MemeType getMemeType() {
        return gameSettings.getMemeType();
    }

    public String getSubreddit() {
        return gameSettings.getSubreddit();
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

    public String getCurrentRoundTitle() {
        GameRound currentRound = getCurrentRound();
        return currentRound == null? null : currentRound.getTitle();
    }

    public RoundPhase getCurrentRoundPhase() {
        GameRound currentRound = getCurrentRound();
        return currentRound == null? null : currentRound.getRoundPhase();
    }

    public Map<Long, String> getCurrentSuggestions() {
        GameRound currentRound = getCurrentRound();
        return currentRound == null? null : currentRound.getSuggestions();
    }

    public Map<Long, Long> getCurrentVotes() {
        GameRound currentRound = getCurrentRound();
        return currentRound == null? null : currentRound.getVotes();
    }

    public Map<Long, Integer> getCurrentScores() {
        GameRound currentRound = getCurrentRound();
        return currentRound == null? null : currentRound.getScores();
    }

    public String getCurrentMemeURL() {
        GameRound currentRound = getCurrentRound();
        return currentRound == null? null : currentRound.getMemeURL();
    }

    public Map<Long, PlayerState> getPlayerStates() {
        return playerStates;
    }

    public List<String> getMemesFound() {
        return gameSettings.getMemesFound();
    }

    // TODO sort getters and setters

    public Long getLastModified() {
        return lastModified;
    }


    /* SPECIAL METHODS */

    /**
     * finds all the players that are enrolled for this game
     * @return a list of the players found
     */
    public synchronized List<Long> getEnrolledPlayers() {
        List<Long> playerList = new ArrayList<>();
        for (Long player : playerStates.keySet()) {
            if (getPlayerState(player).isEnrolled()) playerList.add(player);
        }
        return playerList;
    }

    /**
     * finds all the players that are enrolled for this game and still present
     * @return a list of the players found
     */
    public synchronized List<Long> getPresentPlayers() {
        List<Long> playerList = new ArrayList<>();
        for (Long player : playerStates.keySet()) {
            if (getPlayerState(player).isPresent()) playerList.add(player);
        }
        return playerList;
    }

    /**
     * finds all the players that are waiting for the game to start
     * @return a list of the players found
     */
    public synchronized List<Long> getReadyPlayers() {
        List<Long> playerList = new ArrayList<>();
        for (Long player : playerStates.keySet()) {
            if (getPlayerState(player).isReady()) playerList.add(player);
        }
        return playerList;
    }

    /**
     * finds the game master of this game.
     * note that if the game master left the game, the game will automatically try to elect a new one.
     * @return the player that is game master, or null if there is currently no game master
     */
    public synchronized Long getGameMaster() {
        for (Long player : playerStates.keySet()){
            if (getPlayerState(player).isPromoted()) return player;
        }
        return null;
    }

    /**
     * returns a player's current state in this game.
     * never returns null!
     * @param player the player to check (not null)
     * @return the player's current state in this game
     */
    public synchronized PlayerState getPlayerState(Long player) {
        PlayerState currentPlayerState = playerStates.get(player);
        return (currentPlayerState == null)? PlayerState.STRANGER : currentPlayerState;
    }

    /**
     * marks a player as ready or not
     * @param player the player to switch state (not null)
     * @param ready whether to set ready or to set not ready
     * @return the player's new state in this game
     */
    public synchronized PlayerState setPlayerReady(Long player, boolean ready) {
        playerStates.put(player, getPlayerState(player).readyState(ready));
        this.lastModified = System.currentTimeMillis();
        return getPlayerState(player);
    }

    /**
     * promotes a player to game master.
     * demotes the current game master (if any)
     * @param player the player to promote (not null)
     * @return the player's new state in this game
     */
    public synchronized PlayerState promotePlayer(Long player) {
        Long currentGameMaster = getGameMaster();
        if (currentGameMaster != null)
            playerStates.put(currentGameMaster, getPlayerState(currentGameMaster).promotedState(false));
        playerStates.put(player, getPlayerState(player).promotedState(true));
        this.lastModified = System.currentTimeMillis();
        // TODO undo if didn't work
        return getPlayerState(player);
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
        PlayerState currentPlayerState = getPlayerState(player.getUserId());
        // ignore request if user is already enrolled
        if (currentPlayerState.isEnrolled()) return currentPlayerState;

        // failing cases
        if (!gameSettings.acceptsPassword(password))
            throw new IllegalArgumentException("wrong password");
        if (gameState != GameState.LOBBY)
            throw new IllegalStateException("players can only join during the lobby state");
        if (getPlayerState(player.getUserId()).isBanned())
            throw new SecurityException("player is banned");
        if (getEnrolledPlayers().size() >= gameSettings.getMaxPlayers())
            throw new IndexOutOfBoundsException("game is full");

        // otherwise allow user to join
        addPlayer(player, PlayerState.ENROLLED);
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
        PlayerState currentPlayerState = getPlayerState(player.getUserId());
        if (!currentPlayerState.isEnrolled()) return currentPlayerState;

        // decide how to remove player
        switch(gameState) {
            case LOBBY:     return removePlayer(player, PlayerState.VANISHED);
            case STARTING:
            case RUNNING:
            case PAUSED:    return removePlayer(player, PlayerState.ABORTED);
            default:        return removePlayer(player, PlayerState.LEFT);
        }
    }

    /**
     * bans a player from this game
     * @param player the player to ban (not null)
     * @return the player's new state in this game: BANNED
     * @throws IllegalArgumentException if the given player is the game master
     */
    public synchronized PlayerState banPlayer(User player) {
        if (getPlayerState(player.getUserId()).isPromoted())
            throw new IllegalArgumentException("can't ban the game master");
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
    public synchronized PlayerState forgivePlayer(Long player) {
        if (gameState != GameState.LOBBY)
            throw new IllegalStateException("cannot forgive players once the game started");
        PlayerState currentPlayerState = getPlayerState(player);
        if (!currentPlayerState.isBanned()) return currentPlayerState;
        playerStates.put(player, PlayerState.VANISHED);
        this.lastModified = System.currentTimeMillis();
        return PlayerState.VANISHED;
    }

    /**
     * adds a player to the game and to the game chat.
     * don't call this method directly! use enrollPlayer instead.
     * @param player the player to remove (not null)
     * @param playerState the state to assign to the player
     */
    private synchronized void addPlayer(User player, PlayerState playerState) {
        assert(playerState.isEnrolled());
        // TODO check max players
        playerStates.put(player.getUserId(), playerState);
        this.gameChat.addParticipant(player);
        this.lastModified = System.currentTimeMillis();
        checkPlayerList(); // TODO
    }

    /**
     * removes a player from the game and from the game chat.
     * don't call this method directly! use dismissPlayer or banPlayer instead.
     * @param player the player to remove (not null)
     * @param playerState the state to assign to the player
     */
    private synchronized PlayerState removePlayer(User player, PlayerState playerState) {
        assert(!playerState.isPresent());
        playerStates.put(player.getUserId(), playerState);
        gameChat.removeParticipant(player);
        checkPlayerList();
        this.lastModified = System.currentTimeMillis();
        return playerState;
    }

    /**
     * makes sure that there's enough players and a game master.
     * if there are not enough players to continue the game, termination is initialized.
     * should be called after removing players in any way.
     */
    private synchronized void checkPlayerList() {
        List<Long> remainingPlayers = getPresentPlayers();
        if (remainingPlayers.size() < gameState.minPlayers()) {
            gameState = gameState.abandoningState();
            kill();
        } else if (getGameMaster() == null) {
            promotePlayer(remainingPlayers.get(0));
        }
    }

    /**
     * adapts all non-null settings of a GameSettings instance
     * @param settings the instance to adapt from
     */
    public synchronized Game adaptSettings(GameSettings settings) {
        if (!gameState.isVirgin())
            throw new IllegalStateException("game has already started");
        if (settings.getName() != null)
            this.gameSettings.setName(settings.getName());
        if (settings.getPassword() != null)
            this.gameSettings.setPassword(settings.getPassword());
        if (settings.getMaxPlayers() != null)
            this.gameSettings.setMaxPlayers((Math.max(settings.getMaxPlayers(), this.getEnrolledPlayers().size())));
        if (settings.getSubreddit() != null)
            this.gameSettings.setSubreddit(settings.getSubreddit());
        if (settings.getMemeType() != null)
            this.gameSettings.setMemeType(settings.getMemeType());
        if (settings.getTotalRounds() != null)
            this.gameSettings.setTotalRounds(Math.max(1, settings.getTotalRounds()));
        if (settings.getMaxSuggestSeconds() != null)
            this.gameSettings.setMaxSuggestSeconds(settings.getMaxSuggestSeconds());
        if (settings.getMaxVoteSeconds() != null)
            this.gameSettings.setMaxVoteSeconds(settings.getMaxVoteSeconds());
        if (settings.getMaxAftermathSeconds() != null)
            this.gameSettings.setMaxAftermathSeconds(settings.getMaxAftermathSeconds());
        this.lastModified = System.currentTimeMillis();
        return this;
    }

    /**
     * sets the creator of this game as its game master (without asking for a password)
     * and opens the lobby for this game
     * @param gameMaster the creator of this game
     */
    public synchronized Game initialize(User gameMaster) {
        if (gameState != GameState.INIT)
            throw new IllegalStateException("game was already initialized");
        // init chat bot
        this.chatBot.setCurrentGameId(gameId);
        this.chatBot.setUsername("Botterfly#"+Long.toHexString(gameId));
        this.chatBot.setPassword(UUID.randomUUID().toString());
        this.chatBot.setToken(UUID.randomUUID().toString());
        gameChat.setAssociatedGameId(this.gameId);
        gameChat.addAdmin(chatBot);
        gameChat.setConfidential(true);
        // init game master
        addPlayer(gameMaster, PlayerState.GAME_MASTER);
        // set next game state
        gameState = GameState.LOBBY;
        this.lastModified = System.currentTimeMillis();
        return this;
    }

    /**
     * leaves the lobby phase and initializes the game rounds.
     * does nothing and returns false if :
     * 1) the game is not in the lobby state
     * 2) less than 3 players are present
     * 3) not forced and not all players are ready
     * 4) not enough memes have been found
     * @param force set true if the lobby should be closed regardless of whether players are ready or not.
     * @return whether the lobby was closed successfully
     */
    public synchronized boolean closeLobby(boolean force) {
        if (gameState != GameState.LOBBY) return false;
        if (!force && getPresentPlayers().size() < 3) return false;
        if (!force && (getReadyPlayers().size() < getPresentPlayers().size())) return false;
        if (getMemesFound().size() < getTotalRounds()) return false;

        gameState = GameState.STARTING;

        MemeUrlSupplier memeFactory = MemeUrlSupplier.create(getSubreddit(), getMemeType().toString().toLowerCase());
        for (int i = 0; i < gameSettings.getTotalRounds(); i++) {
            GameRound round = new GameRound();
            round.setTitle(String.format("Round %d",(i+1)));
            round.setMemeURL(memeFactory.get());
            this.gameRounds.add(round);
        }
        // initialize scores
        for (Long player : getEnrolledPlayers()) {
            scores.put(player, 0);
        }
        // 3 seconds until game starts
        setCountdown(3000L);
        this.lastModified = System.currentTimeMillis();
        return true;
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
        for (Long player : getEnrolledPlayers()) {
            playerStates.put(player, getPlayerState(player).activeState());
        }
        // set round counter
        this.roundCounter = 0;
        // go to running state
        this.gameState = GameState.RUNNING;
        // this will cause the game to advance on next update
        advanceTargetTime = 0L;
    }

    /**
     * sets the game on pause
     * @throws IllegalStateException if the game is not in an active state
     */
    public synchronized void pause() {
        if (!gameState.isActive())
            throw new IllegalStateException("cannot pause game in phase "+ gameState.toString());

        // need to remember the remaining countdown
        // time measure will be lost while paused
        remainingCountdown = getCurrentCountdown();
        this.gameState = GameState.PAUSED;
        this.lastModified = System.currentTimeMillis();
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
        setCountdown(remainingCountdown);
    }

    /**
     * terminates the game instantly and removes all the players.
     */
    public synchronized void kill() {
        if (!gameState.isOver()) gameState = GameState.ABORTED;
        for (Long player : getPresentPlayers()) playerStates.put(player, PlayerState.LEFT);
        gameChat.removeParticipant(chatBot);
        gameChat.close();
        this.lastModified = System.currentTimeMillis();
    }

    /**
     * if the game is dead, returns a summary of it
     * if the game is running, updates the countdown and advances if the latter ran out.
     */
    public synchronized GameUpdateResponse update() {
        // read chat, execute commands
        for (Message message : chatBot.getInbox()) {
            if (message.getText().startsWith("/")) {
                try {
                    interpretCommand(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        chatBot.getInbox().clear();

        // advance depending on game state
        switch(gameState) {
            // TODO distinguish whether modified or not
            case LOBBY:     closeLobby(false);
                            return GameUpdateResponse.MODIFIED;

            case STARTING:  // update countdown. If it ran out, start.
                            if (getCurrentCountdown() < 0) start();
                            return GameUpdateResponse.MODIFIED;

            case RUNNING:   // update countdown. If it ran out, advance.
                            if (getCurrentCountdown() < 0) advance();
                            return GameUpdateResponse.MODIFIED;

            case AFTERMATH: return GameUpdateResponse.COMPLETE;

            case ABORTED:
            case ABANDONED:
            case FINISHED:  return GameUpdateResponse.DEAD;

            default:        return GameUpdateResponse.MODIFIED;
        }
    }

    /**
     * sets the countdown to a given value and resets time measure.
     * @param millis milliseconds to set the countdown to
     */
    private synchronized void setCountdown(long millis) {
        advanceTargetTime = System.currentTimeMillis() +millis;
    }

    /**
     * Will advance the current round into the next phase
     * or issue the next round if the current one has ended.
     * Automatically sets the countdown.
     */
    private synchronized void advance() {
        assert (gameState == GameState.RUNNING);
        getCurrentRound().nextPhase();
        // set timer for next round phase or go to next round
        switch (getCurrentRound().getRoundPhase()) {
            case QUEUED ->      throw new AssertionError("current round should not be in phase QUEUED");
            case STARTING ->    setCountdown(3000L);    // 3 seconds for players to prepare for new round
            case SUGGEST ->     setCountdown(gameSettings.getMaxSuggestSeconds() * 1000L);
            case VOTE ->        setCountdown(gameSettings.getMaxVoteSeconds() * 1000L);
            case AFTERMATH ->   {
                                    distributePoints();
                                    setCountdown(gameSettings.getMaxAftermathSeconds() * 1000L);
                                }
            case CLOSED ->      skipRound();
        }
        this.lastModified = System.currentTimeMillis();
    }

    /**
     * TODO
     */
    private synchronized void distributePoints() {
        // initialize maps
        Map<Long, Integer> roundScores = getCurrentScores();    // score achieved in this round
        Map<Long, Integer> voteCounter = new HashMap<>();       // number of votes each player received
        for (Long player : getEnrolledPlayers()) {
            roundScores.put(player, 0);
            voteCounter.put(player, 0);
        }
        // count received votes for each player
        int maxVotes = 0;
        for (Long player : getEnrolledPlayers()) {
            if (getCurrentSuggestions().get(player) == null) {
                // no-suggestion penalty
                roundScores.put(player, roundScores.get(player) -3);
            }
            Long candidate = getCurrentVotes().get(player);
            if (candidate == null) {
                // no-vote penalty
                roundScores.put(player, roundScores.get(player) -3);
                continue;
            }
            // 5 points to candidate
            roundScores.put(candidate, roundScores.get(candidate)+5);
            // update voteCounter
            voteCounter.put(candidate, voteCounter.get(candidate) +1);
            // update maxVotes
            maxVotes = Math.max(maxVotes, voteCounter.get(candidate));
        }
        if (maxVotes == 0) return; // no votes, no scores (only penalties)

        int[] rankCounter = new int[maxVotes+1];    // rankCounter[n]: how many players received n votes
        int[] rankBonus = new int[maxVotes+1];      // rankBonus[n]: how many bonus points a player with n votes gets
        int currentBonus = 10;                      // current rank gets current bonus (decreasing)
        int currentRank = maxVotes;                 // current rank going from maxVotes to 0
        // init rankCounter
        for (Long player : getEnrolledPlayers()) {
            rankCounter[voteCounter.get(player)]++;
        }
        // distribute bonus points (decreasing) from top to bottom ranks
        while (currentBonus > 0 && currentRank > 0) {
            // for each player that landed in a rank, give bonus points to that rank
            for (int i = 0; i < rankCounter[currentRank]; i++) {
                rankBonus[currentRank] += currentBonus;
                currentBonus -= 2;
            }
            currentRank--;
        }
        // divide rank bonus to players in the same rank
        for (int i = 1; i <= maxVotes; i++) {
            if (rankCounter[i] > 0) rankBonus[i] /= rankCounter[i];
        }
        for (Long player : getEnrolledPlayers()) {
            // give bonus points
            roundScores.put(player, roundScores.get(player) + rankBonus[voteCounter.get(player)]);
            // update total scores
            scores.put(player, scores.get(player) + roundScores.get(player));
        }
        this.lastModified = System.currentTimeMillis();
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
            assert(getCurrentRound().getRoundPhase() == RoundPhase.QUEUED);
            // this will cause the game to advance on next update
            advanceTargetTime = 0L;
        }
        this.lastModified = System.currentTimeMillis();
    }

    /**
     * puts a suggestion of a player in the current round
     * @param player the player who suggests
     * @param suggestion suggested meme title
     * @throws SecurityException if the player is not enrolled
     * @throws IllegalStateException if either the game's state or the current round's state doesn't allow suggestions
     */
    public synchronized void putSuggestion(Long player, String suggestion) {
        if (!getPlayerState(player).isEnrolled())
            throw new SecurityException("player is not enrolled");
        if (!gameState.isActive())
            throw new IllegalStateException("can only suggest in an active state");

        getCurrentRound().putSuggestion(player, suggestion);
    }

    /**
     * puts a vote of a player in the current round
     * @param player the player who votes
     * @param vote userId of the player to vote for
     * @throws SecurityException if the player is not enrolled
     * @throws IllegalStateException if either the game's state or the current round's state doesn't allow suggestions
     */
    public synchronized void putVote(Long player, Long vote) {
        if (!getPlayerState(player).isEnrolled())
            throw new SecurityException("player is not enrolled");
        if (!gameState.isActive())
            throw new IllegalStateException("can only vote in an active state");

        getCurrentRound().putVote(player, vote);
    }

    private void interpretCommand(Message command) {
        String[] commandSegment = command.getText().split(" ");
        PlayerState commanderState = getPlayerState(command.getSender().getUserId());

        if (commanderState.isPromoted()) {
            // game master commands
            switch (commandSegment[0]) {
                case "/start" -> closeLobby(true);
                case "/a" -> advance();
                case "/skip" -> skipRound();
                case "/kill" -> kill();
                case "/pause" -> pause();
                case "/resume" -> resume();
                case "/ban" ->  {
                                    for (User player : command.getReferenced()) {
                                        try { banPlayer(player); } catch (Exception e) { e.printStackTrace(); }
                                    }
                                }
                case "/forgive" -> {for (User player : command.getReferenced()) forgivePlayer(player.getUserId());}
            }
        }
        if (commanderState.isPresent()) {
            // player commands
            switch (commandSegment[0]) {
                case "/r" -> setPlayerReady(command.getSender().getUserId(), !commanderState.isReady());
                case "/s" -> putSuggestion(command.getSender().getUserId(), command.getText().substring(3));
                case "/v" -> putVote(command.getSender().getUserId(), command.getReferenced().get(0).getUserId());
            }
        }
    }

    /**
     * creates a list of game round summaries up to the current round counter
     * @return list of game round summaries
     */
    public List<GameRoundSummary> summarizePastRounds() {
        List<GameRoundSummary> summaries = new ArrayList<>();
        for (int i = 0; i < this.roundCounter; i++) {
            GameRoundSummary summary = new GameRoundSummary();
            summary.adapt(gameRounds.get(i));
            summaries.add(summary);
        }
        return summaries;
    }

}
