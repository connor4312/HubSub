package io.peet.hubsub.packet;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import akka.util.ByteString;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PacketsTest {

    @Test
    public void simpleString() throws Exception {
        SimpleStringPacket p = new SimpleStringPacket();
        p.setData("OK");
        assertThat(p.encode().utf8String(), is("+OK\r\n"));
    }

    @Test
    public void simpleStringToString() throws Exception {
        SimpleStringPacket p = new SimpleStringPacket();
        p.setData("OK");
        assertThat(p.toString(), is("OK"));
    }

    @Test
    public void error() throws Exception {
        ErrorPacket p = new ErrorPacket();
        p.setData("ERR unknown command 'foobar'");
        assertThat(p.encode().utf8String(),
                is("-ERR unknown command 'foobar'\r\n"));
    }

    @Test
    public void integer() throws Exception {
        IntegerPacket p = new IntegerPacket();
        p.setData(42);
        assertThat(p.encode().utf8String(), is(":42\r\n"));
    }

    @Test
    public void integerToString() throws Exception {
        IntegerPacket p = new IntegerPacket();
        p.setData(42);
        assertThat(p.toString(), is("42"));
    }

    @Test
    public void bulkstringDefault() throws Exception {
        BulkStringPacket p = new BulkStringPacket();
        p.setStringData("foobar");
        assertThat(p.encode().utf8String(), is("$6\r\nfoobar\r\n"));
    }

    @Test
    public void bulkstringEmpty() throws Exception {
        BulkStringPacket p = new BulkStringPacket();
        p.setArrayData(new byte[0]);
        assertThat(p.encode().utf8String(), is("$0\r\n\r\n"));
    }

    @Test
    public void bulkstringNull() throws Exception {
        BulkStringPacket p = new BulkStringPacket();
        p.setNull();
        assertThat(p.encode().utf8String(), is("$-1\r\n"));
    }

    @Test
    public void bulkstringToString() throws Exception {
        BulkStringPacket p = new BulkStringPacket("foobar");
        assertThat(p.toString(), is("foobar"));
        assertThat(new BulkStringPacket().toString(), is("<null>"));
    }

    @Test
    public void arrayNull() throws Exception {
        ArrayPacket p = new ArrayPacket();
        assertThat(p.encode().utf8String(), is("*-1\r\n"));
    }

    @Test
    public void arrayBlank() throws Exception {
        ArrayPacket p = new ArrayPacket();
        p.setData(new ArrayList<>());
        assertThat(p.encode().utf8String(), is("*0\r\n"));
    }

    private ArrayPacket makeFullArrayPacket() {
        ArrayPacket p = new ArrayPacket();

        List<Packet> packets = new ArrayList<>();
        BulkStringPacket p1 = new BulkStringPacket();
        p1.setArrayData("foo".getBytes());
        packets.add(p1);
        BulkStringPacket p2 = new BulkStringPacket();
        p2.setArrayData("bar".getBytes());
        packets.add(p2);

        p.setData(packets);
        return p;
    }

    @Test
    public void arrayBuilders() throws Exception {
        assertThat(new ArrayPacket().add(1).encode().utf8String(),
                is("*1\r\n:1\r\n"));
        assertThat(new ArrayPacket().add("asdf").encode().utf8String(),
                is("*1\r\n$4\r\nasdf\r\n"));
        assertThat(new ArrayPacket().add(ByteString.fromString("asdf"))
                        .encode().utf8String(), is("*1\r\n$4\r\nasdf\r\n"));
        assertThat(new ArrayPacket().add(new SimpleStringPacket("asdf"))
                        .encode().utf8String(), is("*1\r\n+asdf\r\n"));
    }

    @Test
    public void arrayOfStrings() throws Exception {
        ArrayPacket p = makeFullArrayPacket();
        assertThat(p.encode().utf8String(), is("*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n"));
    }

    @Test
    public void arrayAsValidCommand() throws Exception {
        Command p = makeFullArrayPacket().asCommand();
        assertThat(p.name(), is("foo"));
        assertThat(p.args(), is(1));
        assertThat(p.arg(0).toString(), is("bar"));
    }

    @Test
    public void arrayAsInvalidCommand() throws Exception {
        assertNull(new ArrayPacket().asCommand());
    }
}
