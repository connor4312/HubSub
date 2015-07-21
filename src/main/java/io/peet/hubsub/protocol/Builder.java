package io.peet.hubsub.protocol;

import akka.util.ByteString;

import java.io.UnsupportedEncodingException;

/**
 * The builder provides methods used internally to assist with the building
 * and recognition of packets.
 */
public class Builder {
    /**
     * Charset used for all string encodings.
     */
    public static String charset = "UTF-8";

    /**
     * The delimiter is added to the end of commands to separate them.
     */
    public static ByteString delimiter = stringToBytes("\r\n");

    // Tokens for varying packet types.
    public static ByteString ArrayToken = stringToBytes("*");
    public static ByteString BulkStringToken = stringToBytes("$");
    public static ByteString IntegerToken = stringToBytes(":");
    public static ByteString ErrorToken = stringToBytes("-");
    public static ByteString SimpleStringToken = stringToBytes("+");

    /**
     * Attempts to encode the string to a byte array using the default encoding.
     */
    public static ByteString stringToBytes(String str) {
        byte[] strbytes;
        try {
            strbytes = str.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            strbytes = str.getBytes();
        }

        return ByteString.fromArray(strbytes);
    }

    /**
     * Returns the index of needle in the haystack.
     *
     * @param needle byte string to search for
     * @param haystack byte string to search in
     * @return the position of the needle, or -1 if not found.
     */
    public static int indexOf(ByteString needle, ByteString haystack) {
        byte[] n = needle.toArray();
        byte[] h = haystack.toArray();

        OUTER:
        for (int i = 0; i <= h.length - n.length; i++) {
            for (int j = 0; j < n.length; j++) {
                if (h[i+j] != n[j]) {
                    continue OUTER;
                }
            }

            return i;
        }

        return -1;
    }
}
