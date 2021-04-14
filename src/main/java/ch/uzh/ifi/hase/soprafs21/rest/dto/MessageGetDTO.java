package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class MessageGetDTO {

    private Long messageId;
    private Long chatId;
    private Long timestamp;
    private String username;
    private String text;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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

    public void setUsername(String username) {this.username = username;}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
