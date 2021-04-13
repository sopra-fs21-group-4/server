package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class MessageGetDTO {

    private Long messageId;
    private Long chatId;
    private Long timestamp;
    private String senderName;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {this.senderName = senderName;}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
