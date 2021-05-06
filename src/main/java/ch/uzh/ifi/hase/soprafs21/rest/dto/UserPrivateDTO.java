package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameSummaryRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;

import java.util.*;

public class UserPrivateDTO {
    private Long userId; //checked
    private GamePrivateDTO currentGame; //checked all informations about the game we are currently in
    private Map<Long, MessageChannelGetDTO> subscribedMessageChannels;//checked
    //private List<MessageGetDTO> inbox; //not used atm (used as a tracker for the Chat)
    private Map<Long, UserPublicDTO> subscribedUsers; //checked
    private Map<Long, GameSummaryDTO> subscribedGameSummaries; //<GameSummaryId, GameSummaryDTO> //checked

    public Map<Long, GameSummaryDTO> getSubscribedGameSummaries() {
        return subscribedGameSummaries;
    }

    public void setSubscribedGameSummaries(Set<Long> subscribedGameSummaries) {
        GameSummaryRepository gameSummaryRepository = SpringContext.getBean(GameSummaryRepository.class);
        this.subscribedGameSummaries = new HashMap<>();
        for(Long gameSummaryId : subscribedGameSummaries){
            GameSummary gameSummary = gameSummaryRepository.findByGameId(gameSummaryId);
            this.subscribedGameSummaries.put(gameSummaryId, DTOMapper.INSTANCE.convertEntityToGameSummaryDTO(gameSummary));
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public GamePrivateDTO getCurrentGame() {
        return currentGame;
    }

    //Convert gameId to game; Converte game to a GameGetCompleteDTO
    public void setCurrentGame(Long gameId) {
        GameRepository gameRepository = SpringContext.getBean(GameRepository.class);
        Game ourGame = gameRepository.findByGameId(gameId);
        this.currentGame = DTOMapper.INSTANCE.convertEntityToGamePrivateDTO(ourGame);
    }

    public Map<Long, UserPublicDTO> getSubscribedUsers() {
        return subscribedUsers;
    }

    public void setSubscribedUsers(Set<Long> subscribedUsers) {
        UserRepository userRepository = SpringContext.getBean(UserRepository.class);
        this.subscribedUsers = new HashMap<>();
        for(Long userId : subscribedUsers){
            User user = userRepository.findByUserId(userId);
            this.subscribedUsers.put(userId, DTOMapper.INSTANCE.convertEntityToUserPublicDTO(user));
        }
    }

    public void setSubscribedMessageChannels(Set<Long> subscribedMessageChannels) {
        MessageChannelRepository messageChannelRepository = SpringContext.getBean(MessageChannelRepository.class);
        this.subscribedMessageChannels = new HashMap<>();
        for(Long messageChannelId : subscribedMessageChannels){
            MessageChannel messageChannel = messageChannelRepository.findByMessageChannelId(messageChannelId);
            this.subscribedMessageChannels.put(messageChannelId, DTOMapper.INSTANCE.convertEntityToMessageChannelGetDTO(messageChannel));
        }
    }

    public Map<Long, MessageChannelGetDTO> getSubscribedMessageChannels() {
        return subscribedMessageChannels;
    }


}
