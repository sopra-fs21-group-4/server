package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.Map;

public class GameRoundSummaryDTO {

    private String title;
    private String memeURL;
    private Map<Long, String> suggestions;
    private Map<Long, Long> votes;
    private Map<Long, Integer> scores;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemeURL() {
        return memeURL;
    }

    public void setMemeURL(String memeURL) {
        this.memeURL = memeURL;
    }

    public Map<Long, String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(Map<Long, String> suggestions) {
        this.suggestions = suggestions;
    }

    public Map<Long, Long> getVotes() {
        return votes;
    }

    public void setVotes(Map<Long, Long> votes) {
        this.votes = votes;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<Long, Integer> scores) {
        this.scores = scores;
    }

}
