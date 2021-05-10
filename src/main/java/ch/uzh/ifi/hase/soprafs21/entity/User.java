package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection
    private final Set<Long> subscribedMessageChannels = new HashSet<>();

    @ElementCollection
    private final Set<Long> subscribedGameSummaries = new HashSet<>();

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

    @Column
    private Long lastRequest = System.currentTimeMillis();

    @ElementCollection
    private final Set<Long> subscribedUsers = new HashSet<>();

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

    public Set<Long> getSubscribedGameSummaries() {
        return subscribedGameSummaries;
    }

    public Set<Long> getSubscribedUsers() {
        return subscribedUsers;
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

    public Set<Long> getSubscribedMessageChannels() {
        return subscribedMessageChannels;
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

    public Long getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Long lastRequest) {
        this.lastRequest = lastRequest;
        this.lastModified = System.currentTimeMillis();
    }



    public void notifyMessage(Message message) {
        inbox.add(message);
        this.lastModified = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        return  o instanceof User && ((User) o).userId == this.userId;
    }

    public Long getLastModified() {
        return lastModified;
    }
}
