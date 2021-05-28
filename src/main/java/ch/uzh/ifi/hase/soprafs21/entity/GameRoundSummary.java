package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRoundSummaryRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal Game Round representation
 * This class composes the internal representation of Rounds and
 * defines how they are stored in the database.
 */

@Entity
@Table(name="GAME_ROUND_SUMMARY")
public class GameRoundSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long gameRoundSummaryId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String memeURL;

    @ElementCollection
    private Map<Long, String> suggestions;

    @ElementCollection
    private Map<Long, Long> votes;

    @ElementCollection
    private Map<Long, Integer> scores;

    @Column
    private Long lastModified;

    public Long getGameRoundSummaryId() {
        return gameRoundSummaryId;
    }

    public String getTitle() {
        return title;
    }

    public String getMemeURL() {
        return memeURL;
    }

    public Map<Long, String> getSuggestions() {
        return suggestions;
    }

    public Map<Long, Long> getVotes() {
        return votes;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void adapt(GameRound gameRound) {
        if (this.title != null) throw new IllegalStateException("GameRoundSummaries are immutable!");

        this.gameRoundSummaryId = gameRound.getGameRoundId()+1;
        this.title = gameRound.getTitle();
        this.memeURL = gameRound.getMemeURL();
        this.suggestions = new HashMap(gameRound.getSuggestions());
        this.votes = new HashMap<>(gameRound.getVotes());
        this.scores = new HashMap(gameRound.getScores());
        this.lastModified = System.currentTimeMillis();

        GameRoundSummaryRepository repo = SpringContext.getBean(GameRoundSummaryRepository.class);
        repo.save(this);
        repo.flush();
    }

}
