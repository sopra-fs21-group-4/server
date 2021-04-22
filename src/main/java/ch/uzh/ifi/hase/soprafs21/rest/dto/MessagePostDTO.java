package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class MessagePostDTO {

    // TODO don't think we need a DTO for a single String

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
