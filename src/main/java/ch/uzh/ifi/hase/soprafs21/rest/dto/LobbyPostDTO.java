package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class LobbyPostDTO {

    private String name;
    private String subreddit;
    private String memeType;
    private String password;
    private Integer maxPlayers;
    private Integer totalRounds;
    private Long namingTime;
    private Long votingTime;
    private Long resultsTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getMemeType() {
        return memeType;
    }

    public void setMemeType(String memeType) {
        this.memeType = memeType;
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

}
