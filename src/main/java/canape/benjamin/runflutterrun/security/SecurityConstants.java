package canape.benjamin.runflutterrun.security;

public class SecurityConstants {

    public static final String SECRET = "SECRET_KEY";
    public static final long EXPIRATION_TIME = 36_000_000;
    public static final String SIGN_UP_URL = "/api/user/login";
    public static final String REGISTER_URL = "/api/user/register";

    public static final String LOGOUT_URL = "/api/private/user/logout";
}
