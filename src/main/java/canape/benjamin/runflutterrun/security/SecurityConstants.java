package canape.benjamin.runflutterrun.security;

public class SecurityConstants {

    public static final String SECRET = "SECRET_KEY";
    public static final long EXPIRATION_TIME = 7200000; //2h
    public static final long REFRESH_EXPIRATION_TIME = 7776000000L; //3 months
    public static final String SIGN_UP_URL = "/api/user/login";
    public static final String REGISTER_URL = "/api/user/register";

    public static final String LOGOUT_URL = "/api/private/user/logout";

    public static final String REFRESH_TOKEN_URL = "/api/user/refreshToken";
}
