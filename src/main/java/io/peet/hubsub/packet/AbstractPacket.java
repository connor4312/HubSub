package io.peet.hubsub.packet;

import akka.util.ByteString;

abstract public class AbstractPacket<T> implements Packet<T> {

    /**
     * Data contained in the packet.
     */
    protected T data;

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Encodes the packet's inner data, excluding its token and ending
     * delimiter.
     */
    abstract protected ByteString encodeBase();

    /**
     * Returns the token used for identifying this packet type.
     */
    abstract protected ByteString getToken();

    @Override
    public ByteString encode() {
        return getToken()
                .concat(encodeBase())
                .concat(Builder.delimiter);
    }
}
