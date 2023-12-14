package canape.benjamin.runflutterrun.services.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import canape.benjamin.runflutterrun.exceptions.RefreshTokenException;
import canape.benjamin.runflutterrun.model.RefreshToken;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.RefreshTokenRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IRefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import static canape.benjamin.runflutterrun.security.SecurityConstants.REFRESH_EXPIRATION_TIME;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    private JwtUtils jwtUtils;

    /**
     * Find a refresh token by its token string.
     *
     * @param token the token string
     * @return an Optional containing the refresh token, or an empty Optional if not found
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Generate a new access token from a request token.
     *
     * @param requestTokenString the request token string
     * @return the new access token
     * @throws RefreshTokenException if the refresh token is not found in the database
     */
    public String generateNewAccessTokenFromRequestToken(String requestTokenString) {
        Optional<RefreshToken> token = findByToken(requestTokenString);

        if (token.isPresent()) {
            RefreshToken verifiedToken = verifyExpiration(token.get());
            return jwtUtils.generateTokenFromUsername(verifiedToken.getUser().getUsername());
        }

        throw new RefreshTokenException(requestTokenString, "Refresh token is not in the database!");
    }


    /**
     * Create a new refresh token for the given username.
     *
     * @param username the username
     * @return the created refresh token
     */
    @Transactional()
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        RefreshToken refreshToken = Optional.ofNullable(user.getRefreshToken()).orElseGet(RefreshToken::new);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plus(REFRESH_EXPIRATION_TIME, ChronoUnit.MILLIS));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    /**
     * Delete the refresh token associated with the given username.
     *
     * @param username the username
     * @return the number of refresh tokens deleted (0 or 1)
     */
    @Transactional()
    public int deleteByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Verify the expiration of a refresh token.
     *
     * @param token the refresh token to verify
     * @return the verified refresh token
     * @throws RefreshTokenException if the refresh token is expired
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }
}
