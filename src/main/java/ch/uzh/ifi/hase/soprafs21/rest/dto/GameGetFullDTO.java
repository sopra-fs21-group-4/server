package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.constant.RoundPhase;
import ch.uzh.ifi.hase.soprafs21.entity.GameRound;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameGetFullDTO {

    // TODO sort attributes, getters and setters

    private Long gameId;
    private String name;
    private String subreddit;
    private MemeType memeType;
    private GameState gameState;
    private String currentRoundTitle;
    private RoundPhase currentRoundPhase;
    private Map<Long, String> currentSuggestions;
    private Map<Long, Long> currentVotes;
    private String currentMemeURL;
    private Integer roundCounter;
    private Integer totalRounds;
    private Long currentCountdown;
    private Long maxSuggestSeconds;
    private Long maxVoteSeconds;
    private Long maxAftermathSeconds;
    private Integer maxPlayers;
    private Map<Long, PlayerState> playerStates;
    private Map<Long, Integer> playerPoints;
    private String gameMasterName;
    private List<String> playerNames;
    private Long gameChatId;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public MemeType getMemeType() {
        return memeType;
    }

    public void setMemeType(MemeType memeType) {
        this.memeType = memeType;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String getCurrentRoundTitle() {
        return currentRoundTitle;
    }

    public void setCurrentRoundTitle(String currentRoundTitle) {
        this.currentRoundTitle = currentRoundTitle;
    }

    public RoundPhase getCurrentRoundPhase() {
        return currentRoundPhase;
    }

    public void setCurrentRoundPhase(RoundPhase currentRoundPhase) {
        this.currentRoundPhase = currentRoundPhase;
    }

    public Map<Long, String> getCurrentSuggestions() {
        return currentSuggestions;
    }

    public void setCurrentSuggestions(Map<User, String> currentSuggestions) {
        this.currentSuggestions = new HashMap<>();
        for (User user : currentSuggestions.keySet()) {
            this.currentSuggestions.put(user.getUserId(), currentSuggestions.get(user));
        }
    }

    public Map<Long, Long> getCurrentVotes() {
        return currentVotes;
    }

    public void setCurrentVotes(Map<User, Long> currentVotes) {
        this.currentVotes = new HashMap<>();
        for (User user : currentVotes.keySet()) {
            this.currentVotes.put(user.getUserId(), currentVotes.get(user));
        }
    }

    public String getCurrentMemeURL() {
        return currentMemeURL;
    }

    public void setCurrentMemeURL(String currentMemeURL) {
        this.currentMemeURL = currentMemeURL;
    }

    public Integer getRoundCounter() {
        return roundCounter;
    }

    public void setRoundCounter(Integer roundCounter) {
        this.roundCounter = roundCounter;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public Long getCurrentCountdown() {
        return currentCountdown;
    }

    public void setCurrentCountdown(Long currentCountdown) {
        this.currentCountdown = currentCountdown;
    }

    public Long getMaxSuggestSeconds() {
        return maxSuggestSeconds;
    }

    public void setMaxSuggestSeconds(Long maxSuggestSeconds) {
        this.maxSuggestSeconds = maxSuggestSeconds;
    }

    public Long getMaxVoteSeconds() {
        return maxVoteSeconds;
    }

    public void setMaxVoteSeconds(Long maxVoteSeconds) {
        this.maxVoteSeconds = maxVoteSeconds;
    }

    public Long getMaxAftermathSeconds() {
        return maxAftermathSeconds;
    }

    public void setMaxAftermathSeconds(Long maxAftermathSeconds) {
        this.maxAftermathSeconds = maxAftermathSeconds;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Map<Long, PlayerState> getPlayerStates() {
        return playerStates;
    }
    // entities to userIds
    public void setPlayerStates(Map<User, PlayerState> playerStates) {
        this.playerStates = new HashMap<>();
        for (User user : playerStates.keySet()) {
            this.playerStates.put(user.getUserId(), playerStates.get(user));
        }
    }

    public Map<Long, Integer> getPlayerPoints() {
        return playerPoints;
    }
    // entities to userIds
    public void setPlayerPoints(Map<User, Integer> playerPoints) {
        this.playerPoints = new HashMap<>();
        for (User user : playerPoints.keySet()) {
            this.playerPoints.put(user.getUserId(), playerPoints.get(user));
        }
    }
  
    public String getGameMasterName() {
        return gameMasterName;
    }

    public void setGameMasterName(User gameMaster) {
        this.gameMasterName = gameMaster.getUsername();
    }// entity to username

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<User> players) {
        this.playerNames = new ArrayList<>();
        for (User user : players) this.playerNames.add(user.getUsername());
    }   // entities to usernames

    public Long getGameChatId() {
        return gameChatId;
    }
    // convert entity to id
    public void setGameChat(MessageChannel gameChat) {
        this.gameChatId = gameChat.getMessageChannelId();
    }

}
