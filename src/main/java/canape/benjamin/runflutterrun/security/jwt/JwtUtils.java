package canape.benjamin.runflutterrun.security.jwt;

import canape.benjamin.runflutterrun.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

import static canape.benjamin.runflutterrun.security.SecurityConstants.EXPIRATION_TIME;
import static canape.benjamin.runflutterrun.security.SecurityConstants.SECRET;

@Component
public class JwtUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * Generates a JWT token for the given user.
     *
     * @param user The user.
     * @return The generated JWT token.
     */
    public String generateJwtToken(User user) {
        return generateTokenFromUsername(user.getUsername());
    }

    /**
     * Generates a JWT token from the given username.
     *
     * @param username The username.
     * @return The generated JWT token.
     */
    public String generateTokenFromUsername(String username) {
        Date currentDate = new Date();
        String token = JWT.create()
                .withSubject(username)
                .withIssuedAt(currentDate)
                .withExpiresAt(new Date(currentDate.getTime() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET));

        TokenManager.addValidToken(token);

        return token;
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token The JWT token.
     * @return The extracted username.
     */
    public String getUserNameFromJwtToken(String token) {
        token = extractTokenFromBearerToken(token);

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build().verify(token);
        return decodedJWT.getSubject();
    }

    /**
     * Validates the given JWT token.
     *
     * @param authToken The JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        if (TokenManager.isValidToken(authToken)) {
            try {
                String token = extractTokenFromBearerToken(authToken);

                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                        .build()
                        .verify(token);
                return true;
            } catch (TokenExpiredException e) {
                LOGGER.error("JwtUtils | validateJwtToken | JWT token is expired: {}", e.getMessage());
            } catch (JWTVerificationException e) {
                LOGGER.error("JwtUtils | validateJwtToken | JWT verification failed: {}", e.getMessage());
            }
        }
        return false;
    }

    /**
     * Extracts the token from the "Bearer" token format.
     *
     * @param bearerToken The "Bearer" token.
     * @return The extracted token.
     */
    private String extractTokenFromBearerToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    /**
     * Extracts the token from the Authorization header of the HTTP request.
     *
     * @param request The HTTP request.
     * @return The extracted token.
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return extractTokenFromBearerToken(bearerToken);
    }
}
