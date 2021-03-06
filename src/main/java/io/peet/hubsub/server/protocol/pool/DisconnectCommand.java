package io.peet.hubsub.server.protocol.pool;

import io.peet.hubsub.pubsub.Publishable;

public class DisconnectCommand {

    protected Publishable publishable;

    public DisconnectCommand(Publishable publishable) {
        this.publishable = publishable;
    }

    public Publishable getPublishable() {
        return publishable;
    }
}
