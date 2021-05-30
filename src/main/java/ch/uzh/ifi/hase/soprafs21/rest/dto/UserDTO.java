package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;

import java.util.*;

public class UserDTO implements EntityDTO {

    private Long id;
    private String username;
    private UserStatus status;

    private Long currentGameId;
    private List<Long> gameHistory;
    private Set<Long> friends;
    private Set<Long> outgoingFriendRequests;
    private Set<Long> incomingFriendRequests;

    private EntityType type = EntityType.USER;
    private Long lastModified;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Long getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(Long currentGameId) {
        this.currentGameId = currentGameId;
    }

    public List<Long> getGameHistory() {
        return gameHistory;
    }

    public void setGameHistory(List<Long> gameHistory) {
        this.gameHistory = gameHistory;
    }

    public Set<Long> getFriends() {
        return friends;
    }

    public void setFriends(Set<Long> friends) {
        this.friends = friends;
    }

    public Set<Long> getOutgoingFriendRequests() {
        return outgoingFriendRequests;
    }

    public void setOutgoingFriendRequests(Set<Long> outgoingFriendRequests) {
        this.outgoingFriendRequests = outgoingFriendRequests;
    }

    public Set<Long> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }

    public void setIncomingFriendRequests(Set<Long> incomingFriendRequests) {
        this.incomingFriendRequests = incomingFriendRequests;
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
        children.add(currentGameId);
        children.addAll(friends);
        if (outgoingFriendRequests != null) children.addAll(outgoingFriendRequests);
        if (incomingFriendRequests != null) children.addAll(incomingFriendRequests);
        return children;
    }

    @Override
    public void crop(Long receiverId, String cropHint) {
        if (!id.equals(receiverId)) {
            outgoingFriendRequests = null;
            incomingFriendRequests = null;
        }
    }
}
