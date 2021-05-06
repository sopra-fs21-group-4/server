package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Message;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;

import java.util.ArrayList;
import java.util.List;

public class UpdatePrivateUserDTO {
    private Long userId;
    private String username;
    private String email;
    private UserStatus status;
    private Long currentGameId;
    private GameGetCompleteDTO currentGame;
    private List<Long> subscribedMessageChannels;
    private List<MessageGetDTO> inbox;
    private List<UserGetLimitedDTO> friends;
    private List<UserGetLimitedDTO> outgoingFriendRequests;
    private List<UserGetLimitedDTO> incomingFriendRequests;


    public Long getCurrentGame() {
        return currentGameId;
    }

    public void setCurrentGameId(Long gameId) {
        this.currentGameId = currentGameId;
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

    public List<Long> getSubscribedMessageChannels() {
        return subscribedMessageChannels;
    }

    public void setSubscribedMessageChannels(List<Long> subscribedMessageChannels) {
        this.subscribedMessageChannels = subscribedMessageChannels;
    }

    public List<MessageGetDTO> getInbox() {
        return inbox;
    }

    public void setInbox(List<Message> inbox) {
        this.inbox = new ArrayList<>();
        for (Message message : inbox) {
            this.inbox.add(DTOMapper.INSTANCE.convertEntityToMessageGetDTO(message));
        }
    }

    public List<UserGetLimitedDTO> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = new ArrayList<>();
        for (User user : friends) {
            this.friends.add(DTOMapper.INSTANCE.convertEntityToUserGetLimitedDTO(user));
        }
    }

    public List<UserGetLimitedDTO> getOutgoingFriendRequests() {
        return outgoingFriendRequests;
    }

    public void setOutgoingFriendRequests(List<User> outgoingFriendRequests) {
        this.friends = new ArrayList<>();
        for (User user : outgoingFriendRequests) {
            this.outgoingFriendRequests.add(DTOMapper.INSTANCE.convertEntityToUserGetLimitedDTO(user));
        }
    }

    public List<UserGetLimitedDTO> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }

    public void setIncomingFriendRequests(List<User> incomingFriendRequests) {
        this.friends = new ArrayList<>();
        for (User user : incomingFriendRequests) {
            this.incomingFriendRequests.add(DTOMapper.INSTANCE.convertEntityToUserGetLimitedDTO(user));
        }
    }

}
