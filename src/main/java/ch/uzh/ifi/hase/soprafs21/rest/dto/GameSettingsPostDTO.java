package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.MemeType;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import java.util.List;

public class GameSettingsPostDTO {

    private String name;
    private String password; // not in DTO
    private Integer maxPlayers;
    private Integer totalRounds;
    private String subreddit;
    private MemeType memeType;
    private Integer maxSuggestSeconds;
    private Integer maxVoteSeconds;
    private Integer maxAftermathSeconds;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
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

    public Integer getMaxSuggestSeconds() {
        return maxSuggestSeconds;
    }

    public void setMaxSuggestSeconds(Integer maxSuggestSeconds) {
        this.maxSuggestSeconds = maxSuggestSeconds;
    }

    public Integer getMaxVoteSeconds() {
        return maxVoteSeconds;
    }

    public void setMaxVoteSeconds(Integer maxVoteSeconds) {
        this.maxVoteSeconds = maxVoteSeconds;
    }

    public Integer getMaxAftermathSeconds() {
        return maxAftermathSeconds;
    }

    public void setMaxAftermathSeconds(Integer maxAftermathSeconds) {
        this.maxAftermathSeconds = maxAftermathSeconds;
    }


}
