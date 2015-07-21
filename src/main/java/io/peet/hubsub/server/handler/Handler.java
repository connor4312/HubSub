package io.peet.hubsub.server.handler;

import io.peet.hubsub.protocol.Command;

public interface Handler {
    Response[] handle(Command cmd) throws UnhandledCommandException;
}
