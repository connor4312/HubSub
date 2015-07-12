package io.peet.hubsub.pubsub;

public interface Publishable {

    /**
     * Publishes an event to the listening "object"
     * @param ev the event to publish
     */
    public void publish(Event ev);
}
