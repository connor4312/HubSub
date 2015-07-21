package io.peet.hubsub.protocol;

import akka.util.ByteString;

import java.util.ArrayList;
import java.util.List;

public class ArrayPacket extends BulkDataPacket<List<Packet>> {

    /**
     * Adds a new packet to the array body.
     * @param packet the packet to add
     * @return the current arraypacket
     */
    public ArrayPacket add(Packet packet) {
        if (data == null) {
            data = new ArrayList<>();
        }

        data.add(packet);
        return this;
    }

    /**
     * Adds new data to the array body as a bulk string.
     * @param str the string to add
     * @return the current arraypacket
     */
    public ArrayPacket add(String str) {
        add(new BulkStringPacket(str));
        return this;
    }

    /**
     * Adds new data to the array body as a bulk string.
     * @param bytes the bytes to add
     * @return the current arraypacket
     */
    public ArrayPacket add(ByteString bytes) {
        add(new BulkStringPacket(bytes));
        return this;
    }

    /**
     * Adds new data to the array body as an integer packet.
     * @param i the integer to add
     * @return the current arraypacket
     */
    public ArrayPacket add(int i) {
        add(new IntegerPacket(i));
        return this;
    }

    @Override
    protected int getSize() {
        return data.size();
    }

    @Override
    protected ByteString getInnerData() {
        ByteString output = ByteString.empty();
        for (Packet packet : data) {
            output = output.concat(packet.encode());
        }

        return output;
    }

    @Override
    protected int decodeInner(int length, ByteString bytes) throws PacketIncompleteException {
        Decoder decoder = new Decoder(bytes);
        data = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            try {
                data.add(decoder.packet());
            } catch (Exception e) {
                throw new PacketIncompleteException();
            }
        }

        return decoder.getOffset();
    }

    @Override
    protected ByteString getNullBytes() {
        return Builder.stringToBytes("-1\r\n");
    }

    @Override
    protected ByteString getToken() {
        return Builder.ArrayToken;
    }

    @Override
    public ByteString encode() {
        return getToken().concat(encodeBase());
    }

    /**
     * Wraps the bulk string as a command packet.
     * @return a command packet, or null if not enough info
     */
    public Command asCommand() {
        if (data == null || data.size() == 0) {
            return null;
        }

        return new Command(data);
    }
}
