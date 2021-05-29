package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.entity.GameRound;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.GameSummary;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameDTO implements EntityDTO {

    // basic
    private Long id;
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
    private Long gameSettingsId;

    // current round
    private Long currentRoundId;

    // summary
    private Long gameSummaryId;


    private EntityType type = EntityType.GAME;
    private Long lastModified;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setRoundCounter(Integer roundCounter) {
        this.roundCounter = roundCounter;
    }

    public void setAdvanceTargetTime(Long advanceTargetTime) {
        this.advanceTargetTime = advanceTargetTime;
    }

    public void setGameChatId(MessageChannel gameChat) {
        this.gameChatId = gameChat.getMessageChannelId();
    }

    public void setGameMaster(Long gameMaster) {
        this.gameMaster = gameMaster;
    }

    public void setPlayers(List<Long> players) {
        this.players = players;
    }

    public void setPlayerStates(Map<Long, PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public void setScores(Map<Long, Integer> scores) {
        this.scores = scores;
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettingsId = (gameSettings == null)? null : gameSettings.getGameSettingsId();
    }

    public void setCurrentRound(GameRound currentRound) {
        this.currentRoundId = (currentRound == null)? null : currentRound.getGameRoundId();
    }

    public void setGameSummaryId(Long gameSummaryId) {
        this.gameSummaryId = gameSummaryId;
    }

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
        if (playerStates != null) children.addAll(playerStates.keySet());
        if (gameSettingsId != null) children.add(gameSettingsId);
        if (currentRoundId != null) children.add(currentRoundId);
        return children;
    }

    @Override
    public void crop(Long receiverId, String cropHint) {
        PlayerState state = playerStates.get(receiverId);
        if (state == null || !state.isEnrolled()) {
            gameChatId = null;
            scores = null;
            currentRoundId = null;
        }
    }
}
