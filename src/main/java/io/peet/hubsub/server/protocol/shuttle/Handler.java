package io.peet.hubsub.server.protocol.shuttle;

import io.peet.hubsub.protocol.Command;

public interface Handler {
    Response[] handle(Command cmd) throws UnhandledCommandException;
}
