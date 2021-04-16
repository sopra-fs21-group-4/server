package ch.uzh.ifi.hase.soprafs21.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MEMEVOTE")
public class MemeVote {

    @Id
    @GeneratedValue
    private long memeVoteId;
    private Long lobbyId;
    private Long forUserId; // for which user is this vote
    private Long fromUserId; // user who cast the vote
    private int round;

    public long getMemeVoteId() {
        return memeVoteId;
    }

    public void setMemeVoteId(long memeVoteId) {
        this.memeVoteId = memeVoteId;
    }

    public Long getForUserId() {
        return forUserId;
    }

    public void setForUserId(Long forUserId) {
        this.forUserId = forUserId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
