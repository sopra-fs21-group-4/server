package ch.uzh.ifi.hase.soprafs21.entity;

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
    private Long gameRoundId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String memeURL;

    @ElementCollection
    private Map<User, String> suggestions = new HashMap<>();

    @ElementCollection
    private Map<User, Long> votes = new HashMap<>();

    public Long getGameRoundId() {
        return gameRoundId;
    }

    public String getTitle() {
        return title;
    }

    public String getMemeURL() {
        return memeURL;
    }

    public Map<User, String> getSuggestions() {
        return new HashMap<>(suggestions);
    }

    public Map<User, Long> getVotes() {
        return new HashMap<>(votes);
    }

    public void adapt(GameRound gameRound) {
        if (this.gameRoundId != null) throw new IllegalStateException("GameRoundSummaries are immutable!");

        this.gameRoundId = gameRound.getRoundId();
        this.title = gameRound.getTitle();
        this.memeURL = gameRound.getMemeURL();
        this.suggestions = gameRound.getSuggestions();
        this.votes = gameRound.getVotes();
    }

}
