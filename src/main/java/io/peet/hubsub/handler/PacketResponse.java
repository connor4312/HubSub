package io.peet.hubsub.handler;

import io.peet.hubsub.packet.Packet;

public class PacketResponse implements Response {

    protected Packet packet;

    public PacketResponse(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}
