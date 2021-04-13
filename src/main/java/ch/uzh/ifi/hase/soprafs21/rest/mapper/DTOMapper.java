package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.*;
import ch.uzh.ifi.hase.soprafs21.helpers.SpringContext;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
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
    UserService userService = SpringContext.getBean(UserService.class);     // hacky steal the userService

    // USERS

    // user login
    @Mapping(source = "token", target = "token")
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
    UserGetDTO convertEntityToUserGetDTO(User user);


    // LOBBIES

    // getting lobbies
    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "gameState", target = "gameState")
    @Mapping(source = "round", target = "round")
    @Mapping(source = "maxRounds", target = "maxRounds")
    @Mapping(source = "maxTimer", target = "maxTimer")
    @Mapping(source = "maxPlayers", target = "maxPlayers")
    @Mapping(source = "players", target = "players")
    LobbyGetDTO convertEntityToLobbyGetDTO(Lobby lobby);

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
    @Mapping(target = "senderName", expression = "java(getUsername(message.getSenderId()))")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "text", target = "text")
    MessageGetDTO convertEntityToMessageGetDTO(Message message);

    // posting messages
    @Mapping(target = "chatId", expression = "java(null)")      // taken from request header
    @Mapping(target = "timestamp", expression = "java(null)")   // generated
    @Mapping(source = "senderId", target = "senderId")
    @Mapping(source = "text", target = "text")
    Message convertMessagePostDTOtoEntity(MessagePostDTO messagePostDTO);

    /**
     * gets a user's username by its userId.
     * @param userId
     * @return the user's username if existent, null otherwise.
     */
    default String getUsername(Long userId) {
        System.out.println("test");
        User user = userService.getUserByUserId(userId);
        return (user == null)? null : user.getUsername();
    }
}
