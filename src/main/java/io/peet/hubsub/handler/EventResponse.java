package io.peet.hubsub.handler;

import io.peet.hubsub.pubsub.Event;

public class EventResponse implements Response {

    protected Event event;

    public EventResponse(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
