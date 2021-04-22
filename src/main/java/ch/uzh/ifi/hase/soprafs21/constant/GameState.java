package ch.uzh.ifi.hase.soprafs21.constant;

public enum GameState {
    INIT,       // being initialized
    LOBBY,      // open for players to join
    STARTING,   // getting initialized. Not started yet, but lobby is closed
    RUNNING,    // the game is going on
    PAUSED,     // interrupted, meant to continue at some point
    ABORTED,    // interrupted, unable to continue
    ABANDONED,  // not finished, not enough players to continue
    AFTERMATH,  // game finished successfully, waiting for players to leave
    FINISHED;   // game finished successfully, all players left


    public boolean isVirgin() {
        switch(this) {
            case INIT:
            case LOBBY:     return true;
            default:        return false;
        }
    }

    public boolean isActive() {
        switch(this) {
            case RUNNING:
            case PAUSED:    return true;
            default:        return false;
        }
    }

    public boolean isOver() {
        switch(this) {
            case ABORTED:
            case ABANDONED:
            case AFTERMATH:
            case FINISHED:  return true;
            default:        return false;
        }
    }

    public boolean isDead() {
        switch(this) {
            case ABORTED:
            case ABANDONED:
            case FINISHED:  return true;
            default:        return false;
        }
    }

    public boolean isFinished() {
        switch(this) {
            case AFTERMATH:
            case FINISHED:  return true;
            default:        return false;
        }
    }

    public int minPlayers() {
        switch(this) {
            case LOBBY:
            case AFTERMATH:     return 1;

            case STARTING:
            case RUNNING:
            case PAUSED:        return 3;

            default:            return 0;
        }
    }

    public GameState abandoningState() {
        switch(this) {
            case LOBBY:
            case STARTING:
            case RUNNING:
            case PAUSED:        return ABANDONED;

            case AFTERMATH:     return FINISHED;

            default:            return this;
        }
    }


}
