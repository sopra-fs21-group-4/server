package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.MemeType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * a collection of information about a game.
 * taken away from the original class for the sake of simplicity.
 * do not share between games.
 */
@Entity
@Table(name = "GAME_SETTINGS")
public class GameSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    /* FIELDS */

    @Id
    @GeneratedValue
    private Long gameSettingsId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password = "NO_PASSWORD";

    @Column(nullable = false)
    private Integer maxPlayers;

    @Column(nullable = false)
    private Integer totalRounds;

    @Column
    private String memeSourceURL;  // subreddit

    @Column(nullable = false)
    private MemeType memeType;

    @Column(nullable = false)
    private Integer maxSuggestSeconds;

    @Column (nullable = false)
    private Integer maxVoteSeconds;

    @Column(nullable = false)
    private Integer maxAftermathSeconds;

    /* GETTERS AND SETTERS */

    public Long getGameSettingsId() {
        return gameSettingsId;
    }

    public void setGameSettingsId(Long gameSettingsId) {
        this.gameSettingsId = gameSettingsId;
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password == null? "NO_PASSWORD" : password);
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }

    public String getMemeSourceURL() {
        return memeSourceURL;
    }

    public void setMemeSourceURL(String memeFetcherURL) {
        this.memeSourceURL = memeFetcherURL;
    }

    public MemeType getMemeType() {
        return memeType;
    }

    public void setMemeType(MemeType memeType) {
        this.memeType = memeType;
    }

    public Integer getMaxSuggestSeconds() {
        return maxSuggestSeconds;
    }

    public void setMaxSuggestSeconds(Integer maxNamingTime) {
        this.maxSuggestSeconds = maxNamingTime;
    }

    public Integer getMaxVoteSeconds() {
        return maxVoteSeconds;
    }

    public void setMaxVoteSeconds(Integer maxVotingTime) {
        this.maxVoteSeconds = maxVotingTime;
    }

    public Integer getMaxAftermathSeconds() {
        return maxAftermathSeconds;
    }

    public void setMaxAftermathSeconds(Integer maxResultsTime) {
        this.maxAftermathSeconds = maxResultsTime;
    }

    /**
     * tells whether a password is accepted.
     * use this method instead of a direct .equals()
     * @param password the password to check
     * @return true if the passwords match, or no password is required
     */
    public boolean acceptsPassword(String password) {
        // password can never be null.
        return this.password.equals("NO_PASSWORD") || this.password.equals(password);
    }

    /**
     * TODO necessary?
     * creates a copy of this object
     * @return a new GameSettings instance with the same values
     */
    public GameSettings clone() {
        GameSettings clone = new GameSettings();

        // clone.gameSettingsId = this.gameSettingsId;
        clone.password = this.password;
        clone.maxPlayers = this.maxPlayers;
        clone.totalRounds = this.totalRounds;
        clone.memeSourceURL = this.memeSourceURL;
        clone.memeType = this.memeType;
        clone.maxSuggestSeconds = this.maxSuggestSeconds;
        clone.maxVoteSeconds = this.maxVoteSeconds;
        clone.maxAftermathSeconds = this.maxAftermathSeconds;
        return clone;
    }

}
