package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
    @GeneratedValue
    private Long gameSummaryId;

    @Column(nullable = false,unique = true)
    private Long gameId;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    private Map<Long, Integer> scores;

    @Column
    private Long gameChatId;

    @Column(nullable = false)
    private GameState gameState;

    @Column(nullable = false)
    @OneToMany(targetEntity = GameRoundSummary.class, cascade = CascadeType.ALL)
    private List<GameRoundSummary> rounds;

    @Column
    private String subreddit;  // subreddit

    @Column
    private MemeType memeType;

    /* GETTERS AND SETTERS */

    public Long getGameSummaryId() {
        return gameSummaryId;
    }

    public void setGameSummaryId(Long gameSummaryId) {
        this.gameSummaryId = gameSummaryId;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public Long getGameChatId() {
        return gameChatId;
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<GameRoundSummary> getRounds() {
        return new ArrayList<>(rounds);
    }

    public String getSubreddit() {
        return subreddit;
    }

    public MemeType getMemeType() {
        return memeType;
    }

    public GameSummary adapt(Game game) {
        if (this.gameId != null) throw new IllegalStateException("GameSummaries are immutable!");

        this.gameId = game.getGameId();
        this.name = game.getName();
        this.scores = new HashMap<>(game.getScores());
        this.gameChatId = game.getGameChat().getMessageChannelId();
        this.gameState = game.getGameState();
        this.rounds = game.summarizePastRounds();
        this.subreddit = game.getSubreddit();
        this.memeType = game.getMemeType();

        return this;
    }

}
