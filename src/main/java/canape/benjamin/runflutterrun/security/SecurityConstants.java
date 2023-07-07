package canape.benjamin.runflutterrun.security;

public class SecurityConstants {

    /**
     * Secret key used for JWT signing.
     */
    public static final String SECRET = "SECRET_KEY";

    /**
     * Expiration time for access tokens (in milliseconds).
     */
    public static final long EXPIRATION_TIME = 7200000; // 2 hours

    /**
     * Expiration time for refresh tokens (in milliseconds).
     */
    public static final long REFRESH_EXPIRATION_TIME = 7776000000L; // 3 months

    /**
     * URL for user login.
     */
    public static final String SIGN_UP_URL = "/api/user/login";

    /**
     * URL for user registration.
     */
    public static final String REGISTER_URL = "/api/user/register";

    /**
     * URL for user logout.
     */
    public static final String LOGOUT_URL = "/api/private/user/logout";

    /**
     * URL for refreshing access tokens.
     */
    public static final String REFRESH_TOKEN_URL = "/api/user/refreshToken";
}
