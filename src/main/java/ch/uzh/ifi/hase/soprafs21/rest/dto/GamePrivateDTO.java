package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.entity.GameRound;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;

import java.util.List;
import java.util.Map;

public class GamePrivateDTO {


    // basic
    private Long gameId;
    private GameState gameState;
    private Integer roundCounter;
    private Long currentCountdown;
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

    public Long getCurrentCountdown() {
        return currentCountdown;
    }

    public void setCurrentCountdown(Long currentCountdown) {
        this.currentCountdown = currentCountdown;
    }

    public Long getGameChatId() {
        return gameChatId;
    }

    public void setGameChatId(Long gameChatId) {
        this.gameChatId = gameChatId;
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
        this.currentRound = DTOMapper.INSTANCE.convertEntityToGameRoundDTO(currentRound);
    }
}
