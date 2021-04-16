package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LobbyGetDTO {

    private Long lobbyId;
    private String name;
    private GameState gameState;
    private int round;
    private int maxRounds;
    private String currentMeme;
    private String subreddit;
    private MemeType memeType;
    private int maxTitleTime;
    private int maxVoteTime;
    private int maxPointsTime;
    private int maxPlayers;
    private long gameMaster; // special because we dont want to send the user object just the id
    private List<Long> players = new ArrayList<>(); // players and its setter is special because we dont want to return all user objects, just the ids of all users



    public String getCurrentMeme() {
        return currentMeme;
    }

    public void setCurrentMeme(String currentMeme) {
        this.currentMeme = currentMeme;
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

    public int getMaxTitleTime() {
        return maxTitleTime;
    }

    public void setMaxTitleTime(int maxTitleTime) {
        this.maxTitleTime = maxTitleTime;
    }

    public int getMaxVoteTime() {
        return maxVoteTime;
    }

    public void setMaxVoteTime(int maxVoteTime) {
        this.maxVoteTime = maxVoteTime;
    }

    public int getMaxPointsTime() {
        return maxPointsTime;
    }

    public void setMaxPointsTime(int maxPointsTime) {
        this.maxPointsTime = maxPointsTime;
    }

    public long getGameMaster() {
        return gameMaster;
    }

    public void setGameMaster(User gameMaster) {
        this.gameMaster = gameMaster.getUserId();
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

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
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

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

}
