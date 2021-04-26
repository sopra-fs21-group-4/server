package ch.uzh.ifi.hase.soprafs21.nonpersistent;

import ch.uzh.ifi.hase.soprafs21.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.Message;

public class GameChatCommandInterpreter extends MessageChannelAdapter {

    // TODO make persistent version

    private final Game game;

    public GameChatCommandInterpreter(Game game) {
        this.game = game;
    }

    @Override
    public void messagePosted(Message message) {
        String text = message.getText();
        PlayerState initiatorState = game.getPlayerState(message.getSender());
        if (text.startsWith("/")) {
            interpretCommand(text.split(" "), initiatorState);
        }
    }

    private void interpretCommand(String[] commandSegment, PlayerState initiatorState) {

        switch(commandSegment[0]) {
            case "/start": tryStart(initiatorState);
            default: throw new IllegalArgumentException("unknown command");
        }
    }

    private void tryStart(PlayerState initiatorState) {
        System.out.println("TRYING TO START");
        if (!initiatorState.isPromoted()) throw new SecurityException("unauthorized command");
        game.closeLobby(true);

    }
}
