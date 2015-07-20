package io.peet.hubsub.handler;

import io.peet.hubsub.command.PubsubCommand;

public class CommandResponse implements Response {

    protected PubsubCommand command;

    public CommandResponse(PubsubCommand command) {
        this.command = command;
    }

    public PubsubCommand getCommand() {
        return command;
    }
}
