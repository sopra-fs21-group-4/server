package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import util.MemeUrlSupplier;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @Column
    private String subreddit;

    @Column(nullable = false)
    private MemeType memeType;

    @Column(nullable = false)
    private Integer totalRounds;

    @ElementCollection
    private final List<String> memesFound = new ArrayList<>();

    @Column(nullable = false)
    private Integer maxSuggestSeconds;

    @Column (nullable = false)
    private Integer maxVoteSeconds;

    @Column(nullable = false)
    private Integer maxAftermathSeconds;

    @Column
    private Long lastModified;

    /* GETTERS AND SETTERS */

    public Long getGameSettingsId() {
        return gameSettingsId;
    }

    public void setGameSettingsId(Long gameSettingsId) {
        this.gameSettingsId = gameSettingsId;
        this.lastModified = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
        this.lastModified = System.currentTimeMillis();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password == null? "NO_PASSWORD" : password);
        this.lastModified = System.currentTimeMillis();
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.lastModified = System.currentTimeMillis();
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
        this.memesFound.clear();
        this.lastModified = System.currentTimeMillis();
    }

    public MemeType getMemeType() {
        return memeType;
    }

    public void setMemeType(MemeType memeType) {
        this.memeType = memeType;
        this.memesFound.clear();
        this.lastModified = System.currentTimeMillis();
    }

    public Integer getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
        this.lastModified = System.currentTimeMillis();
    }

    public Integer getMaxSuggestSeconds() {
        return maxSuggestSeconds;
    }

    public void setMaxSuggestSeconds(Integer maxNamingTime) {
        this.maxSuggestSeconds = maxNamingTime;
        this.lastModified = System.currentTimeMillis();
    }

    public Integer getMaxVoteSeconds() {
        return maxVoteSeconds;
    }

    public void setMaxVoteSeconds(Integer maxVotingTime) {
        this.maxVoteSeconds = maxVotingTime;
        this.lastModified = System.currentTimeMillis();
    }

    public Integer getMaxAftermathSeconds() {
        return maxAftermathSeconds;
    }

    public void setMaxAftermathSeconds(Integer maxResultsTime) {
        this.maxAftermathSeconds = maxResultsTime;
        this.lastModified = System.currentTimeMillis();
    }

    public List<String> getMemesFound() {
        if (memesFound.isEmpty() && subreddit != null && memeType != null) {
            memesFound.addAll(MemeUrlSupplier.create(getSubreddit(), getMemeType().toString().toLowerCase()).getMemeList());
            this.lastModified = System.currentTimeMillis();
        }
        return memesFound;
    }

    public Long getLastModified() {
        return lastModified;
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

}
