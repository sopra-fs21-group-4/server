package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameSummaryRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SseUpdateDTO {

    private Long userId;
    private Map<Long, ObservableEntity> observedEntities;
    private Map<Long, EntityType> entityTypes;




    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setObservedEntities(Map<Long, EntityType> entityTypes) {
        this.entityTypes = entityTypes;
        this.observedEntities = new HashMap<>();
    }

    public Map<Long, ObservableEntity> getObservedEntities() {
        return observedEntities;
    }

    public Map<Long, EntityType> getEntityTypes() {
        return entityTypes;
    }

    public void init() {

        UserRepository userRepository = SpringContext.getBean(UserRepository.class);
        MessageChannelRepository messageChannelRepository = SpringContext.getBean(MessageChannelRepository.class);
        GameRepository gameRepository = SpringContext.getBean(GameRepository.class);
        GameSummaryRepository gameSummaryRepository = SpringContext.getBean(GameSummaryRepository.class);

        for (Long entityId : entityTypes.keySet()) {
            switch(entityTypes.get(entityId)) {
                case USER:      User user = userRepository.findByUserId(entityId);
                    observedEntities.put(entityId, DTOMapper.INSTANCE.convertEntityToUserPublicDTO(user));
                    break;
                case CHANNEL:   MessageChannel mc = messageChannelRepository.findByMessageChannelId(entityId);
                    observedEntities.put(entityId, DTOMapper.INSTANCE.convertEntityToMessageChannelGetDTO(mc));
                    break;
                case GAME:      GameSummary summary = gameSummaryRepository.findByGameId(entityId);
                    if (summary != null) {
                        observedEntities.put(entityId, DTOMapper.INSTANCE.convertEntityToGameSummaryDTO(summary));
                        break;
                    }
                    Game game = gameRepository.findByGameId(entityId);
                    if (game == null) break;
                    if (game.getPlayerState(userId).isEnrolled()) {
                        observedEntities.put(entityId, DTOMapper.INSTANCE.convertEntityToGamePrivateDTO(game));
                    } else {
                        observedEntities.put(entityId, DTOMapper.INSTANCE.convertEntityToGamePublicDTO(game));
                    }
                    break;

            }
        }
    }

    public Long filter(Long lastUpdated){

        long maxLastModified = 0;

        for (ObservableEntity e : observedEntities.values()) {
            long lastModified = e.filter(lastUpdated);
            maxLastModified = Math.max(maxLastModified, lastModified);
            if (lastModified < lastUpdated) observedEntities.remove(e.getId());
        }
        return maxLastModified;
    }
}
