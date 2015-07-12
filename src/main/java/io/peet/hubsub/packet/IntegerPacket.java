package io.peet.hubsub.packet;

import akka.util.ByteString;

public class IntegerPacket extends SimpleDataPacket<Integer> {

    public IntegerPacket(int data) {
        this.data = data;
    }

    public IntegerPacket() {
    }

    @Override
    protected Integer decodeInner(String data) {
        return Integer.parseInt(data);
    }

    @Override
    protected ByteString getToken() {
        return Builder.IntegerToken;
    }
}
