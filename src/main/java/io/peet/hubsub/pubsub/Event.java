package io.peet.hubsub.pubsub;

import akka.util.ByteString;
import io.peet.hubsub.packet.ArrayPacket;
import io.peet.hubsub.packet.BulkStringPacket;
import io.peet.hubsub.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class Event {

    protected String pattern;
    protected ByteString payload;

    public Event(String pattern, ByteString payload) {
        this.pattern = pattern;
        this.payload = payload;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public ByteString getPayload() {
        return payload;
    }

    public void setPayload(ByteString payload) {
        this.payload = payload;
    }

    /**
     * Encodes the event to a "message" packet to send to clients.
     * @return a message packet
     */
    public Packet packet() {
        return new ArrayPacket()
                .add("message")
                .add(pattern)
                .add(payload);
    }
}
