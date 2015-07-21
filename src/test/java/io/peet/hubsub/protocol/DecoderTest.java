package io.peet.hubsub.protocol;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class DecoderTest {

    @Test
    public void identification() throws PacketIncompleteException {
        assertTrue(new Decoder(":42\r\n".getBytes()).identify() == IntegerPacket.class);
        assertTrue(new Decoder("+42\r\n".getBytes()).identify() == SimpleStringPacket.class);
        assertTrue(new Decoder("$42\r\n".getBytes()).identify() == BulkStringPacket.class);
        assertTrue(new Decoder("-42\r\n".getBytes()).identify() == ErrorPacket.class);
        assertTrue(new Decoder("*-1\r\n".getBytes()).identify() == ArrayPacket.class);
    }

    @Test
    public void integerBasic() throws Exception {
        assertThat(new Decoder(":42\r\n".getBytes()).packet().getData(), is(42));
    }

    @Test
    public void simpleStringBasic() throws Exception {
        assertThat(new Decoder("+OK\r\n".getBytes()).packet().getData(), is("OK"));
    }

    @Test
    public void errorBasic() throws Exception {
        assertThat(new Decoder("-OH NO\r\n".getBytes()).packet().getData(), is("OH NO"));
    }

    @Test
    public void bulkstringBasic() throws Exception {
        BulkStringPacket packet = (BulkStringPacket) new Decoder(
                "$6\r\nfoobar\r\n".getBytes()).packet();
        assertThat(packet.getData().utf8String(), is("foobar"));
    }

    @Test
    public void arrayBasic() throws Exception {
        String data = "*4\r\n" +
                ":1\r\n" +
                ":2\r\n" +
                ":3\r\n" +
                "$6\r\n" +
                "foobar\r\n";
        List<Packet> packet = (List<Packet>) new Decoder(data.getBytes())
                .packet().getData();

        assertThat(packet.size(), is(4));
        assertThat(packet.get(0).getData(), is(1));
        assertThat(packet.get(1).getData(), is(2));
        assertThat(packet.get(2).getData(), is(3));
        assertThat(((BulkStringPacket) packet.get(3))
                .getData().utf8String(), is("foobar"));
    }

    @Test
    public void nullTypes() throws Exception {
        assertNull(new Decoder("*-1\r\n".getBytes()).packet().getData());
        assertNull(new Decoder("$-1\r\n".getBytes()).packet().getData());
    }

    @Test(expected = PacketIncompleteException.class)
    public void incompleteSimple1() throws Exception {
        new Decoder("+".getBytes()).packet();
    }

    @Test(expected = PacketIncompleteException.class)
    public void incompleteSimple2() throws Exception {
        new Decoder("+Hi\r".getBytes()).packet();
    }

    @Test(expected = PacketIncompleteException.class)
    public void incompleteBulk1() throws Exception {
        new Decoder("$6\r\n".getBytes()).packet();
    }

    @Test(expected = PacketIncompleteException.class)
    public void incompleteBulk2() throws Exception {
        new Decoder("$6\r\nfoobar".getBytes()).packet();
    }

    @Test(expected = PacketIncompleteException.class)
    public void incompleteBulk3() throws Exception {
        new Decoder("$6\r\nfoobar\r".getBytes()).packet();
    }

    @Test(expected = PacketIncompleteException.class)
    public void incompleteArray1() throws Exception {
        new Decoder("*1\r\n".getBytes()).packet();
    }

    @Test(expected = PacketIncompleteException.class)
    public void incompleteArray2() throws Exception {
        new Decoder("*1\r\n+hey".getBytes()).packet();
    }

    @Test(expected = PacketIncompleteException.class)
    public void incompleteArray3() throws Exception {
        new Decoder("*1\r\n+hey\r".getBytes()).packet();
    }
}
