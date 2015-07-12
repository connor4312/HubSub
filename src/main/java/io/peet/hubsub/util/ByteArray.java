package io.peet.hubsub.util;


public class ByteArray {
    public byte[] data;

    protected ByteArray() {}

    /**
     * Creates a new byte array, using the data as the initial underlying
     * byte array.
     * @param data the data to encapsulate
     */
    public ByteArray(byte[] data) {
        this.data = data;
    }

    /**
     * Return the number of bytes in the data.
     * @return the number of bytes
     */
    public int size() {
        return data.length;
    }

    /**
     * Returns the underlying byte array.
     * @return an array of bytes
     */
    public byte[] array() {
        return data;
    }

    /**
     * Returns whether the byte array ends with a sequence of bytes.
     * @param b a sequence of bytes
     * @return true if it ends with that sequence
     */
    public boolean endsWith(byte[] b) {
        if (b.length > data.length) {
            return false;
        }

        for (int i = 0, j = data.length - b.length; i < b.length; i++, j++) {
            if (data[j] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the byte array starts with a sequence of bytes.
     * @param b a sequence of bytes
     * @return true if it starts with that sequence
     */
    public boolean startsWith(byte[] b) {
        if (b.length > data.length) {
            return false;
        }

        for (int i = 0; i < b.length; i++) {
            if (data[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Concats data onto the byte array.
     * @param arrays bytes to add on
     */
    public ByteArray write(byte[]... arrays) {
        if (data != null) {
            byte[][] newArrays = new byte[arrays.length+1][];
            newArrays[0] = data;
            System.arraycopy(arrays, 0, newArrays, 1, arrays.length);
            arrays = newArrays;
        }

        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }

        data = new byte[length];
        int pos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, data, pos, array.length);
            pos += array.length;
        }

        return this;
    }

    /**
     * Returns a subsection of the byte array from the start to the end.
     * The start or end can be negative to get from the end.
     *
     * @param start start index
     * @param end end index
     * @return the sliced array
     */
    public byte[] slice(int start, int end) {
        if (end < 0)   end = data.length - end;
        if (start < 0) start = data.length - start;

        byte[] out = new byte[end-start];
        System.arraycopy(data, start, out, 0, end-start);

        return out;
    }

    /**
     * Returns a subsection of the array from the start to the end of the array.
     * @param start starting index
     * @return a byte slice
     */
    public byte[] slice(int start) {
        return slice(start, data.length);
    }

    /**
     * Searches for the first position of needle after pos.
     * @param needle the "substring" to search for
     * @param pos the starting position
     * @return the index of the start of the needle, or -1 if not found
     */
    public int indexOf(byte[] needle, int pos) {
        if (needle.length == 0) {
            return 0;
        }

        outer:
        for (int i = pos; i <= data.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (data[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return i;
        }

        return -1;
    }

    /**
     * Searches for the first position of needle in the bytearray.
     * @param needle the substring to search for
     * @return the index of the start of the needle, or -1 if not found
     */
    public int indexOf(byte[] needle) {
        return indexOf(needle, 0);
    }

    /**
     * Creates a new byte array by a concatenation of a list of arrays.
     * @param arrays the arrays to concat.
     */
    public static ByteArray from(byte[]... arrays) {
        if (arrays.length == 1) {
            return new ByteArray(arrays[0]);
        }

        return new ByteArray().write(arrays);
    }
}