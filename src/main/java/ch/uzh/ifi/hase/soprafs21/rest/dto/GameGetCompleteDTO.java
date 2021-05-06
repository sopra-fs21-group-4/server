package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.constant.RoundPhase;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameGetCompleteDTO {


    // basic
    private Long gameId;
    private GameState gameState;
    private Integer roundCounter;
    private Long currentCountdown;
    private Long gameChatId;

    // players
    private Long gameMaster;
    private List<Long> players;
    private Map<Long, PlayerState> playerStates;
    private Map<Long, Integer> scores;

    // settings
    private String name;
    private String subreddit;
    private MemeType memeType;
    private List<String> memesFound;
    private Integer totalRounds;
    private Integer maxPlayers;
    private Long maxSuggestSeconds;
    private Long maxVoteSeconds;
    private Long maxAftermathSeconds;

    // current round
    private String currentRoundTitle;
    private String currentMemeURL;
    private RoundPhase currentRoundPhase;
    private Map<Long, String> currentSuggestions;
    private Map<Long, Long> currentVotes;
    private Map<Long, Integer> currentScores;

    /* getters and setters */

    // basic
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getGameChatId() {
        return gameChatId;
    }
    // convert entity to id
    public void setGameChat(MessageChannel gameChat) {
        this.gameChatId = gameChat.getMessageChannelId();
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

    public List<String> getMemesFound() {
        return memesFound;
    }

    public void setMemesFound(List<String> memesFound) {
        this.memesFound = memesFound;
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

    public void setCurrentSuggestions(Map<Long, String> currentSuggestions) {
        this.currentSuggestions = currentSuggestions;
    }

    public Map<Long, Long> getCurrentVotes() {
        return currentVotes;
    }

    public void setCurrentVotes(Map<Long, Long> currentVotes) {
        this.currentVotes = currentVotes;
    }

    public Map<Long, Integer> getCurrentScores() {
        return currentScores;
    }

    public void setCurrentScores(Map<Long, Integer> currentScores) {
        this.currentScores = currentScores;
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

    public void setPlayerStates(Map<Long, PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<Long, Integer> scores) {
        this.scores = scores;
    }
  
    public Long getGameMaster() {
        return gameMaster;
    }

    public void setGameMaster(Long gameMaster) {
        this.gameMaster = gameMaster;
    }

    public List<Long> getPlayers() {
        return players;
    }

    public void setPlayers(List<Long> players) {
        this.players = players;
    }

}
