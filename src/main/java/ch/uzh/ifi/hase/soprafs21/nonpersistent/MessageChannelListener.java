package ch.uzh.ifi.hase.soprafs21.nonpersistent;

import ch.uzh.ifi.hase.soprafs21.entity.Message;

public interface MessageChannelListener {

    public void messagePosted(Message message);
    public void messageModified(Message message, String originalText);
    public void messageDeleted(Message message);

}
