package canape.benjamin.runflutterrun.security.jwt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class TokenManager {
    private static final Set<String> validTokens = Collections.synchronizedSet(new HashSet<>());

    private TokenManager() {
    }

    /**
     * Checks if a token is valid.
     *
     * @param token The token to check.
     * @return true if the token is valid, false otherwise.
     */
    public static boolean isValidToken(String token) {
        return validTokens.contains(token);
    }

    /**
     * Invalidates a token.
     *
     * @param token The token to invalidate.
     */
    public static void invalidateToken(String token) {
        validTokens.remove(token);
    }

    /**
     * Adds a valid token.
     *
     * @param token The token to add.
     */
    public static void addValidToken(String token) {
        validTokens.add(token);
    }
}
