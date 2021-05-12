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

    @Mapping(source = "name", target = "name")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "memesFound", target = "memesFound")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "maxSuggestSeconds", target = "maxSuggestSeconds")
    @Mapping(source = "maxVoteSeconds", target = "maxVoteSeconds")
    @Mapping(source = "maxAftermathSeconds", target = "maxAftermathSeconds")
    @Mapping(source = "lastModified", target = "lastModified")
    GameSettingsGetDTO convertEntityToGameSettingsGetDTO(GameSettings gameSettings);

    //get Rounds
    @Mapping(source = "title", target = "title")
    @Mapping(source = "memeURL", target = "memeURL")
    @Mapping(source = "roundPhase", target = "roundPhase")
    @Mapping(source = "suggestions", target = "suggestions")
    @Mapping(source = "votes", target = "votes")
    @Mapping(source = "scores", target = "scores")
    @Mapping(source = "lastModified", target = "lastModified")
    GameRoundDTO convertEntityToGameRoundDTO(GameRound gameRound);

    // getting restricted game information
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "maxSuggestSeconds", target = "maxSuggestSeconds")
    @Mapping(source = "maxVoteSeconds", target = "maxVoteSeconds")
    @Mapping(source = "maxAftermathSeconds", target = "maxAftermathSeconds")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "gameMaster", target = "gameMaster")
    @Mapping(source = "presentPlayers", target = "playerCount")
    GamePublicDTO convertEntityToGamePublicDTO(Game game);

    // getting restricted game information
    @Mapping(source = "gameId", target = "gameId")
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
    @Mapping(source = "lastModified", target = "lastModified")
    GamePrivateDTO convertEntityToGamePrivateDTO(Game game);


    // GAME SUMMARIES

    // game round summaries
    @Mapping(source = "title", target = "title")
    @Mapping(source = "memeURL", target = "memeURL")
    @Mapping(source = "suggestions", target = "suggestions")
    @Mapping(source = "votes", target = "votes")
    @Mapping(source = "scores", target = "scores")
    GameRoundSummaryDTO convertEntityToGameRoundSummaryDTO(GameRoundSummary gameRoundSummary);

    // game summaries
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "scores", target = "scores")
    @Mapping(source = "gameChatId", target = "gameChatId")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "rounds", target = "rounds")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    GameSummaryDTO convertEntityToGameSummaryDTO(GameSummary gameSummary);

    // CHATS

    // getting chats
    @Mapping(source = "messageChannelId", target = "messageChannelId")
    @Mapping(source = "lastModified", target = "lastModified")
    MessageChannelGetDTO convertEntityToMessageChannelGetDTO(MessageChannel chat);


    // MESSAGES

    // getting messages
    @Mapping(source = "messageId", target = "messageId")
    @Mapping(source = "messageChannel", target = "messageChannelId")
    @Mapping(source = "sender", target = "username")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "text", target = "text")
    MessageGetDTO convertEntityToMessageGetDTO(Message message);

    // posting messages
    @Mapping(target = "messageChannel", expression = "java(null)")      // taken from request header
    @Mapping(target = "timestamp", expression = "java(null)")   // generated
    @Mapping(target = "sender", expression = "java(null)")    // taken from request header
    @Mapping(source = "text", target = "text")
    Message convertMessagePostDTOtoEntity(MessagePostDTO messagePostDTO);


    //getting the UserPrivateDTO for the Client (which actually changed) (source Entity; target DTO)
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "currentGameId", target = "currentGame")
    @Mapping(source = "subscribedUsers", target = "subscribedUsers")
    @Mapping(source = "subscribedGameSummaries", target = "subscribedGameSummaries")
    @Mapping(source = "subscribedMessageChannels", target = "subscribedMessageChannels")
    @Mapping(source = "lastModified", target = "lastModified")
    UserPrivateDTO convertEntityToUserPrivateDTO(User user);


    // getting other users
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "currentGameId", target = "currentGameId")
    @Mapping(source = "friends", target = "friends")
    @Mapping(source = "outgoingFriendRequests", target = "outgoingFriendRequests")
    @Mapping(source = "incomingFriendRequests", target = "incomingFriendRequests")
    @Mapping(source = "lastModified", target = "lastModified")
    UserPublicDTO convertEntityToUserPublicDTO(User user);

}
