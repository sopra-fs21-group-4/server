package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.GameSettings;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
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

    // getting other users
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "currentGameId", target = "currentGameId")
    UserGetLimitedDTO convertEntityToUserGetLimitedDTO(User user);

    // getting own user
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "currentGameId", target = "currentGameId")
    @Mapping(source = "inbox", target = "inbox")
    @Mapping(source = "friends", target = "friends")
    @Mapping(source = "outgoingFriendRequests", target = "outgoingFriendRequests")
    @Mapping(source = "incomingFriendRequests", target = "incomingFriendRequests")
    UserGetCompleteDTO convertEntityToUserGetCompleteDTO(User user);

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
    GameSettings convertGameSettingsDTOToEntity(GameSettingsDTO gameSettingsDTO);

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
    GameGetLimitedDTO convertEntityToGameGetRestrictedDTO(Game game);

    // getting complete game information
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "memesFound", target = "memesFound")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "currentRoundTitle", target = "currentRoundTitle")
    @Mapping(source = "currentRoundPhase", target = "currentRoundPhase")
    @Mapping(source = "currentSuggestions", target = "currentSuggestions")
    @Mapping(source = "currentVotes", target = "currentVotes")
    @Mapping(source = "currentScores", target = "currentScores")
    @Mapping(source = "currentMemeURL", target = "currentMemeURL")
    @Mapping(source = "roundCounter", target = "roundCounter")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "currentCountdown", target = "currentCountdown")
    @Mapping(source = "maxSuggestSeconds", target = "maxSuggestSeconds")
    @Mapping(source = "maxVoteSeconds", target = "maxVoteSeconds")
    @Mapping(source = "maxAftermathSeconds", target = "maxAftermathSeconds")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "scores", target = "scores")
    @Mapping(source = "gameMaster", target = "gameMaster")
    @Mapping(source = "presentPlayers", target = "players")
    @Mapping(source = "playerStates", target = "playerStates")
    @Mapping(source = "gameChat", target = "gameChat")
    GameGetCompleteDTO convertEntityToGameGetCompleteDTO(Game game);


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


    // getting own user (source Entity; target DTO)
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "currentGameId", target = "currentGameId")
    @Mapping(source = "inbox", target = "inbox")
    @Mapping(source = "friends", target = "friends")
    @Mapping(source = "outgoingFriendRequests", target = "outgoingFriendRequests")
    @Mapping(source = "incomingFriendRequests", target = "incomingFriendRequests")
    UpdatePrivateUserDTO convertEntityToUpdatePrivateUserDTO(User user);
}
