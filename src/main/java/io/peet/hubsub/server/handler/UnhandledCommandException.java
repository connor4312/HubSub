package io.peet.hubsub.server.handler;

import io.peet.hubsub.protocol.ErrorPacket;

public class UnhandledCommandException extends Exception {

    protected ErrorPacket error;

    public UnhandledCommandException(String message) {
        super(message);
        error = new ErrorPacket(message);
    }

    public ErrorPacket getError() {
        return error;
    }
}
