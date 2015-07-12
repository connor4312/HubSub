package io.peet.hubsub.packet;

import akka.util.ByteString;

/**
 * The bulk data packet encodes packets which are formed by their identifier,
 * a size delimiter, then body data. This is for bulk string and array type
 * packets.
 * @param <T>
 */
abstract public class BulkDataPacket<T> extends AbstractPacket<T> {

    /**
     * Returns the packet "size".
     * @return the size to add in the packet
     */
    abstract protected int getSize();

    /**
     * Returns the "inner" packet data.
     */
    abstract protected ByteString getInnerData();

    /**
     * Decodes the inner data (length from the bytes) and returns the
     * total number of bytes decoded.
     * @param length the packet length
     * @param bytes packet data
     * @return number of bytes consumed
     */
    abstract protected int decodeInner(int length, ByteString bytes) throws PacketIncompleteException;

    protected ByteString getNullBytes() {
        return Builder.stringToBytes("-1");
    }

    @Override
    protected ByteString encodeBase() {
        // Null is a special case, with a set response.
        if (data == null) {
            return getNullBytes();
        }

        return Builder.stringToBytes(Integer.toString(getSize()))
                .concat(Builder.delimiter)
                .concat(getInnerData());
    }

    @Override
    public int decode(ByteString bytes) throws PacketIncompleteException {
        int lengthEnd = Builder.indexOf(Builder.delimiter, bytes);
        int length = Integer.parseInt(bytes.slice(
                Builder.BulkStringToken.length(),
                lengthEnd).utf8String());
        int dataStart = lengthEnd + Builder.delimiter.length();

        if (length == -1) {
            data = null;
            return lengthEnd;
        }

        return dataStart + decodeInner(length,
                bytes.slice(dataStart, bytes.size()));
    }
}
