package io.peet.hubsub.server;

import io.peet.hubsub.pubsub.Publishable;

public interface PubsubCommand {
    String getPattern();

    Publishable getPublishable();

    boolean isGlob();
}
