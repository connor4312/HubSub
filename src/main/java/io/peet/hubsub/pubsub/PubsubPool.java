package io.peet.hubsub.pubsub;

public interface PubsubPool {
    void publish(Event ev);

    void subscribe(String pattern, Publishable item);

    void psubscribe(String pattern, Publishable item);

    void unsubscribe(String pattern, Publishable item);

    void punsubscribe(String pattern, Publishable item);

    void unsubscribe(Publishable item);
}
