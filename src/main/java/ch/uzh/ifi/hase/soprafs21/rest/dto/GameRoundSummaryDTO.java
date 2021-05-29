package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameRoundSummaryDTO implements EntityDTO {

    private Long id;
    private String title;
    private String memeURL;
    private Map<Long, String> suggestions;
    private Map<Long, Long> votes;
    private Map<Long, Integer> scores;

    private EntityType type = EntityType.GAME_ROUND_SUMMARY;
    private Long lastModified;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMemeURL(String memeURL) {
        this.memeURL = memeURL;
    }

    public void setSuggestions(Map<Long, String> suggestions) {
        this.suggestions = suggestions;
    }

    public void setVotes(Map<Long, Long> votes) {
        this.votes = votes;
    }

    public void setScores(Map<Long, Integer> scores) {
        this.scores = scores;
    }

    @Override
    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public Set<Long> getChildren() {
        return scores == null? new HashSet<>() : scores.keySet();
    }

    @Override
    public void crop(Long receiverId, String cropHint) {
        if (!scores.containsKey(receiverId)) {
            memeURL = null;
            scores = null;
            votes = null;
            suggestions = null;
        }
    }


}
