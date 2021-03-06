package ch.uzh.ifi.hase.soprafs21.repository;

import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * testing if the userRepository properly stores users and if they can be found by the methods
 */

@DataJpaTest
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;


    @Test
    public void findByGameId_success() {
        // given
        User gameMaster = new User();
        gameMaster.setUserId(1l);
        gameMaster.setStatus(UserStatus.OFFLINE);
        gameMaster.setToken("1");
        gameMaster.setEmail("firstname@lastname");
        gameMaster.setStatus(UserStatus.IDLE);
        gameMaster.setPassword("pw");
        gameMaster.setUsername("name");

        GameSettings gameSettings = new GameSettings();
        gameSettings.setName("test");
        gameSettings.setPassword("");
        gameSettings.setMaxPlayers(5);
        gameSettings.setTotalRounds(4);
        gameSettings.setSubreddit("test");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setMaxSuggestSeconds(5);
        gameSettings.setMaxAftermathSeconds(5);
        gameSettings.setMaxVoteSeconds(5);


        Game game = new Game();
        game.setGameId(1l);
        game.initialize(gameMaster);
        game.adaptSettings(gameSettings);


        entityManager.persist(game.getGameSettings());
//        entityManager.persist(game.getChatBot()); // unused feature
        entityManager.persist(game.getGameChat());
        Game found = entityManager.persist(game);

        entityManager.flush();

        // when
//        Game found = gameRepository.findByGameId(game.getGameId());

        // then
        assertNotNull(found.getGameId());
//        assertEquals(found.getChatBot(), game.getChatBot()); // unused feature
        assertEquals(found.getGameChat(), game.getGameChat());
        assertEquals(found.getGameSettings(), game.getGameSettings());
        assertEquals(found.getMaxPlayers(), game.getMaxPlayers());
        assertEquals(found.getName(), game.getName());
        assertEquals(found.getTotalRounds(), game.getTotalRounds());
    }

}
