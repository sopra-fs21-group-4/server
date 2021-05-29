package ch.uzh.ifi.hase.soprafs21.controller;


import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameSettingsPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private GameService gameService;

    @MockBean
    private DTOMapper dtoMapper;


    //Input: gameSettingsPostDTO
    //Output: GamePrivateDTO
    //PostMapping
    @Test
    public void TestCreateLobby() throws Exception{
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        GameSettingsPostDTO gameSettingsPostDTO = new GameSettingsPostDTO();
        gameSettingsPostDTO.setPassword("somePW");
        gameSettingsPostDTO.setMaxAftermathSeconds(10);
        gameSettingsPostDTO.setMaxPlayers(10);
        gameSettingsPostDTO.setName("SomeName");
        gameSettingsPostDTO.setMaxSuggestSeconds(10);
        gameSettingsPostDTO.setMaxVoteSeconds(10);
        gameSettingsPostDTO.setSubreddit("SomeSubreddit");
        gameSettingsPostDTO.setMemeType(MemeType.HOT);
        gameSettingsPostDTO.setTotalRounds(1);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setGameSettingsId(1L);
        gameSettings.setPassword("somePW");
        gameSettings.setMaxAftermathSeconds(10);
        gameSettings.setMaxPlayers(10);
        gameSettings.setName("SomeName");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setSubreddit("SomeSubreddit");
        gameSettings.setMaxVoteSeconds(10);
        gameSettings.setMaxSuggestSeconds(10);
        gameSettings.setTotalRounds(1);

        Game game = new Game();
        game.setGameId(1L);
        game.adaptSettings(gameSettings);
        game.initialize(user);

        GameDTO gameDTO = new GameDTO();
        gameDTO.setId(1L);

        given(userService.verifyUser(Mockito.any(),Mockito.any())).willReturn(user);
        given(dtoMapper.convertGameSettingsPostDTOToEntity(Mockito.any())).willReturn(gameSettings);
        given(gameService.createGame(Mockito.any(), Mockito.any())).willReturn(game);
        given(dtoMapper.convertEntityToGameDTO(Mockito.any())).willReturn(gameDTO);

        MockHttpServletRequestBuilder postRequest = post("/games/create")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameSettingsPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(gameDTO.getId().intValue())));

    }

    @Test
    public void TestUpdateGameSettings() throws Exception{
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        GameSettingsPostDTO gameSettingsPostDTO = new GameSettingsPostDTO();
        gameSettingsPostDTO.setPassword("somePW");
        gameSettingsPostDTO.setMaxAftermathSeconds(10);
        gameSettingsPostDTO.setMaxPlayers(10);
        gameSettingsPostDTO.setName("SomeName");
        gameSettingsPostDTO.setMaxSuggestSeconds(10);
        gameSettingsPostDTO.setMaxVoteSeconds(10);
        gameSettingsPostDTO.setSubreddit("SomeSubreddit");
        gameSettingsPostDTO.setMemeType(MemeType.HOT);
        gameSettingsPostDTO.setTotalRounds(1);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setGameSettingsId(1L);
        gameSettings.setPassword("somePW");
        gameSettings.setMaxAftermathSeconds(10);
        gameSettings.setMaxPlayers(10);
        gameSettings.setName("SomeName");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setSubreddit("SomeSubreddit");
        gameSettings.setMaxVoteSeconds(10);
        gameSettings.setMaxSuggestSeconds(10);
        gameSettings.setTotalRounds(1);

        Game game = new Game();
        game.setGameId(1L);
        game.adaptSettings(gameSettings);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        given(dtoMapper.convertGameSettingsPostDTOToEntity(Mockito.any())).willReturn(gameSettings);
        given(gameService.adaptGameSettings(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(game);

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId().toString() + "/update")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameSettingsPostDTO));

        mockMvc.perform(putRequest).andExpect(status().isNoContent());

    }

    @Test
    public void TestStartGame() throws Exception{
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        Game game = new Game();
        game.setGameId(1L);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        doNothing().when(gameService).startGame(Mockito.any(), Mockito.any(), anyBoolean());

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId().toString() + "/start")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(gameService).startGame(game.getGameId(), user.getUserId(), true);
    }

    @Test
    public void TestJoinGame() throws Exception{
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        User gm = new User();
        gm.setUserId(2L);

        GameSettings gameSettings = new GameSettings();
        gameSettings.setGameSettingsId(1L);
        gameSettings.setPassword("somePW");
        gameSettings.setMaxAftermathSeconds(10);
        gameSettings.setMaxPlayers(10);
        gameSettings.setName("SomeName");
        gameSettings.setMemeType(MemeType.HOT);
        gameSettings.setSubreddit("SomeSubreddit");
        gameSettings.setMaxVoteSeconds(10);
        gameSettings.setMaxSuggestSeconds(10);
        gameSettings.setTotalRounds(1);

        Game game = new Game();
        game.setGameId(1L);
        game.adaptSettings(gameSettings);
        game.initialize(gm);
        game.enrollPlayer(user, gameSettings.getPassword());

        GameDTO gameDTO = new GameDTO();
        gameDTO.setId(1L);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        given(gameService.joinGame(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(game);
        given(dtoMapper.convertEntityToGameDTO(Mockito.any())).willReturn(gameDTO);


        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId().toString() + "/join")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .header("password", gameSettings.getPassword())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(gameDTO.getId().intValue())));

    }

    @Test
    public void TestLeaveGame() throws Exception {
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        Game game = new Game();
        game.setGameId(1L);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        doNothing().when(gameService).leaveGame(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId().toString() + "/leave")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(gameService).leaveGame(game.getGameId(), user);
    }

    @Test
    public void TestGameReady() throws Exception {
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        Game game = new Game();
        game.setGameId(1L);

        Boolean readyStatus = true;

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        doNothing().when(gameService).setPlayerReady(Mockito.any(), Mockito.any(), anyBoolean());

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId().toString() + "/ready")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(readyStatus));

        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(gameService).setPlayerReady(game.getGameId(), user, readyStatus);
    }

    @Test
    public void TestSuggestTitle() throws Exception {
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        Game game = new Game();
        game.setGameId(1L);

        String someSuggestion = "For Example Flabbernabberdubdub";

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        doNothing().when(gameService).putSuggestion(Mockito.any(), Mockito.any(), anyString());

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId().toString() + "/suggest")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(someSuggestion);

        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(gameService).putSuggestion(game.getGameId(), user, someSuggestion);
    }

    @Test
    public void TestPutVote() throws Exception {
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        Game game = new Game();
        game.setGameId(1L);

        Long Vote = 10L;

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        doNothing().when(gameService).putVote(Mockito.any(), Mockito.any(), anyLong());

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId().toString() + "/vote")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(Vote));

        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(gameService).putVote(game.getGameId(), user, Vote);
    }

    @Test
    public void TestBanPlayer() throws Exception {
        User gameMasterUser = new User();
        gameMasterUser.setStatus(UserStatus.IDLE);
        gameMasterUser.setEmail("firstname@lastname");
        gameMasterUser.setUserId(1L);
        gameMasterUser.setUsername("Thomas");
        gameMasterUser.setPassword("somePassword");
        gameMasterUser.setToken("someToken");
        gameMasterUser.setCurrentGameId(2L);

        Game game = new Game();
        game.setGameId(1L);

        Long toBanId = 5L;

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(gameMasterUser);
        doNothing().when(gameService).banPlayer(Mockito.any(), Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId().toString() + "/ban")
                .header("userId", gameMasterUser.getUserId())
                .header("token", gameMasterUser.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(toBanId));

        mockMvc.perform(putRequest).andExpect(status().isOk());
        verify(gameService).banPlayer(game.getGameId(), gameMasterUser.getUserId(), toBanId);
    }

    @Test
    public void TestGetArchive() throws Exception {
        User user = new User();
        user.setStatus(UserStatus.IDLE);
        user.setEmail("firstname@lastname");
        user.setUserId(1L);
        user.setUsername("Thomas");
        user.setPassword("somePassword");
        user.setToken("someToken");
        user.setCurrentGameId(2L);

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserId(1L);

        given(userService.verifyUser(Mockito.any(), Mockito.any())).willReturn(user);
        given(dtoMapper.convertEntityToUserLoginDTO(Mockito.any())).willReturn(userLoginDTO);

        MockHttpServletRequestBuilder getRequest = get("/archive")
                .header("userId", user.getUserId())
                .header("token", user.getToken())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest).andExpect(status().isOk())
                                    .andExpect(jsonPath("$.userId", is(user.getUserId().intValue())));

    }


    private String asJsonString ( final Object object){
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
