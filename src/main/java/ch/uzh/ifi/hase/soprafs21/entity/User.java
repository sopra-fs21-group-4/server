package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private UserStatus status;

    @ManyToMany(targetEntity = Message.class)
    private final List<Message> inbox = new ArrayList<>();

    @Column
    private Long currentGameId;

    @ElementCollection
    private final Set<Long> friends = new HashSet<>();

    @ElementCollection
    private final Set<Long> outgoingFriendRequests = new HashSet<>();

    @ElementCollection
    private final Set<Long> incomingFriendRequests = new HashSet<>();

    @ElementCollection
    private final Map<Long, EntityType> observedEntities = new HashMap<>();

    @Column
    private Long lastModified;


    public Long getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(Long gameId) {
        this.currentGameId = gameId;
        status = gameId == null? UserStatus.IDLE : UserStatus.PLAYING;
        this.lastModified = System.currentTimeMillis();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        this.lastModified = System.currentTimeMillis();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.lastModified = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.lastModified = System.currentTimeMillis();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        this.lastModified = System.currentTimeMillis();
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        this.lastModified = System.currentTimeMillis();
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {
        this.email = email;
        this.lastModified = System.currentTimeMillis();
    }

    public List<Message> getInbox() {
        return inbox;
    }

    public Set<Long> getFriends() {
        return friends;
    } //TODO when datastructure is returned update lastModified

    public Set<Long> getOutgoingFriendRequests() {
        return outgoingFriendRequests;
    } //TODO when datastructure is returned update lastModified

    public Set<Long> getIncomingFriendRequests() {
        return incomingFriendRequests;
    } //TODO when datastructure is returned update lastModified

    public void notifyMessage(Message message) {
        inbox.add(message);
        this.lastModified = System.currentTimeMillis();
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void addFriend(Long userId){
        friends.add(userId);
    }

    public void removeFriend(Long userId){
        friends.remove(userId);
    }

    public void addOutgoingFriendRequest(long userId){
        outgoingFriendRequests.add(userId);
    }

    public void addIncomingFriendRequest(long userId){
        incomingFriendRequests.add(userId);
    }

    public void removeOutgoingFriendRequest(long userId){
        outgoingFriendRequests.remove(userId);
    }

    public void removeIncomingFriendRequest(long userId){
        incomingFriendRequests.remove(userId);
    }

    public Map<Long, EntityType> getObservedEntities() {
        return observedEntities;
    }

    public void observeEntity(long entityId, EntityType entityType) {
        this.observedEntities.put(entityId, entityType);
        this.lastModified = System.currentTimeMillis();
    }

    public void disregardEntity(long entityId) {
        this.observedEntities.remove(entityId);
//        this.lastModified = System.currentTimeMillis(); // no need to push this to the client
    }

    @Override
    public boolean equals(Object o) {
        return  o instanceof User && ((User) o).userId == this.userId;
    }


}
