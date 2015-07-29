package io.peet.hubsub.server.protocol.pool;

import io.peet.hubsub.pubsub.Publishable;

public interface PubsubCommand {
    String getPattern();

    Publishable getPublishable();

    boolean isGlob();
}
