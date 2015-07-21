package io.peet.hubsub.server;

import io.peet.hubsub.pubsub.Publishable;

public class AbstractPubsubCommand implements PubsubCommand {

    protected String pattern;
    protected Publishable publishable;
    protected boolean isGlob;

    public AbstractPubsubCommand(String pattern, Publishable publishable, boolean isGlob) {
        this.pattern = pattern;
        this.publishable = publishable;
        this.isGlob = isGlob;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public Publishable getPublishable() {
        return publishable;
    }

    @Override
    public boolean isGlob() {
        return isGlob;
    }
}
