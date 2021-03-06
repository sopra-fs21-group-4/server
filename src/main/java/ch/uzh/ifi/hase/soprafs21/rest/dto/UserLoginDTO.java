package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.*;

public class UserLoginDTO {



    private long userId;
    private String username;
    private String token;
    private List<Long> gameHistory;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setGameHistory(List<Long> gameHistory){
        this.gameHistory = gameHistory;
    }

    public List<Long> getGameHistory() {
        return gameHistory;
    }
}
