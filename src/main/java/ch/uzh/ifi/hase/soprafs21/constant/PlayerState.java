package ch.uzh.ifi.hase.soprafs21.constant;

public enum PlayerState {
    STRANGER,           // unknown user. equivalent to null.
    VANISHED,           // left lobby
    ENROLLED,           // joined, in lobby
    GAME_MASTER,        // game master of lobby
    READY,              // joined and ready to start
    GM_READY,           // game master and ready
    ACTIVE,             // playing
    GM_ACTIVE,          // game master playing
    ABORTED,            // left in the middle of the game
    LEFT,               // left after game ended
    BANNED_FROM_LOBBY,  // banned during the lobby state
    BANNED_FROM_GAME;   // banned during the actual game

    public boolean isEnrolled() {
        switch(this) {
            case ENROLLED:
            case GAME_MASTER:
            case READY:
            case GM_READY:
            case ACTIVE:
            case GM_ACTIVE:
            case ABORTED:
            case LEFT:
            case BANNED_FROM_GAME: return true;
            default: return false;
        }
    }

    public boolean isReady() {
        switch(this) {
            case READY:
            case GM_READY: return true;
            default: return false;
        }
    }

    public boolean isPresent() {
        switch(this) {
            case ENROLLED:
            case GAME_MASTER:
            case READY:
            case GM_READY:
            case ACTIVE:
            case GM_ACTIVE: return true;
            default: return false;
        }
    }

    public boolean isPromoted() {
        switch(this) {
            case GAME_MASTER:
            case GM_READY:
            case GM_ACTIVE: return true;
            default: return false;
        }
    }

    public boolean isBanned() {
        switch(this) {
            case BANNED_FROM_LOBBY:
            case BANNED_FROM_GAME:  return true;
            default: return false;
        }
    }

    public PlayerState readyState(boolean ready) {
        switch(this) {
            case READY:
            case ENROLLED:      return ready? READY : ENROLLED;
            case GM_READY:
            case GAME_MASTER:   return ready? GM_READY : GAME_MASTER;
            default: return this;
        }
    }

    public PlayerState activeState() {
        switch(this) {
            case ENROLLED:
            case READY:         return ACTIVE;
            case GAME_MASTER:
            case GM_READY:      return GM_ACTIVE;
            default: return this;
        }
    }

    public PlayerState promotedState(boolean promoted) {
        switch(this) {
            case GAME_MASTER:
            case ENROLLED:  return promoted? GAME_MASTER : ENROLLED;
            case GM_READY:
            case READY:     return promoted? GM_READY : READY;
            case GM_ACTIVE:
            case ACTIVE:    return promoted? GM_ACTIVE : ACTIVE;
            default: return this;
        }
    }


}
