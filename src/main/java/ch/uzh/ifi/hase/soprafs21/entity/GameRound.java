package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
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
    @GeneratedValue
    @Column
    private Long roundId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String memeURL;

    @Column(nullable = false)
    private RoundPhase phase = RoundPhase.QUEUED;

    @ElementCollection
    private Map<User, String> suggestions = new HashMap<>();

    @ElementCollection
    private Map<User, Long> votes = new HashMap<>();


    public Long getRoundId() {
        return roundId;
    }

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

    public RoundPhase getPhase() {
        return phase;
    }

    public void nextPhase() {
        this.phase = phase.nextPhase();
    }

    public void close() {
        this.phase = RoundPhase.CLOSED;
    }

    public void putSuggestion(User user, String suggestion) {
        if (!this.getPhase().allowsSuggestions())
            throw new IllegalStateException();
        this.suggestions.put(user, suggestion);
    }

    public Map<User, String> getSuggestions() {
        return new HashMap<>(suggestions);
    } // returns only a copy

    public void putVote(User user, Long targetUserId) {
        if (!this.getPhase().allowsVotes())
            throw new IllegalStateException();
        this.votes.put(user, targetUserId);
    }

    public Map<User, Long> getVotes() {
        return new HashMap<>(votes);
    } // returns only a copy


}
