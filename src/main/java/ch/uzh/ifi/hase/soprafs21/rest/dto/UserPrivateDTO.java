package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameSummaryRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;

import java.util.*;

public class UserPrivateDTO {
    private GamePrivateDTO currentGame; // all informations about the game we are currently in
    private Map<Long, String> observedEntities;
    private Map<Long, MessageChannelGetDTO> subscribedMessageChannels;
    private Map<Long, UserPublicDTO> subscribedUsers;
    private Map<Long, GameSummaryDTO> subscribedGameSummaries; //<GameSummaryId, GameSummaryDTO>


    //root timestamp
    private Long lastModified;


    public Long keepModified(Long lastUpdated){
        //GamePrivateDTO-subtree
        if (currentGame != null) {
            this.lastModified = Math.max(this.lastModified, currentGame.getLastModified());
            if(currentGame.keepModified(lastUpdated) < lastUpdated) this.currentGame = null;
        }
        //UserPublicDTO
        for(UserPublicDTO user : subscribedUsers.values()){
            this.lastModified = Math.max(this.lastModified, user.getLastModified());
            if(user.getLastModified() < lastUpdated) subscribedUsers.remove(user.getUserId());
        }
        //MessageChannelGetDTO
        for(MessageChannelGetDTO messageChannel : subscribedMessageChannels.values()){
            this.lastModified = Math.max(this.lastModified, messageChannel.getLastModified());
            if(messageChannel.getLastModified() < lastUpdated) subscribedMessageChannels.remove(messageChannel.getMessageChannelId());
        }
        return lastModified;
    }

    public Map<Long, GameSummaryDTO> getSubscribedGameSummaries() {
        return subscribedGameSummaries;
    }

    public void setSubscribedGameSummaries(Map<Long, EntityType> subscribedGameSummaries) {
        GameSummaryRepository gameSummaryRepository = SpringContext.getBean(GameSummaryRepository.class);
        this.subscribedGameSummaries = new HashMap<>();
        for(Long gameSummaryId : subscribedGameSummaries.keySet()){
            GameSummary gameSummary = gameSummaryRepository.findByGameId(gameSummaryId);
            if (gameSummary != null)
                this.subscribedGameSummaries.put(gameSummaryId, DTOMapper.INSTANCE.convertEntityToGameSummaryDTO(gameSummary));
        }
    }

    public GamePrivateDTO getCurrentGame() {
        return currentGame;
    }

    //Convert gameId to game; Convert game to a GameGetCompleteDTO
    public void setCurrentGame(Long gameId) {
        if (gameId == null) {
            this.currentGame = null;
            return;
        }
        GameRepository gameRepository = SpringContext.getBean(GameRepository.class);
        Game ourGame = gameRepository.findByGameId(gameId);
        if (ourGame == null) {
            this.currentGame = null;
            return;
        }
        this.currentGame = DTOMapper.INSTANCE.convertEntityToGamePrivateDTO(ourGame);
    }

    public Map<Long, UserPublicDTO> getSubscribedUsers() {
        return subscribedUsers;
    }

    public void setSubscribedUsers(Map<Long, EntityType> subscribedUsers) {
        UserRepository userRepository = SpringContext.getBean(UserRepository.class);
        this.subscribedUsers = new HashMap<>();
        for(Long userId : subscribedUsers.keySet()){
            User user = userRepository.findByUserId(userId);
            if (user != null)
                this.subscribedUsers.put(userId, DTOMapper.INSTANCE.convertEntityToUserPublicDTO(user));
        }
    }

    public void setSubscribedMessageChannels(Map<Long, EntityType> subscribedMessageChannels) {
        MessageChannelRepository messageChannelRepository = SpringContext.getBean(MessageChannelRepository.class);
        this.subscribedMessageChannels = new HashMap<>();
        for(Long messageChannelId : subscribedMessageChannels.keySet()){
            MessageChannel messageChannel = messageChannelRepository.findByMessageChannelId(messageChannelId);
            if (messageChannel != null)
                this.subscribedMessageChannels.put(messageChannelId, DTOMapper.INSTANCE.convertEntityToMessageChannelGetDTO(messageChannel));
        }
    }

    public Map<Long, MessageChannelGetDTO> getSubscribedMessageChannels() {
        return subscribedMessageChannels;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
}
