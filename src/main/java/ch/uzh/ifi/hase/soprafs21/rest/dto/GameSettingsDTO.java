package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameSettingsDTO implements EntityDTO {

    private Long id;
    private String name;
    private Integer maxPlayers;
    private Integer totalRounds;
    private String subreddit;
    private MemeType memeType;
    private Integer memesFound;
    private Integer maxSuggestSeconds;
    private Integer maxVoteSeconds;
    private Integer maxAftermathSeconds;

    private EntityType type = EntityType.GAME_SETTINGS;
    private Long lastModified;


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

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
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

    public void setMemesFound(List<String> memesFound) {
        this.memesFound = memesFound.size();
    }

    public void setMaxSuggestSeconds(Integer maxSuggestSeconds) {
        this.maxSuggestSeconds = maxSuggestSeconds;
    }

    public void setMaxVoteSeconds(Integer maxVoteSeconds) {
        this.maxVoteSeconds = maxVoteSeconds;
    }

    public void setMaxAftermathSeconds(Integer maxAftermathSeconds) {
        this.maxAftermathSeconds = maxAftermathSeconds;
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
        // no children
        return new HashSet<>();
    }

    @Override
    public void crop(Long receiverId, String cropHint) {
        // it's all public
    }
}
