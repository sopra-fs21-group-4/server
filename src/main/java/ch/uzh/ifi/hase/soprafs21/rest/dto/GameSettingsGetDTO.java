package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.MemeType;

import java.util.List;

public class GameSettingsGetDTO {

    private String name;
    private Integer maxPlayers;
    private Integer totalRounds;
    private String subreddit;
    private MemeType memeType;
    private Integer memesFound;
    private Integer maxSuggestSeconds;
    private Integer maxVoteSeconds;
    private Integer maxAftermathSeconds;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getMemesFound() {
        return memesFound;
    }

    public void setMemesFound(List<String> memesFound) {
        this.memesFound = memesFound.size();
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
