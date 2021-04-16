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


    public Long getChatId() {
        return chatId;
    }
}
