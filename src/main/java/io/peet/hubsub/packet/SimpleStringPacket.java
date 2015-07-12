package io.peet.hubsub.packet;

import akka.util.ByteString;

public class SimpleStringPacket extends SimpleDataPacket<String> {

    public SimpleStringPacket() {
    }

    public SimpleStringPacket(String data) {
        this.data = data;
    }

    @Override
    protected String decodeInner(String data) {
        return data;
    }

    @Override
    protected ByteString getToken() {
        return Builder.SimpleStringToken;
    }
}
