package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.service.UserService;

import javax.persistence.*;
import java.io.Serializable;

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

    @ManyToOne
    private MessageChannel messageChannel;

    @ManyToOne
    private User sender;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private String text;

    public Message() {
    }


    public Long getMessageId() {
        return messageId;
    }

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public void setMessageChannel(MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
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

    @Override
    public int compareTo(Message o) {
        return (int) (this.timestamp - o.timestamp);
    }
}
