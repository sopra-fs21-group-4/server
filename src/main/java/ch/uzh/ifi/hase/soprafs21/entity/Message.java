package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Message representation
 * This class composes the internal representation of Messages and
 * defines how they are stored in the database.
 */

@Entity
@Table(name="MESSAGE")
public class Message implements Serializable, Comparable<Message> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long messageId;

    @Column
    private Long senderId;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private String text;


    public Long getMessageId() {
        return messageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

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

    public void setMessageId(Long messageId) {this.messageId = messageId;}

    public Long getLastModified() {
        return timestamp;
    }

    @Override
    public int compareTo(Message o) {
        return (int) (this.timestamp - o.timestamp);
    }

    public List<String> getReferences() {
        List<String> references = new ArrayList<>();
        for (String word : text.split(" ")) if (word.startsWith("@")) references.add(word);
        return references;
    }

    public String getCommand() {
        for (String word : text.split(" ")) if (word.startsWith("/")) return word;
        return null;
    }

    public String getArgument(int argNo) {
        int cmdIdx;
        String[] words = text.split(" ");
        for (cmdIdx = 0; cmdIdx < words.length; cmdIdx++) if (words[cmdIdx].startsWith("/")) break;
        if (cmdIdx == words.length || cmdIdx+argNo >= words.length) return null;
        return words[cmdIdx + argNo];
    }

}
