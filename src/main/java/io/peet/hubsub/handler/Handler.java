package io.peet.hubsub.handler;

import io.peet.hubsub.packet.Command;

public interface Handler {
    Response[] handle(Command cmd) throws UnhandledCommandException;
}
