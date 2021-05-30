package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import org.junit.jupiter.api.Test;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Testing if all mappings work without throwing errors
 */
public class DTOsTest {


    @Test
    public void GameDTOtest(){
        Game game = new Game();
        game.setGameId(1l);
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertEntityToGameDTO(game));
    }

    @Test
    public void GameRoundDTOtest(){
        GameRound gameround = new GameRound();

        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertEntityToGameRoundDTO(gameround));
    }

    @Test
    public void GameRoundSummaryDTOtest(){
        GameRoundSummary gameRoundSummary = new GameRoundSummary();
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertEntityToGameRoundSummaryDTO(gameRoundSummary));
    }

    @Test
    public void GameSettingDTOTest(){
        GameSettings gameSettings = new GameSettings();
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertEntityToGameSettingsDTO(gameSettings));
    }

    @Test
    public void GameSettingsPostDTO(){
        GameSettingsPostDTO gameSettings = new GameSettingsPostDTO();
        gameSettings.setTotalRounds(2);
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertGameSettingsPostDTOToEntity(gameSettings));
    }

    @Test
    public void MessageChannelDTO(){
        MessageChannel messageChannel = new MessageChannel();
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertEntityToMessageChannelDTO(messageChannel));
    }

    @Test
    public void MessageDTO(){
        Message message = new Message();
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertEntityToMessageDTO(message));
    }

    @Test
    public void MessagePostDTO(){
        MessagePostDTO messagePostDTO = new MessagePostDTO();
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertMessagePostDTOtoEntity(messagePostDTO));
    }

    @Test
    public void UserDTO(){
        User user = new User();
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertEntityToUserDTO(user));
    }

    @Test
    public void userPutDTO(){
        UserPutDTO userPutDTO = new UserPutDTO();
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO));
    }

    @Test
    public void userLoginDTO(){
        User user = new User();
        assertDoesNotThrow( () -> DTOMapper.INSTANCE.convertEntityToUserLoginDTO(user));
    }

    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {

        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("username");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getPassword(), user.getPassword());
        assertEquals(userPostDTO.getUsername(), user.getUsername());
    }


    @Test
    public void gameSummaryDTOTest() {

        // given
        GameSummary gameSummary = new GameSummary();
        gameSummary.setGameSummaryId(1l);
        gameSummary.setGameState(GameState.LOBBY);
        gameSummary.setName("test");
        gameSummary.setSubreddit("testsub");
        gameSummary.setLastModified(123123123l);
        gameSummary.setRoundIds(Arrays.asList(new Long[]{2l, 3l}));
        gameSummary.setGameChatId(5l);

        // convert
        GameSummaryDTO gameSummaryDTO = DTOMapper.INSTANCE.convertEntityToGameSummaryDTO(gameSummary);

        // test
        assertEquals(gameSummary.getGameSummaryId(), gameSummaryDTO.getId());
        assertEquals(gameSummary.getName(), gameSummaryDTO.getName());
        assertEquals(gameSummary.getGameChatId(), gameSummaryDTO.getGameChatId());
        assertEquals(gameSummary.getGameState(), gameSummaryDTO.getGameState());
        assertEquals(gameSummary.getScores(), gameSummaryDTO.getScores());
        assertEquals(gameSummary.getRoundIds(), gameSummaryDTO.getRoundIds());
        assertEquals(gameSummary.getSubreddit(), gameSummaryDTO.getSubreddit());
        assertEquals(gameSummary.getMemeType(), gameSummaryDTO.getMemeType());
        assertEquals(gameSummary.getLastModified(), gameSummaryDTO.getLastModified());

    }

    @Test
    public void SseUpdateDTOtest() {

        // given
        User user = new User();
        user.setUserId(2l);
        user.observeEntity(2L); // set user to listen to updates of 2l
        // testlist
        // generate DTO
        SseUpdateDTO sseUpdateDTO = DTOMapper.INSTANCE.convertEntityToSseUpdateDTO(user);

        // test
        assertEquals(user.getUserId(), sseUpdateDTO.getUserId());
        assertEquals(new ArrayList<>(), sseUpdateDTO.getLobbies());
    }

}
