package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class MessageGetDTO {

    private Long chatId;
    private Integer index;
    private String senderName;
    private Long timestamp;
    private String text;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {this.senderName = senderName;}

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
