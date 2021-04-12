package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class ChatGetDTO {

    private Long chatId;
    private Integer length;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer index) {
        this.length = length;
    }
}
