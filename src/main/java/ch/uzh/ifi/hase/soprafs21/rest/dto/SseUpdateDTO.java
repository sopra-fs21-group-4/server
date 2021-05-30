package ch.uzh.ifi.hase.soprafs21.rest.dto;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameSummaryRepository;
import ch.uzh.ifi.hase.soprafs21.repository.MessageChannelRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;

import java.util.*;

public class SseUpdateDTO {

    private Long userId;
    private List<Long> lobbies = new ArrayList<>();
    private Set<Long> idPool;
    private final Map<Long, EntityDTO> observedEntities = new HashMap<>();




    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setObservedEntities(Set<Long> idPool) {
        this.idPool = idPool;
    }

    public Map<Long, EntityDTO> getObservedEntities() {
        return observedEntities;
    }

    public List<Long> getLobbies() {
        return lobbies;
    }

    private void collectRecursive(Long id) {
        if (id == null || observedEntities.containsKey(id)) return;
        EntityDTO dto = EntityDTO.find(id);
        if (dto == null) return;
        dto.crop(userId, null);
        observedEntities.put(id, dto);
        for (Long childId : dto.getChildren()) {
            collectRecursive(childId);
        }
    }

    public void init() {
        Collection<Game> runningGames = SpringContext.getBean(GameService.class).getRunningGames();
        for (Game game : runningGames) {
            if (game.getGameState() == GameState.LOBBY) lobbies.add(game.getGameId());
        }

        idPool.add(userId);
        idPool.addAll(lobbies); // automatically observe lobbies
        for (Long id : idPool) {
            collectRecursive(id);
        }
    }

    public boolean filter(Map<Long, Long> clientVersion, Long now) {
        assert(clientVersion != null);
        boolean updateFlag = false;
        Set<Long> idPool = new HashSet<>(observedEntities.keySet());

        for (Long id : idPool) {
            Long lastReceived = clientVersion.get(id);
            Long lastModified = observedEntities.get(id).getLastModified();
            if (lastReceived == null || lastReceived < lastModified) {
                clientVersion.put(id, now);
                updateFlag = true;
            } else {
                observedEntities.remove(id);
            }
        }
        return updateFlag;
    }
}
