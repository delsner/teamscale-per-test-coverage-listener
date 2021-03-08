package edu.tum.sse;

/**
 * A utility class providing some advanced string functionality.
 */
public class StringUtils {

    /**
     * Determine if the supplied {@link String} is <em>blank</em> (i.e., {@code null} or consisting only of whitespace
     * characters).
     *
     * @param str the string to check; may be {@code null}
     * @return {@code true} if the string is blank
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().isEmpty());
    }
}
