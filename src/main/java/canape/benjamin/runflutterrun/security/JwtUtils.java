package canape.benjamin.runflutterrun.security;

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

    public String generateJwtToken(User user) {
        return generateTokenFromUsername(user.getUsername());
    }

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

    public String getUserNameFromJwtToken(String token) {
        token = extractTokenFromBearerToken(token);

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

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

    private String extractTokenFromBearerToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return extractTokenFromBearerToken(bearerToken);
    }
}