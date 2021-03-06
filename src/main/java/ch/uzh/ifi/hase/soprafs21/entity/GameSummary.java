package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameSummaryRepository;

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
    private Long gameSummaryId;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    private Map<Long, Integer> scores;

    @Column
    private Long gameChatId;

    @Column(nullable = false)
    private GameState gameState;

//    @OneToMany(targetEntity = GameRoundSummary.class, cascade = CascadeType.ALL)
    @ElementCollection
    private List<Long> roundIds;

    @Column
    private String subreddit;  // subreddit

    @Column
    private MemeType memeType;

    @Column
    private Long lastModified;

    /* GETTERS AND SETTERS */

    public Long getGameSummaryId() {
        return gameSummaryId;
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

    public List<Long> getRoundIds() {
        return new ArrayList<>(roundIds);
    }

    public String getSubreddit() {
        return subreddit;
    }

    public MemeType getMemeType() {
        return memeType;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public GameSummary adapt(Game game) {
        if (this.name != null) throw new IllegalStateException("GameSummaries are immutable!");

        List<GameRoundSummary> rounds = game.summarizePastRounds();

        this.gameSummaryId = game.getGameSummaryId();
        this.name = game.getName();
        this.scores = new HashMap<>(game.getScores());
        this.gameChatId = game.getGameChat().getMessageChannelId();
        this.gameState = game.getGameState();
        this.roundIds = new ArrayList<>();
        for (GameRoundSummary round : rounds) roundIds.add(round.getGameRoundSummaryId());
        this.subreddit = game.getSubreddit();
        this.memeType = game.getMemeType();
        this.lastModified = System.currentTimeMillis();

        GameSummaryRepository repo = SpringContext.getBean(GameSummaryRepository.class);
        repo.save(this);
        repo.flush();
        return this;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setGameSummaryId(Long gameSummaryId) {
        this.gameSummaryId = gameSummaryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScores(Map<Long, Integer> scores) {
        this.scores = scores;
    }

    public void setGameChatId(Long gameChatId) {
        this.gameChatId = gameChatId;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setRoundIds(List<Long> roundIds) {
        this.roundIds = roundIds;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public void setMemeType(MemeType memeType) {
        this.memeType = memeType;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
