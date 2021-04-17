package ch.uzh.ifi.hase.soprafs21.constant;

public enum MemeType {
    TOP, NEW, RISING, HOT, CONTROVERSIAL, RANDOM, BEST;

    public String toString() {
		switch (this) {
		case TOP:
			return "top";
		case BEST:
			return "best";
		case NEW:
		case RISING:
			return "new";
		case HOT:
			return "hot";
		case CONTROVERSIAL:
			return "controversial";
		case RANDOM:
			return "random";
		}
		return "new";
	}