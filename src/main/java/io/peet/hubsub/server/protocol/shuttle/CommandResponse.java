package io.peet.hubsub.server.protocol.shuttle;

import io.peet.hubsub.server.protocol.pool.PubsubCommand;

public class CommandResponse implements Response {

    protected PubsubCommand command;

    public CommandResponse(PubsubCommand command) {
        this.command = command;
    }

    public PubsubCommand getCommand() {
        return command;
    }
}
