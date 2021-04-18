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


    // LOBBIES

    // crating lobbies
    @Mapping(source = "name", target = "name")
    @Mapping(source = "maxRounds", target = "maxRounds")
    @Mapping(source = "maxTimer", target = "maxTitleTime")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    Lobby convertLobbyPostDTOToEntity(LobbyPostDTO lobbyPostDTO);

    // getting lobbies
    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "round", target = "round")
    @Mapping(source = "maxRounds", target = "maxRounds")
    @Mapping(source = "currentMeme", target = "currentMeme")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "maxTitleTime", target = "maxTitleTime")
    @Mapping(source = "maxVoteTime", target = "maxVoteTime")
    @Mapping(source = "maxPointsTime", target = "maxPointsTime")
    @Mapping(source = "gameMaster", target = "gameMaster")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "players", target = "players")
    @Mapping(source = "chat", target = "chat")
    LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

    // getting lobby overviews
    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "maxRounds", target = "maxRounds")
    @Mapping(source = "subreddit", target = "subreddit")
    @Mapping(source = "memeType", target = "memeType")
    @Mapping(source = "maxTitleTime", target = "maxTitleTime")
    @Mapping(source = "maxVoteTime", target = "maxVoteTime")
    @Mapping(source = "maxPointsTime", target = "maxPointsTime")
    @Mapping(source = "gameMaster", target = "gameMaster")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "players", target = "players")
    LobbyGetDTORestricted convertEntityToLobbyGetDTORestricted(Lobby lobby);

    // creating a new meme title entity
    @Mapping(target = "lobbyId", expression = "java(null)")  // lobbyId is taken from request header
    @Mapping(target = "userId", expression = "java(null)")  // userid is taken from request header
    @Mapping(source = "title", target = "title")
    @Mapping(source = "round", target = "round")
    MemeTitle convertLobbyMemeTitlePutDTOToEntity(LobbyMemeTitlePutDTO lobbyMemeTitlePutDTO);

    // creating a new meme vote entity
    @Mapping(target = "lobbyId", expression = "java(null)") // lobbyId is taken from request header
    @Mapping(target = "fromUserId", expression = "java(null)")  // userid is taken from request header
    @Mapping(source = "forUserId", target = "forUserId") // this is the id of the user for whose meme the vote is
    @Mapping(source = "round", target = "round")
    MemeVote convertLobbyMemeVotePutDTOToEntity(LobbyMemeVotePutDTO lobbyMemeVotePutDTO);

    // CHATS

    // getting chats
    @Mapping(source = "chatId", target = "chatId")
    ChatGetDTO convertEntityToChatGetDTO(Chat chat);


    // MESSAGES

    // getting messages
    @Mapping(source = "messageId", target = "messageId")
    @Mapping(source = "chatId", target = "chatId")
    @Mapping(target = "username", expression = "java(message.getUsername())")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "text", target = "text")
    MessageGetDTO convertEntityToMessageGetDTO(Message message);

    // posting messages
    @Mapping(target = "chatId", expression = "java(null)")      // taken from request header
    @Mapping(target = "timestamp", expression = "java(null)")   // generated
    @Mapping(target = "userId", expression = "java(null)")    // taken from request header
    @Mapping(source = "text", target = "text")
    Message convertMessagePostDTOtoEntity(MessagePostDTO messagePostDTO);


}
