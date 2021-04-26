package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.entity.MessageChannel;
import ch.uzh.ifi.hase.soprafs21.entity.User;

public class MessageGetDTO {

    private Long messageId;
    private Long messageChannelId;
    private Long timestamp;
    private String username;
    private String text;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getMessageChannelId() {
        return messageChannelId;
    }

    public void setMessageChannelId(MessageChannel messageChannel) {
        this.messageChannelId = messageChannel.getMessageChannelId();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(User user) {this.username = user.getUsername();}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
