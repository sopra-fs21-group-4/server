package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class MessageChannelGetDTO {

    private Long messageChannelId;
    // TODO more fields!
    private Long lastModified;

    public Long getMessageChannelId() {
        return messageChannelId;
    }

    public void setMessageChannelId(Long messageChannelId) {
        this.messageChannelId = messageChannelId;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
