package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.nonpersistent.MessageChannelListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Chat representation
 * This class composes the internal representation of a Chat and
 * defines how it is stored in the database.
 */

@Entity
@Table(name="MESSAGE_CHANNEL")
public class MessageChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long messageChannelId;

    @Column(nullable = false)
    private Boolean confidential = false;

    @Column(nullable = false)
    private Boolean closed = false;

    @ManyToMany(targetEntity = User.class)
    private final List<User> admins = new ArrayList();

    @ManyToMany(targetEntity = User.class)
    private final List<User> participants = new ArrayList();

    @Transient
    private transient List<MessageChannelListener> observerList;



    public Long getMessageChannelId() {
        return messageChannelId;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public Boolean getClosed() {
        return closed;
    }

    public List<User> getAdmins() {
        return new ArrayList<>(admins);
    }

    public List<User> getParticipants() {
        return new ArrayList<>(participants);
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public void close() {
        this.closed = true;
    }

    public boolean addAdmin(User user) {
        if (this.admins.contains(user)) return false;
        this.addParticipant(user);
        return this.admins.add(user);
    }

    public boolean removeAdmin(User user) {
        return this.admins.remove(user);
    }

    public boolean addParticipant(User user) {
        if (this.participants.contains(user)) return false;
        return this.participants.add(user);
    }

    public boolean removeParticipant(User user) {
        return this.participants.remove(user);
    }

    public boolean addMessageChannelListener(MessageChannelListener mcl) {
        if (observerList == null) observerList = new ArrayList<>(); // lazy init
        return observerList.add(mcl);
    }

    public boolean removeMessageChannelListener(MessageChannelListener mcl) {
        if (observerList == null) return false;
        return observerList.remove(mcl);
    }

    public void messagePosted(Message message) {
        if (observerList == null) return;
        for (MessageChannelListener mcl : observerList) mcl.messagePosted(message);
    }

    public void messageModified(Message message, String originalText) {
        if (observerList == null) return;
        for (MessageChannelListener mcl : observerList) mcl.messageModified(message, originalText);
    }

    public void messageDeleted(Message message) {
        if (observerList == null) return;
        for (MessageChannelListener mcl : observerList) mcl.messageDeleted(message);
    }

}
