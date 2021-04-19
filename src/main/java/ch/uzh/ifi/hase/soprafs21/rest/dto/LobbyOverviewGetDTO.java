package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LobbyOverviewGetDTO {

    private Long lobbyId;
    private String name;
    private GameState gameState;
    private Integer totalRounds;
    private String subreddit;
    private MemeType memeType;
    private Long namingTime;
    private Long votingTime;
    private Long resultsTime;
    private Integer maxPlayers;
    private String gameMaster; // special because we dont want to send the user object just the username
    private List<Long> players = new ArrayList<>(); // players and its setter is special because we dont want to return all user objects, just the ids of all users


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

    public Long getNamingTime() {
        return namingTime;
    }

    public void setNamingTime(Long namingTime) {
        this.namingTime = namingTime;
    }

    public Long getVotingTime() {
        return votingTime;
    }

    public void setVotingTime(Long votingTime) {
        this.votingTime = votingTime;
    }

    public Long getResultsTime() {
        return resultsTime;
    }

    public void setResultsTime(Long resultsTime) {
        this.resultsTime = resultsTime;
    }

    public String getGameMaster() {
        return gameMaster;
    }

    public void setGameMaster(User gameMaster) {
        this.gameMaster = gameMaster.getUsername();
    }

    public List<Long> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        // extracting ids
        for (User player:players) {
            this.players.add(player.getUserId());
        }
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
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

}
