package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.entity.Chat;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LobbyGetDTO {

    private Long lobbyId;
    private String name;
    private GameState gameState;
    private int round;
    private int totalRounds;
    private String currentMeme;
    private String subreddit;
    private MemeType memeType;
    private int namingTime;
    private int votingTime;
    private int resultsTime;
    private int maxPlayers;
    private long gameMaster; // special because we dont want to send the user object just the id
    private List<Long> players = new ArrayList<>(); // players and its setter is special because we dont want to return all user objects, just the ids of all users
    private Long chatId;

    public Long getChatId() {
        return chatId;
    }

    public void setChat(Chat chat) {
        this.chatId = chat.getChatId();
    }

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

    public int getNamingTime() {
        return namingTime;
    }

    public void setNamingTime(int namingTime) {
        this.namingTime = namingTime;
    }

    public int getVotingTime() {
        return votingTime;
    }

    public void setVotingTime(int votingTime) {
        this.votingTime = votingTime;
    }

    public int getResultsTime() {
        return resultsTime;
    }

    public void setResultsTime(int resultsTime) {
        this.resultsTime = resultsTime;
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

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
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
