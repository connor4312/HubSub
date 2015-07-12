package io.peet.hubsub.packet;

import akka.util.ByteString;

/**
 * A Reply defines an object that can have their data set, and be serialized
 * into a byte array.
 */
public interface Packet<T> {
    /**
     * Sets the data contained in the packet.
     */
    public void setData(T data);

    /**
     * Gets the data contained in the packet.
     */
    public T getData();

    /**
     * Decodes the packet from a list of bytes.
     * @param bytes the source bytes
     * @throws PacketIncompleteException
     * @return the number of bytes read
     */
    public int decode(ByteString bytes) throws PacketIncompleteException;

    /**
     * Serializes the the reply into a byte string suitable to be sent
     * to a Redis-enabled recipient.
     */
    public ByteString encode();
}
