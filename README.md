

<div style="text-align:center"><img src="https://raw.githubusercontent.com/sopra-fs21-group-4/client/master/src/image/logo/doyouevenmeme.png"/></div>
[toc]
# Project-Description
In this game players submit titles for images and gifs that are collected from a specified subreddit. A normal game flow looks something like this.

1. A user creates a game and enters a subreddit.
2. With the Reddit API the game master can define what type of memes are taken. For example these are the last 25 currently best posts of subreddit r/memes: https://www.reddit.com/r/memes/top.json
3. Because Reddit gives the possibility to filter the posts for specific parameters like time or upvotes we can give the users the possibility to set those. (hot/rising/new/etc.)
4. Other players join the game
5. The game begins and a meme is shown to all users. Those users have to enter a title that they find funny and fitting
6. As soon as the time is up all players vote on which is the best title for the meme. Then the players receive points according to the votes recieved. Steps 5 and 6 get repeated until the total number of rounds are played.

# Technologies
The server is written in Java using the Spring Boot framework. JPA is used for persistence and deployment is handled by Heroku.

To establish a connection between the front- and backend REST is used.
# High-Level Components

## MessageService
The [MessageService](https://github.com/sopra-fs21-group-4/server/blob/master/src/main/java/service/MessageService.java) Class  is responsible for all functionality related to the user (e.g., it creates, modifies, deletes and finds). The resultes are then passed back to the caller.
This class allows the caller to send, recieve, delete, update and reference messages. 
Referencing is done by using @ and then certain keywords. For example "@all" references all players.


## MemeUrlSupplier
The [MemeUrlSupplier](https://github.com/sopra-fs21-group-4/server/blob/master/src/main/java/util/MemeUrlSupplier.java) Class handles collecting the memes from Reddit. 
It gets invoked by the [GameSettings](https://github.com/sopra-fs21-group-4/server/blob/master/src/main/java/entity/GameSettings.java) class where the memes then get stored to be distributed to all players.
The requests to [Reddit](https://reddit.com) gets called via a Rest request to a URL that is defined by the GameSettings.
**(https://reddit.com/"the specified subreddit"/"the predefined setting like hot/new/rising"/.json?sort="again the setting"&limit=100)**
To conform to the specification provided by reddit the request needs to send an unique User-Agent header. This needs to be done so that reddit can know and manage who sends requests. 
There are certain restrictions to what and how many requests can be sent. For example the amount of requests per minute can't be more than 100. More information can be found at this [link](https://www.reddit.com/dev/api/).

After the response is recieved form Reddit we get the JSON objects and remove every child object that does not include a link to object that we dont recognize. 
Our current implementation only accepts  files with .jpg, .png and .gif file extentsion. 
Every child object that does not include the extentsion we remove.
This array is than returned to the GameSettings class.


## Game

The [Game](https://github.com/sopra-fs21-group-4/server/blob/master/src/main/java/entity/Game.java) Class implements the entire game.
We store the Game class as an entity in our JPA Repository. This allows us to keep the Game instances as semi-persistent entities. We check whether a player is enrolled in the game before the player can do anything. 
The included special functions besides retriving the game data are:
* getEnrolledPlayers(): finds all the players that are enrolled for this game
* getPresentPlayers(): finds all the players that are enrolled for this game and still present
* getReadyPlayers(): finds all the players that are waiting for the game to start
* getGameMaster(): finds the game master of this game. note that if the game master left the game, the game will automatically try to elect a new one.
* getPlayerState(Long player): returns a player's current state in this game.
* setPlayerReady(Long player, boolean ready): marks a player as ready or not
* promotePlayer(Long player): promotes a player to game master. demotes the current game master (if any)
* enrollPlayer(User player, String password):  enrolls a player for this game does nothing if the player joined already.
* dismissPlayer(User player): removes a player from this game. ignores users that are not enrolled.
* banPlayer(User player): bans a player from this game
* forgivePlayer(Long player): makes a banned player able to join again, given that the game is still in the lobby state. does nothing if the player isn't actually banned.
* addPlayer(User player, PlayerState playerState): adds a player to the game and to the game chat. don't call this method directly! use enrollPlayer instead.
* removePlayer(User player, PlayerState playerState): removes a player from the game and from the game chat. don't call this method directly! use dismissPlayer or banPlayer instead.
* checkPlayerList(): makes sure that there's enough players and a game master. if there are not enough players to continue the game, termination is initialized. should be called after removing players in any way.
* adaptSettings(GameSettings settings): adapts all non-null settings of a GameSettings instance
* initialize(User gameMaster): sets the creator of this game as its game master (without asking for a password) and opens the lobby for this game
* closeLobby(boolean force): leaves the lobby phase and initializes the game rounds. does nothing and returns false if :
     1) the game is not in the lobby state
     2) less than 3 players are present
     3) not forced and not all players are ready
     4) not enough memes have been found
* start(): starts the game, sets the game state to running and all player states to active
* pause(): sets the game on pause
* resume(): resumes the game if it was paused
* kill(): terminates the game instantly and removes all the players.
* update(): if the game is dead, returns a summary of it, if the game is running, updates the countdown and advances if the latter ran out.
* setCountdown(long millis): sets the countdown to a given value and resets time measure.
* advance(): Will advance the current round into the next phase or issue the next round if the current one has ended. Automatically sets the countdown.
* distributePoints(): distributes points according to the votes recieved and to wether a person has voted or not
* skipRound(): Closes the current round and advances the next one. Sets the game phase to AFTERMATH if this was the last round
* putSuggestion(Long player, String suggestion): puts a suggestion of a player in the current round
* putVote(Long player, Long vote): puts a vote of a player in the current round
* interpretCommand(Message command): handles the commands that are given through the chat interface. The accepted commands are:
    * /start: starts game
    * /a: advance
    * /skip: skips round
    * /kill: kills the game
    * /pause: pauses the game
    * /resume: resumes the game
    * /ban: bans a player
* summarizePastRounds(): creates a list of game round summaries up to the current round counter


# Launch and Development
## Build

```bash
./gradlew build
```

## Run

```bash
./gradlew bootRun
```

## Test

```bash
./gradlew test
```

# Roadmap
1. extend the resources where memes could be collected for the game
2. making sure that no meme is displayed to a player twice


# Authors and Acknowledgment
The authors of this project are:
[Florian Herzog](https://github.com/Stud-FH), [Aljoscha Schnider](https://github.com/plexinio), [Max-Zurbriggen](https://github.com/Max-Zurbriggen), [Cyrill Hidber](https://github.com/Aece96), [Philipp Marock](https://github.com/Sahibabdul)

## Acknowledgemnt
We would like to thank our tutors for providing us with a starting point and continued support. 
Further we would like to thank our friends for testing our game and providing us with ideas for improvement.

# License

MIT License

Copyright (c) [2021] [SoPra Group 04]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.