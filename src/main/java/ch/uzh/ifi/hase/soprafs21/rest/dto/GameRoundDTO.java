package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameRoundDTO implements EntityDTO {

    private Long id;
    private String title;
    private String memeURL;
    private RoundPhase roundPhase;
    private Map<Long, String> suggestions;
    private Map<Long, Long> votes;
    private Map<Long, Integer> scores;

    private EntityType type = EntityType.GAME_ROUND;
    private Long LastModified;


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

    public void setRoundPhase(RoundPhase roundPhase) {
        this.roundPhase = roundPhase;
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
        return LastModified;
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

    public void setLastModified(Long lastModified) {
        LastModified = lastModified;
    }

}
