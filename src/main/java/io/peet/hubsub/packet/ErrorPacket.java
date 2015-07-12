package io.peet.hubsub.packet;

import akka.util.ByteString;

public class ErrorPacket extends SimpleDataPacket<String> {

    public ErrorPacket() {
    }

    public ErrorPacket(String data) {
        this.data = data;
    }

    @Override
    protected ByteString getToken() {
        return Builder.ErrorToken;
    }

    @Override
    protected String decodeInner(String data) {
        return data;
    }
}
