package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.List;

public class GamePublicDTO {

    // TODO sort attributes, getters and setters

    private Long gameId;
    private String name;
    private GameState gameState;
    private Integer totalRounds;
    private String subreddit;
    private MemeType memeType;
    private Long maxSuggestSeconds;
    private Long maxVoteSeconds;
    private Long maxAftermathSeconds;
    private Integer maxPlayers;
    private Long gameMaster;
    private Integer playerCount;

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

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Integer getTotalRounds() {
        return totalRounds;
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

    public Long getGameMaster() {
        return gameMaster;
    }

    public void setGameMaster(Long gameMaster) {
        this.gameMaster = gameMaster;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }
  
    public void setPlayerCount(List<Long> players) {
        this.playerCount = players.size();
    }

}
