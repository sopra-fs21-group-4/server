package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameGetRestrictedDTO {

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
    private String gameMasterName;
    private List<String> playerNames;

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

}
