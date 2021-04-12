package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal Chat representation
 * This class composes the internal representation of a Chat and
 * defines how it is stored in the database.
 */

@Entity
@Table(name="CHAT")
public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column
    private Long chatId;

    @Column(nullable = false)
    private Integer length = 0;


    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getLength() {
        return length;
    }

    /**
     * synchronized post-increment method.
     * This replaces setLength() in order to return unique message indices.
     * increments the length attribute by 1 and returns the old value.
     * @return length
     */
    public synchronized Integer incrementLength() {
        return length++;
    }
}
