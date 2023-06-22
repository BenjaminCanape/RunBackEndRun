package canape.benjamin.runflutterrun.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class TokenManager {
    private static final Set<String> validTokens = Collections.synchronizedSet(new HashSet<>());

    private TokenManager() {
    }

    public static boolean isValidToken(String token) {
        return validTokens.contains(token);
    }

    public static void invalidateToken(String token) {
        validTokens.remove(token);
    }

    public static void addValidToken(String token) {
        validTokens.add(token);
    }
}