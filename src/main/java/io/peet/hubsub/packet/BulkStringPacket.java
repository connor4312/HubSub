package io.peet.hubsub.packet;

import akka.util.ByteString;

public class BulkStringPacket extends BulkDataPacket<ByteString> {

    public BulkStringPacket(String data) {
        setStringData(data);
    }

    public BulkStringPacket(ByteString data) {
        this.data = data;
    }

    public BulkStringPacket() {
    }

    /**
     * Sets bulk string data using a byte array.
     */
    public void setArrayData(byte[] b) {
        data = ByteString.fromArray(b);
    }

    /**
     * Sets bulk string data using a string array.
     */
    public void setStringData(String str) {
        data = Builder.stringToBytes(str);
    }

    /**
     * Sets the packet to be NULL, which is a special case in Redis.
     */
    public void setNull() {
        data = null;
    }

    @Override
    protected int getSize() {
        return data.size();
    }

    @Override
    protected ByteString getInnerData() {
        return data;
    }

    @Override
    protected ByteString getToken() {
        return Builder.BulkStringToken;
    }

    @Override
    protected int decodeInner(int length, ByteString bytes) throws PacketIncompleteException {
        if (bytes.size() < length + Builder.delimiter.length()) {
            throw new PacketIncompleteException();
        }

        data = bytes.slice(0, length);

        return length + Builder.delimiter.length();
    }

    @Override
    public String toString() {
        return data == null ? "<null>" : data.utf8String();
    }
}
