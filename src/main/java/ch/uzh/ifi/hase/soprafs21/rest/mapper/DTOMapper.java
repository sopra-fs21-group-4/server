package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.*;
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

    // getting users
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "email", target = "email")
    UserGetDTO convertEntityToUserGetDTO(User user);

    // update user profile
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);


    // GAMES

    @Mapping(source = "name", target = "name")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "subreddit", target = "memeSourceURL")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "maxSuggestSeconds", target = "maxSuggestSeconds")
    @Mapping(source = "maxVoteSeconds", target = "maxVoteSeconds")
    @Mapping(source = "maxAftermathSeconds", target = "maxAftermathSeconds")
    GameSettings convertGameSettingsDTOToEntity(GameSettingsDTO gameSettingsDTO);

    // getting restricted game information
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "memeSourceURL", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "maxSuggestSeconds", target = "maxSuggestSeconds")
    @Mapping(source = "maxVoteSeconds", target = "maxVoteSeconds")
    @Mapping(source = "maxAftermathSeconds", target = "maxAftermathSeconds")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "gameMaster", target = "gameMasterName")
    @Mapping(source = "presentPlayers", target = "playerNames")
    GameGetRestrictedDTO convertEntityToGameGetRestrictedDTO(Game game);

    // getting complete game information
    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "memeSourceURL", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "currentRoundTitle", target = "currentRoundTitle")
    @Mapping(source = "currentRoundPhase", target = "currentRoundPhase")
    @Mapping(source = "currentSuggestions", target = "currentSuggestions")
    @Mapping(source = "currentVotes", target = "currentVotes")
    @Mapping(source = "currentMemeURL", target = "currentMemeURL")
    @Mapping(source = "roundCounter", target = "roundCounter")
    @Mapping(source = "totalRounds", target = "totalRounds")
    @Mapping(source = "currentCountdown", target = "currentCountdown")
    @Mapping(source = "maxSuggestSeconds", target = "maxSuggestSeconds")
    @Mapping(source = "maxVoteSeconds", target = "maxVoteSeconds")
    @Mapping(source = "maxAftermathSeconds", target = "maxAftermathSeconds")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "playerStates", target = "playerStates")
    @Mapping(source = "playerPoints", target = "playerPoints")
    @Mapping(source = "gameMaster", target = "gameMasterName")
    @Mapping(source = "presentPlayers", target = "playerNames")
    @Mapping(source = "gameChat", target = "gameChat")
    GameGetFullDTO convertEntityToGameGetFullDTO(Game game);



    // CHATS

    // getting chats
    @Mapping(source = "messageChannelId", target = "messageChannelId")
    MessageChannelGetDTO convertEntityToMessageChannelGetDTO(MessageChannel chat);


    // MESSAGES

    // getting messages
    @Mapping(source = "messageId", target = "messageId")
    @Mapping(source = "messageChannelId", target = "messageChannelId")
    @Mapping(target = "username", expression = "java(message.getUsername())")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "text", target = "text")
    MessageGetDTO convertEntityToMessageGetDTO(Message message);

    // posting messages
    @Mapping(target = "messageChannelId", expression = "java(null)")      // taken from request header
    @Mapping(target = "timestamp", expression = "java(null)")   // generated
    @Mapping(target = "userId", expression = "java(null)")    // taken from request header
    @Mapping(source = "text", target = "text")
    Message convertMessagePostDTOtoEntity(MessagePostDTO messagePostDTO);


}
