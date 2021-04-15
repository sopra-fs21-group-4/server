package ch.uzh.ifi.hase.soprafs21.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MEMETITLE")
public class MemeTitle {

    @Id
    @GeneratedValue
    private long memeTitleId;

    private Long lobbyId;
    private Long userId;
    private String title;
    private int round;

    public long getMemeTitleId() {
        return memeTitleId;
    }

    public void setMemeTitleId(long memeTitleId) {
        this.memeTitleId = memeTitleId;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
