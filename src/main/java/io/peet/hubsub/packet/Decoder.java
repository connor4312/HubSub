package io.peet.hubsub.packet;

import akka.util.ByteString;

/**
 * The decoder is meant to be used as a persistent "frame" for decoding
 * packets. After writing information to it, decode should be called into
 * a PacketIncompleteException is thrown, at which point the decoder should
 * be kept for when we get more info.
 */
public class Decoder {

    protected ByteString data;

    protected int offset;

    /**
     * Creates a Decoder instance with the indention of decoding the bytes
     * held in the `data`
     */
    public Decoder(ByteString data) {
        this.data = data;
    }

    public Decoder(byte[] data) {
        this.data = ByteString.fromArray(data);
    }

    public Decoder() {
        this.data = ByteString.fromArray(new byte[0]);
    }

    /**
     * Attempts to decode the packet, and returns a corresponding packet object.
     * If the packet is incomplete or malformed, an exception is raised.
     */
    public Packet packet() throws PacketIncompleteException {
        // If the packet is empty or doesn't end in a delimiter, it's obviously
        // not complete.
        if (data.size() == 0 || !data.endsWith(Builder.delimiter)) {
            throw new PacketIncompleteException();
        }

        Packet packet;
        try {
            packet = (Packet) identify().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // this should never happen.
            e.printStackTrace();
            return null;
        }

        int size = packet.decode(data);
        data = data.slice(size, data.length());
        offset += size;

        return packet;
    }

    /**
     * Returns the class corresponding to the data in the packet.
     * @return a packet class
     */
    public Class<?> identify() throws PacketIncompleteException {
        if (data.startsWith(Builder.SimpleStringToken)) {
            return SimpleStringPacket.class;
        } else if (data.startsWith(Builder.ErrorToken)) {
            return ErrorPacket.class;
        } else if (data.startsWith(Builder.ArrayToken)) {
            return ArrayPacket.class;
        } else if (data.startsWith(Builder.BulkStringToken)) {
            return BulkStringPacket.class;
        } else if (data.startsWith(Builder.IntegerToken)) {
            return IntegerPacket.class;
        }

        throw new PacketIncompleteException();
    }

    /**
     * Gets how many bytes of data have been read so far.
     * @return the number of bytes read
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Writes data to the decoder.
     * @param bytes the byte array to write
     */
    public void write(ByteString bytes) {
        data = data.concat(bytes);
    }
}
