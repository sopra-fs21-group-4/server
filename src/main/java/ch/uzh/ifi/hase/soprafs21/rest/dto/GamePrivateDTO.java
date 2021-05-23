package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.entity.GameRound;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.ObservableEntity;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;

import java.util.List;
import java.util.Map;

public class GamePrivateDTO implements ObservableEntity {


    // basic
    private Long gameId;
    private GameState gameState;
    private Integer roundCounter;
    private Long advanceTargetTime;
    private Long gameChatId;

    // players
    private Long gameMaster;
    private List<Long> players;
    private Map<Long, PlayerState> playerStates;
    private Map<Long, Integer> scores;

    // settings
    private GameSettingsGetDTO gameSettings;

    // current round
    private GameRoundDTO currentRound;

    //compare different TimeEvents
    private Long lastModified;



    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Integer getRoundCounter() {
        return roundCounter;
    }

    public void setRoundCounter(Integer roundCounter) {
        this.roundCounter = roundCounter;
    }

    public Long getAdvanceTargetTime() {
        return advanceTargetTime;
    }

    public void setAdvanceTargetTime(Long advanceTargetTime) {
        this.advanceTargetTime = advanceTargetTime;
    }

    public Long getGameChatId() {
        return gameChatId;
    }

    public void setGameChatId(MessageChannel gameChat) {
        this.gameChatId = gameChat.getMessageChannelId();
    }

    public Long getGameMaster() {
        return gameMaster;
    }

    public void setGameMaster(Long gameMaster) {
        this.gameMaster = gameMaster;
    }

    public List<Long> getPlayers() {
        return players;
    }

    public void setPlayers(List<Long> players) {
        this.players = players;
    }

    public Map<Long, PlayerState> getPlayerStates() {
        return playerStates;
    }

    public void setPlayerStates(Map<Long, PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<Long, Integer> scores) {
        this.scores = scores;
    }

    public GameSettingsGetDTO getGameSettings() {
        return gameSettings;
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = DTOMapper.INSTANCE.convertEntityToGameSettingsGetDTO(gameSettings);
    }

    public GameRoundDTO getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(GameRound currentRound) {
        if (currentRound == null) {
            this.currentRound = null;
            return;
        }
        this.currentRound = DTOMapper.INSTANCE.convertEntityToGameRoundDTO(currentRound);
    }

    // TODO duplicate delete
    public Long keepModified(Long lastUpdated){
        this.lastModified = Math.max(this.lastModified, gameSettings.getLastModified());
        if(gameSettings.getLastModified() < lastUpdated) this.gameSettings = null;
        if (this.currentRound != null) {
            this.lastModified = Math.max(this.lastModified, currentRound.getLastModified());
            if (currentRound.getLastModified() < lastUpdated) this.currentRound = null;
        }
        return this.lastModified;
    }

    @Override
    public long getId() {
        return gameId;
    }

    public long getLastModified() {
        return lastModified;
    }

    @Override
    public long filter(long lastUpdate) {
        this.lastModified = Math.max(this.lastModified, gameSettings.getLastModified());
        if(gameSettings.getLastModified() < lastUpdate) this.gameSettings = null;
        if (this.currentRound != null) {
            this.lastModified = Math.max(this.lastModified, currentRound.getLastModified());
            if (currentRound.getLastModified() < lastUpdate) this.currentRound = null;
        }
        return this.lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
