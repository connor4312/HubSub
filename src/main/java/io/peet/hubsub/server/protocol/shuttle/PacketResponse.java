package io.peet.hubsub.server.protocol.shuttle;

import io.peet.hubsub.protocol.Packet;

public class PacketResponse implements Response {

    protected Packet packet;

    public PacketResponse(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}
