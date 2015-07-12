package io.peet.hubsub.command;

import io.peet.hubsub.pubsub.Publishable;

public interface PubsubCommand {
    String getPattern();

    Publishable getPublishable();

    boolean isGlob();
}
