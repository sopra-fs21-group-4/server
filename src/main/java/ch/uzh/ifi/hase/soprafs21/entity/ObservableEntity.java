package ch.uzh.ifi.hase.soprafs21.entity;

public interface ObservableEntity {
    long getId();
    long getLastModified();
    long filter(long lastUpdate);
}
