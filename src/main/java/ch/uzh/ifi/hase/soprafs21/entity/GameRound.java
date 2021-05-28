package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.RoundPhase;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * // TODO comment
 * Internal Game Round representation
 * This class composes the internal representation of Rounds and
 * defines how they are stored in the database.
 */

@Entity
@Table(name="GAME_ROUND")
public class GameRound implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long gameRoundId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String memeURL;

    @Column(nullable = false)
    private RoundPhase roundPhase = RoundPhase.QUEUED;

    @ElementCollection
    private Map<Long, String> suggestions = new HashMap<>();

    @ElementCollection
    private Map<Long, Long> votes = new HashMap<>();

    @ElementCollection
    private Map<Long, Integer> scores = new HashMap<>();

    @Column
    private Long lastModified;


    public Long getGameRoundId() {
        return gameRoundId;
    }

    public void setGameRoundId(Long gameRoundId) {
        this.gameRoundId = gameRoundId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.lastModified = System.currentTimeMillis();
    }

    public String getMemeURL() {
        return memeURL;
    }

    public void setMemeURL(String memeURL) {
        this.memeURL = memeURL;
        this.lastModified = System.currentTimeMillis();
    }

    public RoundPhase getRoundPhase() {
        return roundPhase;
    }

    public void nextPhase() {
        this.roundPhase = roundPhase.nextPhase();
        this.lastModified = System.currentTimeMillis();
    }

    public void close() {
        this.roundPhase = RoundPhase.CLOSED;
        this.lastModified = System.currentTimeMillis();
    }

    public void putSuggestion(Long user, String suggestion) {
        if (!this.getRoundPhase().allowsSuggestions())
            throw new IllegalStateException("current round phase doesn't allow suggestions");
        if (user == null)
            throw new NullPointerException("\"null\" can't suggest");
        this.suggestions.put(user, suggestion);
        this.lastModified = System.currentTimeMillis();
    }

    public Map<Long, String> getSuggestions() {
        return suggestions;
    }

    public void putVote(Long user, Long targetUserId) {
        if (!this.getRoundPhase().allowsVotes())
            throw new IllegalStateException("current round phase doesn't allow votes");
        if (user == null)
            throw new NullPointerException("\"null\" can't vote");
        if (user.equals(targetUserId))
            throw new IllegalArgumentException("can't vote for yourself");
        lastModified = System.currentTimeMillis();
        this.votes.put(user, targetUserId);
    }

    public Map<Long, Long> getVotes() {
        return new HashMap<>(votes);
    } // returns only a copy

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public Long getLastModified() {
        return lastModified;
    }

}
