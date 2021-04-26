package ch.uzh.ifi.hase.soprafs21.nonpersistent;

import ch.uzh.ifi.hase.soprafs21.entity.Message;

public abstract class MessageChannelAdapter implements MessageChannelListener {

    public void messagePosted(Message message) {
        // to be overridden by subclasses
    }

    public void messageModified(Message message, String originalText) {
        // to be overridden by subclasses
    }

    public void messageDeleted(Message message) {
        // to be overridden by subclasses
    }

}
