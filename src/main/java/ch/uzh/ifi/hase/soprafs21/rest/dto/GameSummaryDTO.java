package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.entity.GameRoundSummary;

import java.util.*;

public class GameSummaryDTO implements EntityDTO {

    private Long id;
    private String name;
    private Long gameChatId;
    private Map<Long, Integer> scores;
    private GameState gameState;
    private List<Long> roundIds;
    private String subreddit;
    private MemeType memeType;

    private EntityType type = EntityType.GAME_SUMMARY;
    private Long lastModified;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<Long, Integer> scores) {
        this.scores = scores;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Long getGameChatId() {
        return gameChatId;
    }

    public void setGameChatId(Long gameChatId) {
        this.gameChatId = gameChatId;
    }

    public List<Long> getRoundIds() {
        return roundIds;
    }
    // convert entities to DTOs
    public void setRoundIds(List<Long> roundIds) {
        this.roundIds = roundIds;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public MemeType getMemeType() {
        return memeType;
    }

    public void setMemeType(MemeType memeType) {
        this.memeType = memeType;
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
        Set<Long> children = new HashSet<>();
        if (gameChatId != null) children.add(gameChatId);
        if (scores != null) children.addAll(scores.keySet());
        if (roundIds != null) children.addAll(roundIds);
        return children;
    }

    @Override
    public void crop(Long receiverId, String cropHint) {
        if (!scores.containsKey(receiverId)) {
            gameChatId = null;
            scores = null;
            roundIds = null;
        }
    }
}
