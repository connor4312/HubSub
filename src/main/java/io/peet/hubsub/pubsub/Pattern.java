package io.peet.hubsub.pubsub;

/**
 * Implementation of Redis pattern matching:
 * - h?llo subscribes to hello, hallo and hxllo
 * - h*llo subscribes to hllo and heeeello
 * - h[ae]llo subscribes to hello and hallo, but not hillo
 */
public class Pattern {

    protected String pattern;

    public Pattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * This is pretty much copied from the implementation in the Redis server
     * at http://git.io/vmZBZ, with slight modifications to make it
     * more easy to express in Java.
     */
    protected boolean stringmatchlen(char[] pattern, int pi, char[] string, int si) {
        int patternLen = pattern.length;
        int stringLen = string.length;


        while (pi < patternLen) {
            switch (pattern[pi]) {
                case '*':
                    while (pi + 1 < patternLen && pattern[pi + 1] == '*') {
                        pi++;
                    }

                    if (patternLen - pi == 1)
                        return true; /* match */
                    while (si < stringLen) {
                        if (stringmatchlen(pattern, pi + 1, string, si))
                            return true; /* match */
                        si++;
                    }
                    return false;
                case '?':
                    if (stringLen - si == 0)
                        return false;
                    si++;
                    break;
                case '[':

                    pi++;
                    boolean not = pattern[pi] == '^';
                    if (not) pi++;

                    boolean match = false;
                    while (true) {
                        if (pattern[pi] == '\\') {
                            pi++;
                            if (pattern[pi] == string[si])
                                match = true;
                        } else if (pattern[pi] == ']') {
                            break;
                        } else if (patternLen - pi == 0) {
                            pi--;
                            break;
                        } else if (pattern[pi + 1] == '-' &
                                patternLen - pi >= 3) {
                            int start = pattern[pi];
                            int end = pattern[pi + 2];
                            int c = string[si];
                            if (start > end) {
                                int t = start;
                                start = end;
                                end = t;
                            }

                            pi += 2;
                            if (c >= start && c <= end) match = true;
                        } else if (pattern[pi] == string[si]) {
                            match = true;
                        }
                        pi++;
                    }
                    if (not)
                        match = !match;
                    if (!match)
                        return false;
                    si++;
                    break;
                case '\\':
                    if (patternLen - pi >= 2) {
                        pi++;
                    }
            /* fall through */
                default:
                    if (si >= stringLen || pattern[pi] != string[si])
                        return false; /* no match */

                    si++;
                    break;
            }
            pi++;
            if (stringLen - si == 0) {
                while (pi < patternLen && pattern[pi] == '*') {
                    pi++;
                }
                break;
            }
        }

        return patternLen - pi == 0 && stringLen - si == 0;
    }

    /**
     * Returns whether the pattern matches the given string. This is
     * pretty much ported verbatim from the C implementation
     * here: http://git.io/vmZBZ
     *
     * @param input string to compare against
     * @return true if it matches.
     */
    public boolean matches(String input) {
        char[] pattern = this.pattern.toCharArray();
        char[] string = input.toCharArray();

        return stringmatchlen(pattern, 0, string, 0);
    }
}
