package io.peet.hubsub.server;

import io.peet.hubsub.pubsub.Publishable;

public class SubscribeCommand extends AbstractPubsubCommand {
    public SubscribeCommand(String pattern, Publishable publishable, boolean isGlob) {
        super(pattern, publishable, isGlob);
    }
}
