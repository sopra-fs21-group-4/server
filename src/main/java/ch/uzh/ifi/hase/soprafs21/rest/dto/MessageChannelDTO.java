package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.entity.Message;

import java.util.*;

public class MessageChannelDTO implements EntityDTO {

    private Long id;
//    private boolean confidential; // unused feature
//    private boolean closed; // unused feature
    private Map<Long, String> roles;
    private List<Long> messages;
    private Long associatedGameId;

    private EntityType type = EntityType.MESSAGE_CHANNEL;
    private Long lastModified;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public boolean isConfidential() {
//        return confidential;
//    } // unused feature

//    public void setConfidential(boolean confidential) {
//        this.confidential = confidential;
//    } // unused feature

//    public boolean isClosed() {
//        return closed;
//    } // unused feature

//    public void setClosed(boolean closed) {
//        this.closed = closed;
//    } // unused feature

    public Map<Long, String> getRoles() {
        return roles;
    }

    public void setRoles(Map<Long, String> roles) {
        this.roles = roles;
    }

    public List<Long> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = new ArrayList<>();
        for (Message message : messages) this.messages.add(message.getMessageId());
    }

    public Long getAssociatedGameId() {
        return associatedGameId;
    }

    public void setAssociatedGameId(Long associatedGameId) {
        this.associatedGameId = associatedGameId;
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
        if (messages != null) children.addAll(messages);
        children.addAll(roles.keySet());
        children.add(associatedGameId);
        return children;
    }

    @Override
    public void crop(Long receiverId, String cropHint) {
        if (!roles.containsKey(receiverId) || !roles.get(receiverId).contains("@all")) {
            messages = null;
        }
    }
}
