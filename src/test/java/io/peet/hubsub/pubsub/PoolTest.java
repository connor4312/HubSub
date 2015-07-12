package io.peet.hubsub.pubsub;

import akka.util.ByteString;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class PoolTest {

    @Test
    public void testBasic() throws Exception {
        Pool p = new Pool();

        Publishable pub1 = mock(Publishable.class);
        Publishable pub2 = mock(Publishable.class);
        p.subscribe("asdf", pub1);

        Event ev1 = new Event("asdf", ByteString.fromString("hello"));
        Event ev2 = new Event("wasd", ByteString.fromString("hello"));
        Event ev3 = new Event("asdf", ByteString.fromString("bye"));
        p.publish(ev1);
        p.publish(ev2);
        p.subscribe("asdf", pub2);
        p.publish(ev3);

        verify(pub1).publish(ev1);
        verify(pub1, never()).publish(ev2);
        verify(pub1).publish(ev3);
        verify(pub2).publish(ev3);
    }

    @Test
    public void testPatterns() throws Exception {
        Pool p = new Pool();

        Publishable pub1 = mock(Publishable.class);
        Publishable pub2 = mock(Publishable.class);

        p.psubscribe("h?llo", pub1);
        p.psubscribe("h*llo", pub2);

        Event ev1 = new Event("hello", ByteString.fromString("hello"));
        p.publish(ev1);
        verify(pub1).publish(ev1);
        verify(pub2).publish(ev1);

        Event ev2 = new Event("heeello", ByteString.fromString("hello"));
        p.publish(ev2);
        verify(pub1, never()).publish(ev2);
        verify(pub2).publish(ev2);
    }

    @Test
    public void testMixes() throws Exception {
        Pool p = new Pool();

        Publishable pub1 = mock(Publishable.class);
        p.psubscribe("h[e]llo", pub1);
        Publishable pub2 = mock(Publishable.class);
        p.subscribe("h[e]llo", pub2);

        Event ev1 = new Event("h[e]llo", ByteString.fromString("hello"));
        p.publish(ev1);
        verify(pub1, never()).publish(ev1);
        verify(pub2).publish(ev1);

        Event ev2 = new Event("hello", ByteString.fromString("hello"));
        p.publish(ev2);
        verify(pub1).publish(ev2);
        verify(pub2, never()).publish(ev2);
    }

    @Test
    public void testUnsubscribes() throws Exception {
        Pool p = new Pool();

        Publishable pub1 = mock(Publishable.class);
        p.subscribe("a", pub1);
        p.subscribe("b", pub1);

        Event ev1 = new Event("a", ByteString.fromString("hello"));
        p.publish(ev1);
        verify(pub1).publish(ev1);

        p.unsubscribe("a", pub1);

        Event ev2 = new Event("a", ByteString.fromString("hello"));
        p.publish(ev2);
        verify(pub1, never()).publish(ev2);
        Event ev3 = new Event("b", ByteString.fromString("hello"));
        p.publish(ev3);
        verify(pub1).publish(ev3);

        p.unsubscribe(pub1);

        Event ev4 = new Event("b", ByteString.fromString("hello"));
        p.publish(ev4);
        verify(pub1, never()).publish(ev4);
    }

    @Test
    public void testAddsToExisting() throws Exception {
        Pool p = new Pool();

        Publishable pub1 = mock(Publishable.class);
        Publishable pub2 = mock(Publishable.class);
        p.subscribe("a", pub1);
        p.subscribe("a", pub2);

        Event ev1 = new Event("a", ByteString.fromString("hello"));
        p.publish(ev1);
        verify(pub1).publish(ev1);
        verify(pub2).publish(ev1);

        p.unsubscribe(pub1);
        
        Event ev2 = new Event("a", ByteString.fromString("hello"));
        p.publish(ev2);
        verify(pub1, never()).publish(ev2);
        verify(pub2).publish(ev2);
    }

    @Test
    public void testEventEncodes() {
        Event ev1 = new Event("a", ByteString.fromString("hello"));

        assertThat(ev1.packet().encode().utf8String(), is("*3\r\n" +
                "$7\r\nmessage\r\n" +
                "$1\r\na\r\n" +
                "$5\r\nhello\r\n"));
    }
}
