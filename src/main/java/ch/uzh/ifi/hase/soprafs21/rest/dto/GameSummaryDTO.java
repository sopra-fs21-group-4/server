package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.constant.RoundPhase;
import ch.uzh.ifi.hase.soprafs21.entity.GameRoundSummary;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.ObservableEntity;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameSummaryDTO implements ObservableEntity {

    private Long gameId;
    private String name;
    private Map<Long, Integer> scores;
    private Long gameChatId;
    private GameState gameState;
    private List<GameRoundSummaryDTO> rounds;
    private String subreddit;
    private MemeType memeType;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
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

    public List<GameRoundSummaryDTO> getRounds() {
        return rounds;
    }
    // convert entities to DTOs
    public void setRounds(List<GameRoundSummary> rounds) {
        this.rounds = new ArrayList<>();
        for (GameRoundSummary round : rounds) this.rounds.add(DTOMapper.INSTANCE.convertEntityToGameRoundSummaryDTO(round));
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
    public long getId() {
        return gameId;
    }

    @Override
    public long getLastModified() {
        return 0;   // TODO return termination time
    }

    @Override
    public long filter(long lastUpdate) {
        return 0;
    }
}
