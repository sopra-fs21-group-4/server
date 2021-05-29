package ch.uzh.ifi.hase.soprafs21.constant;

import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.repository.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * list of entities that can be observed by a client
 */
public enum EntityType {
    USER, GAME, GAME_SUMMARY, GAME_ROUND_SUMMARY, GAME_SETTINGS, GAME_ROUND, MESSAGE_CHANNEL, MESSAGE, UNKNOWN;


    private static final Map<Long, EntityType> knownTypes = new HashMap<>();
    private static final Map<JpaRepository, EntityType> repoTypes = new HashMap<>();
    private static final Map<EntityType, JpaRepository> typeRepos = new HashMap<>();



    public static EntityType get(Long id) {
        if (repoTypes.isEmpty()) {
            repoTypes.put(SpringContext.getBean(UserRepository.class), USER);
            repoTypes.put(SpringContext.getBean(GameRepository.class), GAME);
            repoTypes.put(SpringContext.getBean(GameSummaryRepository.class), GAME_SUMMARY);
            repoTypes.put(SpringContext.getBean(GameRoundSummaryRepository.class), GAME_ROUND_SUMMARY);
            repoTypes.put(SpringContext.getBean(GameSettingsRepository.class), GAME_SETTINGS);
            repoTypes.put(SpringContext.getBean(GameRoundRepository.class), GAME_ROUND);
            repoTypes.put(SpringContext.getBean(MessageChannelRepository.class), MESSAGE_CHANNEL);
            repoTypes.put(SpringContext.getBean(MessageRepository.class), MESSAGE);
            for (JpaRepository repo : repoTypes.keySet()) typeRepos.put(repoTypes.get(repo), repo);
        }
        EntityType type = knownTypes.get(id);
        if (type != null) return type;
        for (JpaRepository repo : repoTypes.keySet()) {
            if (repo.existsById(id)) {
                type = repoTypes.get(repo);
                knownTypes.put(id, type);
                return type;
            }
        }
        return UNKNOWN;
    }

//    public static JpaRepository getRepo(EntityType type) {
//        return typeRepos.get(type);
//    }

    public static JpaRepository getRepo(Long id) {
        return typeRepos.get(get(id));
    }

}
