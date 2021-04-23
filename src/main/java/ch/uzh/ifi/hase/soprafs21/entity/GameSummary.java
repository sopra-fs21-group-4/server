package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;

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

    @OneToOne(targetEntity = GameSettings.class)
    private GameSettings gameSettings;

    @Column(nullable = false)
    private GameState gameState;

    @Column(nullable = false)
    @OneToMany(targetEntity = GameRoundSummary.class, cascade = CascadeType.ALL)
    private List<GameRoundSummary> rounds;

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

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<GameRoundSummary> getRounds() {
        return new ArrayList<>(rounds);
    }

    public void adapt(Game game) {
        if (this.gameId != null) throw new IllegalStateException("GameSummaries are immutable!");

        this.gameId = game.getGameId();
        this.name = game.getName();
        this.points = game.getPlayerPoints();
        this.gameChat = game.getGameChat();
        this.gameSettings = game.getGameSettings();
        this.gameState = game.getGameState();
        this.rounds = game.summarizePastRounds();
    }

}
