package io.peet.hubsub.protocol;

import akka.util.ByteString;

/**
 * The simple data packet decodes "single line" items, such as simple string
 * packets, error packets, and integer packets.
 * @param <T>
 */
abstract public class SimpleDataPacket<T> extends AbstractPacket<T> {

    @Override
    protected ByteString encodeBase() {
        return Builder.stringToBytes(data.toString());
    }

    /**
     * Called to decode data between the packet identifier and the delimited.
     * @param data the data decoded as a string
     * @return what information the data represents.
     */
    abstract protected T decodeInner(String data);

    @Override
    public int decode(ByteString bytes) throws PacketIncompleteException {
        int end = Builder.indexOf(Builder.delimiter, bytes);
        data = decodeInner(bytes.slice(getToken().length(), end).utf8String());

        return end + Builder.delimiter.length();
    }

    @Override
    public String toString() {
        return data == null ? "<null>" : data.toString();
    }
}
