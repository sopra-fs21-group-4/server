package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

//unknownConflict
/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 * convertEntitytoDTO source is Entity converDTOtoEntity source is DTO
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    // USERS

    // user login
    @Mapping(source = "token", target = "token")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "pastGames", target = "pastGames")
    UserLoginDTO convertEntityToUserLoginDTO(User user);

    // user creation
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);


    // update user profile
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);


    // GAMES

    // game settings
    @Mapping(source = "name", target = "name")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "maxSuggestSeconds", target = "maxSuggestSeconds")
    @Mapping(source = "maxVoteSeconds", target = "maxVoteSeconds")
    @Mapping(source = "maxAftermathSeconds", target = "maxAftermathSeconds")
    GameSettings convertGameSettingsPostDTOToEntity(GameSettingsPostDTO gameSettingsPostDTO);


    // MESSAGES

    // posting messages
    @Mapping(target = "timestamp", expression = "java(null)")   // generated
    @Mapping(target = "senderId", expression = "java(null)")    // taken from request header
    @Mapping(source = "text", target = "text")
    Message convertMessagePostDTOtoEntity(MessagePostDTO messagePostDTO);

    // SSE_UPDATE_DTO

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "observedEntities", target = "observedEntities")
    SseUpdateDTO convertEntityToSseUpdateDTO(User user);

    // ENTITY_DTOS

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "currentGameId", target = "currentGameId")
    @Mapping(source = "friends", target = "friends")
    @Mapping(source = "outgoingFriendRequests", target = "outgoingFriendRequests")
    @Mapping(source = "incomingFriendRequests", target = "incomingFriendRequests")
    @Mapping(source = "lastModified", target = "lastModified")
    UserDTO convertEntityToUserDTO(User entity);

    @Mapping(source = "gameId", target = "id")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "roundCounter", target = "roundCounter")
    @Mapping(source = "advanceTargetTime", target = "advanceTargetTime")
    @Mapping(source = "gameChat", target = "gameChatId")
    @Mapping(source = "gameMaster", target = "gameMaster")
    @Mapping(source = "presentPlayers", target = "players")
    @Mapping(source = "playerStates", target = "playerStates")
    @Mapping(source = "scores", target = "scores")
    @Mapping(source = "gameSettings", target = "gameSettings")
    @Mapping(source = "currentRound", target = "currentRound")
    @Mapping(source = "gameSummaryId", target = "gameSummaryId")
    @Mapping(source = "lastModified", target = "lastModified")
    GameDTO convertEntityToGameDTO(Game entity);

    @Mapping(source = "gameSummaryId", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "gameChatId", target = "gameChatId")
    @Mapping(source = "scores", target = "scores")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "roundIds", target = "roundIds")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "lastModified", target = "lastModified")
    GameSummaryDTO convertEntityToGameSummaryDTO(GameSummary entity);

    @Mapping(source = "gameRoundSummaryId", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "memeURL", target = "memeURL")
    @Mapping(source = "suggestions", target = "suggestions")
    @Mapping(source = "votes", target = "votes")
    @Mapping(source = "scores", target = "scores")
    @Mapping(source = "lastModified", target = "lastModified")
    GameRoundSummaryDTO convertEntityToGameRoundSummaryDTO(GameRoundSummary entity);

    @Mapping(source = "gameSettingsId", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "memesFound", target = "memesFound")
    @Mapping(source = "maxSuggestSeconds", target = "maxSuggestSeconds")
    @Mapping(source = "maxVoteSeconds", target = "maxVoteSeconds")
    @Mapping(source = "maxAftermathSeconds", target = "maxAftermathSeconds")
    @Mapping(source = "lastModified", target = "lastModified")
    GameSettingsDTO convertEntityToGameSettingsDTO(GameSettings entity);

    @Mapping(source = "gameRoundId", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "memeURL", target = "memeURL")
    @Mapping(source = "roundPhase", target = "roundPhase")
    @Mapping(source = "suggestions", target = "suggestions")
    @Mapping(source = "votes", target = "votes")
    @Mapping(source = "scores", target = "scores")
    @Mapping(source = "lastModified", target = "lastModified")
    GameRoundDTO convertEntityToGameRoundDTO(GameRound entity);

    @Mapping(source = "messageChannelId", target = "id")
//    @Mapping(source = "confidential", target = "confidential") // unused feature
//    @Mapping(source = "closed", target = "closed") // unused feature
    @Mapping(source = "roles", target = "roles")
    @Mapping(source = "messages", target = "messages")
    @Mapping(source = "associatedGameId", target = "associatedGameId")
    @Mapping(source = "lastModified", target = "lastModified")
    MessageChannelDTO convertEntityToMessageChannelDTO(MessageChannel entity);

    @Mapping(source = "messageId", target = "id")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "senderId", target = "senderId")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "lastModified", target = "lastModified")
    MessageDTO convertEntityToMessageDTO(Message entity);

}
