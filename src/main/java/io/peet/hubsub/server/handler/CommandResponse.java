package io.peet.hubsub.server.handler;

import io.peet.hubsub.server.PubsubCommand;

public class CommandResponse implements Response {

    protected PubsubCommand command;

    public CommandResponse(PubsubCommand command) {
        this.command = command;
    }

    public PubsubCommand getCommand() {
        return command;
    }
}
