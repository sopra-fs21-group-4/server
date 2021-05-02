package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private final List<Long> subscribedMessageChannels = new ArrayList<>();

    @ManyToMany(targetEntity = Message.class)
    private final List<Message> inbox = new ArrayList<>();

    @Column
    private Long currentGameId;

    @ManyToMany(targetEntity = User.class)
    private final List<User> friends = new ArrayList<>();

    @ManyToMany(targetEntity = User.class)
    private final List<User> outgoingFriendRequests = new ArrayList<>();

    @ManyToMany(targetEntity = User.class)
    private final List<User> incomingFriendRequests = new ArrayList<>();

    @Column
    private Long lastRequest = System.currentTimeMillis();


    public Long getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(Long gameId) {
        this.currentGameId = gameId;
        status = gameId == null? UserStatus.IDLE : UserStatus.PLAYING;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<Long> getSubscribedMessageChannels() {
        return subscribedMessageChannels;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public List<Message> getInbox() {
        return inbox;
    }

    public List<User> getFriends() {
        return friends;
    }

    public List<User> getOutgoingFriendRequests() {
        return outgoingFriendRequests;
    }

    public List<User> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }

    public Long getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Long lastRequest) {
        this.lastRequest = lastRequest;
    }



    public void notifyMessage(Message message) {
        inbox.add(message);
    }

    @Override
    public boolean equals(Object o) {
        return  o instanceof User && ((User) o).userId == this.userId;
    }
}
