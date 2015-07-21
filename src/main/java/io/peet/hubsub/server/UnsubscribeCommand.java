package io.peet.hubsub.server;

import io.peet.hubsub.pubsub.Publishable;

public class UnsubscribeCommand extends AbstractPubsubCommand {
    public UnsubscribeCommand(String pattern, Publishable publishable, boolean isGlob) {
        super(pattern, publishable, isGlob);
    }
}
