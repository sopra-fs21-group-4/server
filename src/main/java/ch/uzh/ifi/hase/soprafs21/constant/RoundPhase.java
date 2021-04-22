package ch.uzh.ifi.hase.soprafs21.constant;

/**
 * the possible states a GameRound can be in.
 * A GameRound should never get back to a previous state.
 */
public enum RoundPhase {
    QUEUED, STARTING, SUGGEST, OPEN, VOTE, AFTERMATH, CLOSED;

    /*
     * The open phase isn't really used right now, but implemented just in case.
     * 'open' means, that both suggestions and votes would be accepted
     */


    /**
     * tells whether votes are accepted in this phase
     * @return true if callee either VOTE or OPEN
     */
    public boolean allowsVotes() {
        return this == OPEN || this == VOTE;
    }

    /**
     * tells whether meme title suggestions are accepted in this phase
     * @return true if callee either SUGGEST or OPEN
     */
    public boolean allowsSuggestions() {
        return this == OPEN || this == SUGGEST;
    }

    /**
     * gives back the next round phase according to this list:
     * QUEUED -> STARTING -> SUGGEST -> OPEN -> VOTE -> AFTERMATH -> CLOSED
     * @return next RoundPhase
     * @throws IllegalStateException if the current RoundPhase is CLOSED.
     */
    public RoundPhase nextPhase() {
        switch(this) {
            case QUEUED: return STARTING;
            case STARTING: return SUGGEST;
            case SUGGEST: return VOTE;
            // case OPEN: return AFTERMATH;
            case VOTE: return AFTERMATH;
            case AFTERMATH: return CLOSED;
            case CLOSED:
            default: throw new IllegalStateException("don't advance a closed round, bruuh");
        }
    }
}
