package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.constant.GameState;
import ch.uzh.ifi.hase.soprafs21.constant.MemeType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;



    @Id
    @GeneratedValue
    private Long lobbyId;

    @Column(nullable = false)
    private String name = "newLobby";

    @Column()
    private String password;

    @Column(nullable = false)
    private GameState gameState = GameState.LOBBY;

    @Column(nullable = false)
    private int round = 0;

    @Column(nullable = false)
    private int maxRounds = 5;

    @Column
    private String currentMeme;

    @Column
    private String subreddit;

    @Column
    private List<String> memeArray;

    @Column
    private MemeType memeType = MemeType.HOT;

    @Column(nullable = false)
    private int maxTitleTime = 15;

    @Column (nullable = false)
    private int maxVoteTime = 15;

    @Column(nullable = false)
    private int maxPointsTime = 10;

    @Column(nullable = false)
    private int maxPlayers = 5;

    @Column(nullable = false)
    private LocalDateTime time = LocalDateTime.now();

    // @Column // TODO (nullable = false)
    @OneToOne(targetEntity = User.class)
    private User gameMaster;

    @Column
    @OneToMany(targetEntity = User.class, mappedBy = "currentLobby")
    private List<User> players = new ArrayList();

    @Column
    @OneToMany(targetEntity = MemeTitle.class, cascade = CascadeType.ALL, mappedBy = "lobbyId")
    private List<MemeTitle> memeTitles;

    @Column
    @OneToMany(targetEntity = MemeVote.class, cascade = CascadeType.ALL, mappedBy = "lobbyId")
    private List<MemeVote> memeVotes;

    @OneToOne(targetEntity = Chat.class, cascade = CascadeType.ALL)
    private Chat chat;


    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getCurrentMeme() {
        return currentMeme;
    }

    public void setCurrentMeme(String currentMeme) {
        this.currentMeme = currentMeme;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public MemeType getMemeType() {
        return memeType;
    }

    public void setMemeType(MemeType memeType) {
        this.memeType = memeType;
    }

    public int getMaxTitleTime() {
        return maxTitleTime;
    }

    public void setMaxTitleTime(int maxTitleTime) {
        this.maxTitleTime = maxTitleTime;
    }

    public int getMaxVoteTime() {
        return maxVoteTime;
    }

    public void setMaxVoteTime(int maxVoteTime) {
        this.maxVoteTime = maxVoteTime;
    }

    public int getMaxPointsTime() {
        return maxPointsTime;
    }

    public void setMaxPointsTime(int maxPointsTime) {
        this.maxPointsTime = maxPointsTime;
    }

    public List<MemeTitle> getMemeTitles() {
        return memeTitles;
    }

    public void setMemeTitles(List<MemeTitle> memeTitles) {
        this.memeTitles = memeTitles;
    }

    public List<MemeVote> getMemeVotes() {
        return memeVotes;
    }

    public void setMemeVotes(List<MemeVote> memeVotes) {
        this.memeVotes = memeVotes;
    }

    public User getGameMaster() {
        return gameMaster;
    }

    public void setGameMaster(User gameMaster) {
        this.gameMaster = gameMaster;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long id) {
        this.lobbyId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }


}
