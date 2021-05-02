package ch.uzh.ifi.hase.soprafs21.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.aspectj.lang.annotation.Before;
import org.hibernate.mapping.Collection;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.stubbing.VoidAnswer6;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs21.Application;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.entity.GameSummary;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetFullDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import jdk.jshell.SourceCodeAnalysis.Suggestion;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK, classes={Application.class})
public class GameControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;
    private UserService userService;

    
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
    @BeforeEach
    public void setUp()
    {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test //@PostMapping("/games/create") --> wont run
    public void CreateLobbyTest() throws Exception {
        // given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");

        //Body
        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);;
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);

        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);

        //result out of user and gameSettings
        Game createdGame = gameService.createGame(user, gameSettings);

        //given(userService.verifyUser(user.getUserId(), user.getToken())).willReturn(user);
        //given(gameService.createGame(user, gameSettings)).willReturn(createdGame);
         
        when(gameService.createGame(user, gameSettings)).thenReturn(createdGame);
        
        //then --> return "is Created Status", JSON of created Game
        mockMvc.perform(post("/games/create")
            .content(asJsonString(gameSettingsDTO))
            .header("userId", user.getUserId(),"token", user.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.gameId").value(createdGame.getGameId()))
            .andExpect(jsonPath("$.gameSettings").value(createdGame.getGameSettings()));
    }


    /**
     * update a game's settings test
     */
    //@PutMapping("/games/{gameId}/update")
    @Test
    public void adaptGameSettings() throws Exception
    {
        
    // given
    //Header
    User user = new User();
    user.setUserId(1L);
    user.setUsername("Jim");
    user.setToken("UniqueToken");
    //Body
    GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
    gameSettingsDTO.setName("Jim");
    gameSettingsDTO.setPassword("SomePassword");
    gameSettingsDTO.setMaxPlayers(15);
    gameSettingsDTO.setTotalRounds(7);
    gameSettingsDTO.setSubreddit("/some/Subreddit");
    gameSettingsDTO.setMemeType(MemeType.HOT);
    gameSettingsDTO.setMaxSuggestSeconds(45);
    gameSettingsDTO.setMaxVoteSeconds(52);
    gameSettingsDTO.setMaxAftermathSeconds(10);
    GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
    //result out of user and gameSettings
    Game createdGame = gameService.createGame(user, gameSettings);
    //result out of user and gameSettings and the game
    Game adaptedGame = gameService.adaptGameSettings(createdGame.getGameId(), user, gameSettings);
    //given end


    //when
    MockHttpServletRequestBuilder builder =
            MockMvcRequestBuilders.put("/games/" + createdGame.getGameId() + "/update")//pathvariablesofRequest
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("userId", user.getUserId(), "token", user.getToken())//headerofRequest
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(asJsonString(gameSettingsDTO));//bodyofRequest
    //then
    this.mockMvc.perform(builder)
                                .andExpect(MockMvcResultMatchers.status().isOk()
                                .andExpect(MockMvcResultMatchers.jsonPath("$.gameId", adaptedGame.getGameId()))
                                .andExpect(MockMvcResultHandlers.jsonPath("$.gameSettings", adaptedGame.getGameSettings()));


    }

    /**
     * start the game test
     */
    //@PutMapping("/games/{gameId}/start")
    @Test
    public void startGame() throws Exception
    {
        // given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);

        Game existingGame = new Game();
        existingGame.setGameId(12345L);
        existingGame.adaptSettings(gameSettings); //not sure if this is even necessary but game might not be able to start without some settings provided

        //Body of Request
        //none

        //when
        MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put("/games/" + existingGame.getGameId() + "/start")//pathvariablesofRequest
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("userId", user.getUserId())//headerofRequest
                            .header("token", user.getToken())//headerofRequest
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            //noBodyofRequest
        //then
        this.mockMvc.perform(builder)
        .andExpect(MockMvcResultMatchers.status().()//ToDo specify the HttpStatus.No_Content

    }

    /**
     * join a game test
     */
    //@PutMapping("/games/{gameId}/join")
    @Test
    public GameGetFullDTO TestjoinLobby() throws Exception
    {
        // given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        user.setPassword("somePassword");

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);

        Game existingGame = new Game();
        existingGame.setGameId(12345L);
        existingGame.adaptSettings(gameSettings); //not sure if this is even necessary but game might not be able to start without some settings provided

        //Body of Request
        //none

        //when
        MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put("/games/" + existingGame.getGameId() + "/join")//pathvariablesofRequest
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("userId", user.getUserId())//headerofRequest
                            .header("token", user.getToken())//headerofRequest
                            .header("password", user.getPassword())//headerofRequest
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            //noBodyofRequest
        //then
        this.mockMvc.perform(builder)
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.gameId", existingGame.getGameId()))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.[0]playerStates", existingGame.getPresentPlayers()));
    }

    /**
     * leave a game test
     */
    //@PutMapping("/games/{gameId}/leave")
    @Test
    public void TestleaveLobby() throws Exception
    {
        // given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);

        Game existingGame = new Game();
        existingGame.setGameId(12345L);
        existingGame.adaptSettings(gameSettings); //not sure if this is even necessary but game might not be able to start without some settings provided

        //Body of Request
        //none

        //when
        MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put("/games/" + existingGame.getGameId() + "/leave")//pathvariablesofRequest
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("userId", user.getUserId())//headerofRequest
                            .header("token", user.getToken())//headerofRequest
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8");
                            //noBodyofRequest
        //then
        this.mockMvc.perform(builder)
        .andExpect(MockMvcResultMatchers.status().()//ToDo specify the HttpStatus.No_Content
        .andExpect(MockMvcResultMatchers.jsonPath(null, user.getCurrentGameId());//gameId of the user that left becomes null
    }

    /**
     * set a player ready test
     */
    //@PutMapping("/games/{gameId}/ready")
    @Test
    public void ReadyTest_Ready() throws Exception
    {
        // given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);

        Game existingGame = new Game();
        existingGame.setGameId(12345L);
        existingGame.adaptSettings(gameSettings); //not sure if this is even necessary but game might not be able to start without some settings provided

        //Body of Request
        Boolean readyCheck = true;

        //when
        MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put("/games/" + existingGame.getGameId() + "/ready")//pathvariablesofRequest
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("userId", user.getUserId())//headerofRequest
                            .header("token", user.getToken())//headerofRequest
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8");
                            .content(asJsonString(readyCheck));
        //then
        this.mockMvc.perform(builder)
        .andExpect(MockMvcResultMatchers.status().()//ToDo specify the HttpStatus.No_Content
    }

    @Test
    public void ReadyTest_NotReady() throws Exception
    {
        // given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);

        Game existingGame = new Game();
        existingGame.setGameId(12345L);
        existingGame.adaptSettings(gameSettings); //not sure if this is even necessary but game might not be able to start without some settings provided

        //Body of Request
        Boolean readyCheck = false;

        //when
        MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.put("/games/" + existingGame.getGameId() + "/ready")//pathvariablesofRequest
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("userId", user.getUserId())//headerofRequest
                            .header("token", user.getToken())//headerofRequest
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8");
                            .content(asJsonString(readyCheck));
        //then
        this.mockMvc.perform(builder)
        .andExpect(MockMvcResultMatchers.status().()//ToDo specify the HttpStatus.No_Content
    }

    /**
     * update game settings test
     */
    //@PutMapping("/games/{gameId}/updateSettings")
    public void TestupdateSettings() throws Exception
    {
        // given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        //Body
        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        //result out of user and gameSettings
        Game createdGame = gameService.createGame(user, gameSettings);
        //game settings are updated
        gameService.updateSettings(createdGame.getGameId(), user.getUserId(), gameSettings);
        //given end


        //when
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/games/" + createdGame.getGameId() + "/updateSettings")//pathvariablesofRequest
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("userId", user.getUserId(), "token", user.getToken())//headerofRequest
                                    .accept(MediaType.APPLICATION_JSON)
                                    .characterEncoding("UTF-8")
                                    .content(asJsonString(gameSettingsDTO));//bodyofRequest
        //then
        this.mockMvc.perform(builder)
                                    .andExpect(MockMvcResultMatchers.status().isOk();
    }



    /**
     * suggest a meme title test
     */
    //@PutMapping("/games/{gameId}/suggest")
    @Test 
    public void TestSuggestionForMemetitle() throws Exception
    {
        //given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        //result out of user and gameSettings
        Game createdGame = gameService.createGame(user, gameSettings);
        //Body
        String suggestion = "Some title the Player came up with";
        
        //when
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/games/" + createdGame.getGameId() + "/suggest")//pathvariablesofRequest
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("userId", user.getUserId(), "token", user.getToken())//headerofRequest
                                    .accept(MediaType.APPLICATION_JSON)
                                    .characterEncoding("UTF-8")
                                    .content(suggestion);//bodyofRequest
        //then
        this.mockMvc.perform(builder)
                                    .andExpect(MockMvcResultMatchers.status().isOk();

    }

    /**
     * vote for a player test
     */
    //@PutMapping("/games/{gameId}/vote")
    @Test
    public void TestputVote() throws Exception {
        //given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        //result out of user and gameSettings
        Game createdGame = gameService.createGame(user, gameSettings);
        //Body
        Long vote = 3L;

        //when
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.put("/games/" + createdGame.getGameId() + "/vote")//pathvariablesofRequest
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("userId", user.getUserId(), "token", user.getToken())//headerofRequest
                                    .accept(MediaType.APPLICATION_JSON)
                                    .characterEncoding("UTF-8")
                                    .content(asJsonString(vote));//bodyofRequest
        //then
        this.mockMvc.perform(builder)
                                    .andExpect(MockMvcResultMatchers.status().isOk());
    }


    /**
     * get information about all lobbies
     * the information returned is restricted and doesn't contain player-explicit information.
     */
    //@GetMapping("/games")
    @Test
    public void TestgetAllGames() throws Exception
    {
        //given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        user.setCurrentGameId(12345L);

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);

        GameSettingsDTO anothergameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Tim");
        gameSettingsDTO.setPassword("AnotherPassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/another/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(40);
        gameSettingsDTO.setMaxVoteSeconds(12);
        gameSettingsDTO.setMaxAftermathSeconds(8);
        //creating two random games
        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        GameSettings othergameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(anothergameSettingsDTO);
        gameService.createGame(user, gameSettings);
        gameService.createGame(user, othergameSettings);
        //retrieving the games that are in the repository into the list gameGetDTOs
        Collection<Game> games = gameService.getRunningGames();
        List<GameGetRestrictedDTO> gameGetDTOs = new ArrayList<>();
        for(Game game : games){
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetRestrictedDTO(game));
        }
        //given End
        //Body
        //noBodyexisting

        //when
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/games")//pathvariablesofRequest
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("userId", user.getUserId(), "token", user.getToken())//headerofRequest
                                    .accept(MediaType.APPLICATION_JSON)
                                    .characterEncoding("UTF-8");
                                    
        //then
        this.mockMvc.perform(builder)
                                    .andExpect(MockMvcResultMatchers.status().()); //some HttpStatus.partial_Content

        


    }

    /**
    * get specific game test
    */
    //@GetMapping("/games/{gameId}")
    @Test
    public void TestgetSingleGame() throws Exception
    {
        //given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        user.setCurrentGameId(12345L);

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);

        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        gameService.createGame(user, gameSettings);
        Game specificGame = gameService.findRunningGame(user.getCurrentGameId());

        //Body
        //nonexistent

        //when
        MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.get("/games/" + user.getCurrentGameId())//pathvariablesofRequest
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("userId", user.getUserId(), "token", user.getToken())//headerofRequest
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8");
                             
        //then
        this.mockMvc.perform(builder)
                            .andExpect(MockMvcResultMatchers.status().isOk()); //test if the specificGame is the same as the return of the mockMvc

    }

    /**
     * get specific game summary test
     */
    //@GetMapping("/archive/games/{gameId}")
    @Test
    public void TestgetGameSummary() throws Exception
    {
        //given
        //Header
        User user = new User();
        user.setUserId(1L);
        user.setUsername("Jim");
        user.setToken("UniqueToken");
        user.setCurrentGameId(12345L);

        GameSettingsDTO gameSettingsDTO = new GameSettingsDTO();
        gameSettingsDTO.setName("Jim");
        gameSettingsDTO.setPassword("SomePassword");
        gameSettingsDTO.setMaxPlayers(15);
        gameSettingsDTO.setTotalRounds(7);
        gameSettingsDTO.setSubreddit("/some/Subreddit");
        gameSettingsDTO.setMemeType(MemeType.HOT);
        gameSettingsDTO.setMaxSuggestSeconds(45);
        gameSettingsDTO.setMaxVoteSeconds(52);
        gameSettingsDTO.setMaxAftermathSeconds(10);

        GameSettings gameSettings = DTOMapper.INSTANCE.convertGameSettingsDTOToEntity(gameSettingsDTO);
        Game createdGame = gameService.createGame(user, gameSettings);
        GameSummary gameSummary = gameService.verifyReviewer(createdGame.getGameId(), user);
        //Body
        //noneexistent

        //when
        MockHttpServletRequestBuilder builder =
        MockMvcRequestBuilders.get("/archive/games/" + user.getCurrentGameId())//pathvariablesofRequest
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("userId", user.getUserId(), "token", user.getToken())//headerofRequest
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8");
                             
        //then
        this.mockMvc.perform(builder)
                            .andExpect(MockMvcResultMatchers.status().isOk()); //test if the specificGame is the same as the return of the mockMvc


    }
}
