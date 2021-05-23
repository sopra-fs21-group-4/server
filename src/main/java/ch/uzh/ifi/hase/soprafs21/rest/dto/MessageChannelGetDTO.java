package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.entity.ObservableEntity;

public class MessageChannelGetDTO implements ObservableEntity {

    private Long messageChannelId;
    // TODO more fields!
    private Long lastModified;

    public Long getMessageChannelId() {
        return messageChannelId;
    }

    public void setMessageChannelId(Long messageChannelId) {
        this.messageChannelId = messageChannelId;
    }

    @Override
    public long getId() {
        return messageChannelId;
    }

    public long getLastModified() {
        return lastModified;
    }

    @Override
    public long filter(long lastUpdate) {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
