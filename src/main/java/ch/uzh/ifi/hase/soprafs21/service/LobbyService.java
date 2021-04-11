package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.MemeTitle;
import ch.uzh.ifi.hase.soprafs21.entity.MemeVote;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lobby Service
 * responsible for all actions relating the lobbies
 */
@Service
@Transactional
public class LobbyService {

    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;
    private final UserService userService;


    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository, UserService userService) {
        this.lobbyRepository = lobbyRepository;
        this.userService = userService;
    }

    /**
     * Loop running every 500ms to update lobby states
     */
    @Scheduled(fixedRate=500)
    public void updateLobbies(){
        List<Lobby> lobbies = lobbyRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        //TODO changing lobby states etc
        for(Lobby lobby : lobbies){
            if(lobby.getGameState() == GameState.TITLE){
                if(now.isAfter(lobby.getTime())){
                    // title giving stage is over:
                    lobby.setGameState(GameState.VOTE);
                    lobby.setTime(LocalDateTime.now().plusSeconds(lobby.getMaxTimer()));
                    // TODO

                }
            }
            else if(lobby.getGameState() == GameState.VOTE){
                if(now.isAfter(lobby.getTime())){
                    // voting stage is over:
                    lobby.setGameState(GameState.POINTS);
                    lobby.setTime(LocalDateTime.now().plusSeconds(lobby.getMaxTimer()));
                    // TODO

                }
            }
            else if(lobby.getGameState() == GameState.POINTS){
                if(now.isAfter(lobby.getTime())){
                    // point giving stage is over:
                    lobby.setRound(lobby.getRound()+1);
                    lobby.setTime(LocalDateTime.now().plusSeconds(lobby.getMaxTimer()));

                    if(lobby.getRound()>lobby.getMaxRounds()){
                        // this was the last round:
                        lobby.setGameState(GameState.FINISH);
                        // TODO

                    }
                    else{
                        // there is another round to play:
                        lobby.setGameState(GameState.VOTE);
                        // TODO get meme and send it to the players
                        // reddit api
//                        getMemeLink()

                    }

                }
            }
            else if(lobby.getGameState() == GameState.FINISH){
                //TODO delete old lobbies
                //lobbyRepository.delete(lobby);
            }
        }

    }


    public List<Lobby> getLobbies() {
        return this.lobbyRepository.findAll();
    }
    public Lobby getLobbyByLobbyId(long id){
        return this.lobbyRepository.findByLobbyId(id);
    }


    public Lobby createLobby(Long userId, String token) {

        userService.verifyUser(userId, token);

        Lobby newLobby = new Lobby();
        newLobby.setGameMaster(userService.getUserByUserId(userId));

        newLobby = lobbyRepository.save(newLobby);
        lobbyRepository.flush();

        log.debug("Created new Lobby: {}", newLobby);
        return newLobby;
    }

    //TODO
    public Lobby joinLobby(Long lobbyId, Long userId, String token){
        userService.verifyUser(userId, token);
        Lobby lobby = getLobbyByLobbyId(lobbyId);

        if(lobby == null /* TODO || user put in the wrong password */){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("Access denied"));
        }

        User user = userService.getUserByUserId(userId);
        user.setCurrentLobby(lobby);

        return lobby;
    }

    public void startGame(Long lobbyId, Long userId, String token){

        userService.verifyUser(userId, token);
        Lobby lobby = getLobbyByLobbyId(lobbyId);

//        System.out.println(userId);
//        System.out.println(lobby.getGameMaster().getUserId());

        // check if the user is the game master
        if(lobby == null || !userId.equals(lobby.getGameMaster().getUserId())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("You are not the game master"));
        }
        if(lobby.getGameState() != GameState.LOBBY){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game is already running");
        }

        // TODO start game
        lobby.setRound(1);
        lobby.setGameState(GameState.TITLE);
        lobby.setTime(LocalDateTime.now().plusSeconds(lobby.getMaxTimer()));

        // TODO get meme from reddit
        //getMemeLink
    }



    public String getMemeLink(String subreddit){
        // TODO go get meme from reddit
        return null;
    }


    public void verifyUserIsInLobby(Long userId, Long lobbyId){
        if(!getLobbyByLobbyId(lobbyId).getPlayers().contains(userService.getUserByUserId(userId))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not part of this lobby");
        }
    }

    public void newTitle(MemeTitle memeTitle, long userId, String token ){
        userService.verifyUser(userId, token);
        verifyUserIsInLobby(userId, memeTitle.getLobbyId());
        //TODO save new title in lobby
    }

    public void newVote(MemeVote memeVote, long userId, String token){
        userService.verifyUser(userId, token);
        verifyUserIsInLobby(userId, memeVote.getLobbyId());
        // TODO save new vote in lobby
    }

}
