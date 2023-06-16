package canape.benjamin.runflutterrun.security;

import canape.benjamin.runflutterrun.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
        long currentTimeMillis = System.currentTimeMillis();
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date(currentTimeMillis))
                .withExpiresAt(new Date(currentTimeMillis + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SECRET));
    }

    public String getUserNameFromJwtToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                    .build()
                    .verify(authToken);
            return true;
        } catch (JWTVerificationException e) {
            if (e instanceof TokenExpiredException) {
                LOGGER.error("JwtUtils | validateJwtToken | JWT token is expired: {}", e.getMessage());
            } else if (e instanceof SignatureVerificationException) {
                LOGGER.error("JwtUtils | validateJwtToken | Invalid signature: {}", e.getMessage());
            } else if (e instanceof InvalidClaimException) {
                LOGGER.error("JwtUtils | validateJwtToken | Invalid claim: {}", e.getMessage());
            } else {
                LOGGER.error("JwtUtils | validateJwtToken | JWT verification failed: {}", e.getMessage());
            }
        }

        return false;
    }
}