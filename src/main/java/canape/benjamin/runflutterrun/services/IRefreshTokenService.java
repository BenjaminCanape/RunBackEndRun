package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IRefreshTokenService {
    /**
     * Generate a new access token from a request token.
     *
     * @param requestTokenString the request token string
     * @return the new access token
     */
    String generateNewAccessTokenFromRequestToken(String requestTokenString);

    /**
     * Find a refresh token by its token string.
     *
     * @param token the token string
     * @return an optional containing the refresh token if found, or empty otherwise
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Create a new refresh token for the specified username.
     *
     * @param username the username
     * @return the created refresh token
     */
    RefreshToken createRefreshToken(String username);

    /**
     * Verify the expiration of a refresh token.
     *
     * @param token the refresh token to verify
     * @return the verified refresh token
     */
    RefreshToken verifyExpiration(RefreshToken token);

    /**
     * Delete refresh tokens associated with the specified username.
     *
     * @param username the username
     * @return the number of deleted refresh tokens
     */
    int deleteByUsername(String username);
}
