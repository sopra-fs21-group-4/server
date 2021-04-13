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
    private static UserService userService = SpringContext.getBean(UserService.class);     // hacky steal the userService

    @Id
    @GeneratedValue
    @Column
    private Long messageId;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private String text;    // TODO max length?


    public Long getMessageId() {
        return messageId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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

    /**
     * gets the username of this Message's sender.
     * @return the sender's username if existent, null otherwise.
     */
    public String getSenderUsername() {
        User user = userService.getUserByUserId(senderId);
        return (user == null)? null : user.getUsername();
    }

    @Override
    public int compareTo(Message o) {
        return (int) (this.timestamp - o.timestamp);
    }
}
