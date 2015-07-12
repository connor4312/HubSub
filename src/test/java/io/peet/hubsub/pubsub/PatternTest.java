package io.peet.hubsub.pubsub;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PatternTest {

    @Test
    public void matchesBasicString() {
        assertTrue(new Pattern("hello").matches("hello"));
        assertFalse(new Pattern("helloo").matches("hello"));
        assertFalse(new Pattern("hello").matches("helloo"));
    }

    @Test
    public void matchesQuestionMark() {
        assertTrue(new Pattern("h?llo").matches("hello"));
        assertTrue(new Pattern("h?llo").matches("hallo"));
        assertTrue(new Pattern("h?llo").matches("hxllo"));

        assertFalse(new Pattern("h?").matches("h"));
        assertTrue(new Pattern("h?").matches("ho"));
    }

    @Test
    public void matchesStar() {
        assertTrue(new Pattern("h*").matches("hey"));
        assertTrue(new Pattern("h**").matches("hey"));
        assertTrue(new Pattern("h*llo").matches("hllo"));
        assertTrue(new Pattern("h*llo").matches("heeeello"));
        assertFalse(new Pattern("h*llo").matches("heeeel"));
    }

    @Test
    public void matchesRange() {
        assertTrue(new Pattern("h[ae]llo").matches("hello"));
        assertTrue(new Pattern("h[ae]llo").matches("hallo"));
        assertFalse(new Pattern("h[ae]llo").matches("hxllo"));

        assertFalse(new Pattern("h[ae]llo").matches("hllo"));

        assertFalse(new Pattern("h[^ae]llo").matches("hello"));
        assertFalse(new Pattern("h[^ae]llo").matches("hallo"));
        assertTrue(new Pattern("h[^ae]llo").matches("hxllo"));

        assertTrue(new Pattern("h[a-e]llo").matches("hello"));
        assertTrue(new Pattern("h[a-e]llo").matches("hbllo"));
        assertFalse(new Pattern("h[a-e]llo").matches("hxllo"));

        assertFalse(new Pattern("h[^a-e]llo").matches("hello"));
        assertFalse(new Pattern("h[^a-e]llo").matches("hbllo"));
        assertTrue(new Pattern("h[^a-e]llo").matches("hxllo"));
    }

    @Test
    public void escapesChars() {
        assertTrue(new Pattern("h\\*llo").matches("h*llo"));
        assertFalse(new Pattern("h\\8llo").matches("hello"));

        assertTrue(new Pattern("h\\[ae]llo").matches("h[ae]llo"));
        assertFalse(new Pattern("h\\[ae]llo").matches("hello"));

        assertTrue(new Pattern("h\\?llo").matches("h?llo"));
        assertFalse(new Pattern("h\\?llo").matches("hello"));
    }
}
