package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.util.HashSet;
import java.util.Set;

public class MessageDTO implements EntityDTO {

    private Long id;
    private Long timestamp;
    private Long senderId;
    private String text;

    private EntityType type = EntityType.MESSAGE;
    private Long lastModified;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        Set<Long> children = new HashSet<>();
        children.add(senderId);
        return children;
    }

    @Override
    public void crop(Long receiverId, String cropHint) {
        // TODO message is unable to verify receiver
    }
}
