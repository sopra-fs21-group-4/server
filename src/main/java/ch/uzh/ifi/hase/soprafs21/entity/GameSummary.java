package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.nonpersistent.Game;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * contains all the relevant data about a finished game.
 * Summaries are immutable.
 */
@Entity
@Table(name = "GAME_SUMMARY")
public class GameSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    /* FIELDS */

    @Id
    private Long gameId;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    private Map<User, Integer> points;

    @OneToOne(targetEntity = MessageChannel.class)
    private MessageChannel gameChat;

    @Column(nullable = false)
    private GameState gameState;

    @Column(nullable = false)
    @OneToMany(targetEntity = GameRoundSummary.class, cascade = CascadeType.ALL)
    private List<GameRoundSummary> rounds;

    @Column
    private Integer maxPlayers;

    @Column
    private Integer totalRounds;

    @Column
    private String memeSourceURL;  // subreddit

    @Column
    private MemeType memeType;

    @Column
    private Integer maxSuggestSeconds;

    @Column
    private Integer maxVoteSeconds;

    @Column
    private Integer maxAftermathSeconds;

    /* GETTERS AND SETTERS */

    public Long getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public Map<User, Integer> getPoints() {
        return points;
    }

    public MessageChannel getGameChat() {
        return gameChat;
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<GameRoundSummary> getRounds() {
        return new ArrayList<>(rounds);
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public String getMemeSourceURL() {
        return memeSourceURL;
    }

    public MemeType getMemeType() {
        return memeType;
    }

    public Integer getMaxSuggestSeconds() {
        return maxSuggestSeconds;
    }

    public Integer getMaxVoteSeconds() {
        return maxVoteSeconds;
    }

    public Integer getMaxAftermathSeconds() {
        return maxAftermathSeconds;
    }

    public void adapt(Game game) {
        if (this.gameId != null) throw new IllegalStateException("GameSummaries are immutable!");

        this.gameId = game.getGameId();
        this.name = game.getName();
        this.points = game.getPlayerPoints();
        this.gameChat = game.getGameChat();
        this.gameState = game.getGameState();
        this.rounds = game.summarizePastRounds();
        this.maxPlayers = game.getMaxPlayers();
        this.totalRounds = game.getTotalRounds();
        this.memeSourceURL = game.getMemeSourceURL();
        this.memeType = game.getMemeType();
        this.maxSuggestSeconds = game.getMaxSuggestSeconds();
        this.maxVoteSeconds = game.getMaxVoteSeconds();
        this.maxAftermathSeconds = game.getMaxAftermathSeconds();

    }

}
