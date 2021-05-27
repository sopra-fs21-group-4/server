package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

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

//    @Column(nullable = false)
//    private Boolean confidential = false; // unused feature

//    @Column(nullable = false)
//    private Boolean closed = false; // unused feature

    @ElementCollection
    private final Map<Long, String> roles = new HashMap<>();

    @OneToMany(targetEntity = Message.class)
    private final List<Message> messages = new ArrayList<>();

    @Column
    private Long associatedGameId;

    @Column
    private Long lastModified;


    public Long getMessageChannelId() {
        return messageChannelId;
    }

//    public Boolean getConfidential() {
//        return confidential;
//    } // unused feature

//    public Boolean getClosed() {
//        return closed;
//    } // unused feature

    public List<Message> getMessages() {
        return messages;
    }

    public synchronized void addMessage(Message message) {
        messages.add(message);

        if (associatedGameId != null && message.getCommand()!= null) {
            Game game = SpringContext.getBean(GameRepository.class).findByGameId(associatedGameId);
            if (game != null) game.runCommand(message);
        }
//        Set<Long> referenced = getReferenced(message);
//        if (message.getCommand()!= null) referenced.addAll(getReferenced("@bot"));
//        UserRepository repo = SpringContext.getBean(UserRepository.class);
//        for (Long userId : referenced) {
//            User user = repo.findByUserId(userId);
//            if (user != null) user.addInbox(message);
//        }
        this.lastModified = System.currentTimeMillis();
    }

    public Long getAssociatedGameId() {
        return associatedGameId;
    }

    public void setAssociatedGameId(Long associatedGameId) {
        this.associatedGameId = associatedGameId;
        this.lastModified = System.currentTimeMillis();
    }

//    public void setConfidential(Boolean confidential) {
//        this.confidential = confidential;
//        this.lastModified = System.currentTimeMillis();
//    }

    public void setMessageChannelId(Long messageChannelId){
        this.messageChannelId = messageChannelId;
        this.lastModified = System.currentTimeMillis();
    }

//    public void close() {
//        this.closed = true;
//        this.lastModified = System.currentTimeMillis();
//    }

    public Map<Long, String> getRoles() {
        return roles;
    }

    public boolean hasRole(Long userId, String role) {
        String roles = this.roles.get(userId);
        return roles != null && roles.contains(role);
    }

    public Set<Long> getReferenced(Message message) {
        Set<Long> referenced = new HashSet<>();
        for (String ref : message.getReferences()) referenced.addAll(getReferenced(ref));
        return referenced;
    }

    public Set<Long> getReferenced(String role) {
        Set<Long> foundUserIds = new HashSet<>();
        if (role.length() < 2) return foundUserIds;
        if (role.charAt(1) == '#'){
            foundUserIds.add(Long.parseLong(role.substring(2)));
        } else {
            for (Long userId : this.roles.keySet()) if (hasRole(userId, role)) foundUserIds.add(userId);
        }
        return foundUserIds;
    }

    public void addRole(Long userId, String role) {
        String roles = this.roles.get(userId);
        if (roles == null) roles = "";
        if (roles.contains(role)) return;
        this.roles.put(userId, roles+role);
    }

    public void removeRole(Long userId, String role) {
        String roles = this.roles.get(userId);
        if (roles == null) return;
        this.roles.put(userId, roles.replaceFirst(role, ""));
    }

    public boolean verifyParticipant(Long userId) {
//        return !closed && (!confidential || hasRole(userId, "@all"));
        return hasRole(userId, "@all");
    }

//    public boolean verifyAdmin(Long userId) {
//        return !closed && hasRole(userId, "@admin");
//    } // unused feature

    public Long getLastModified() {
        return lastModified;
    }
}
