package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Lobby;

public class UserGetDTO {

    private Long userId;
    private String username;
    private UserStatus status;
    private Long currentLobby; // special because we only want to return lobby id and not lobby object
    private String email;

    public Long getCurrentLobby() {
        return currentLobby;
    }

    public void setCurrentLobby(Lobby currentLobby) {
        if(currentLobby==null){
            this.currentLobby = null;
        }
        else{
        this.currentLobby = currentLobby.getLobbyId();
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getEmail(){return email;}

    public void setEmail(String email){this.email = email;}
}
