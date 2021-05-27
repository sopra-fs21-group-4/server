package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.EntityType;
import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface EntityDTO {
    Long getId();
    Long getLastModified();
    EntityType getType();
    Set<Long> getChildren();
    void crop(Long receiverId, String cropHint);


    public static EntityDTO find(Long id) {
        EntityType type = EntityType.get(id);
        JpaRepository repo = EntityType.getRepo(id);
        if (repo == null) return null;
        Optional entity = repo.findById(id);
        if (!entity.isPresent()) return null;
        switch(type) {
            case USER: return DTOMapper.INSTANCE.convertEntityToUserDTO((User) entity.get());
            case GAME: return DTOMapper.INSTANCE.convertEntityToGameDTO((Game) entity.get());
            case GAME_SUMMARY: return DTOMapper.INSTANCE.convertEntityToGameSummaryDTO((GameSummary) entity.get());
            case GAME_ROUND_SUMMARY: return DTOMapper.INSTANCE.convertEntityToGameRoundSummaryDTO((GameRoundSummary) entity.get());
            case GAME_SETTINGS: return DTOMapper.INSTANCE.convertEntityToGameSettingsDTO((GameSettings) entity.get());
            case GAME_ROUND: return DTOMapper.INSTANCE.convertEntityToGameRoundDTO((GameRound) entity.get());
            case MESSAGE_CHANNEL: return DTOMapper.INSTANCE.convertEntityToMessageChannelDTO((MessageChannel) entity.get());
            case MESSAGE: return DTOMapper.INSTANCE.convertEntityToMessageDTO((Message) entity.get());

            default: return null;
        }
    }
}