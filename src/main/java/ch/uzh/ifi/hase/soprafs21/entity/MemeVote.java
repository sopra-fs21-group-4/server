package ch.uzh.ifi.hase.soprafs21.entity;


public class MemeVote {


    private Long lobbyId;
    private Long forUserId; // for which user is this vote
    private Long fromUserId; // user who cast the vote
    private int round;

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
