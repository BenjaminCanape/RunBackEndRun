package canape.benjamin.runflutterrun.services.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import canape.benjamin.runflutterrun.exceptions.RefreshTokenException;
import canape.benjamin.runflutterrun.model.RefreshToken;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.RefreshTokenRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static canape.benjamin.runflutterrun.security.SecurityConstants.REFRESH_EXPIRATION_TIME;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public String generateNewAccessTokenFromRequestToken(String requestTokenString){
        Optional<RefreshToken> token = findByToken(requestTokenString);

        if (token.isPresent()){
            RefreshToken verifiedToken = verifyExpiration(token.get());
            return jwtUtils.generateTokenFromUsername(verifiedToken.getUser().getUsername());
        }

        throw new RefreshTokenException(requestTokenString,
                "Refresh token is not in database!");
    }


    @Transactional()
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username);
        RefreshToken refreshToken = user.getRefreshToken();

        if(refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setUser(userRepository.findByUsername(username));
        }


        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_EXPIRATION_TIME));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Transactional()
    public int deleteByUsername(String username) {
        return refreshTokenRepository.deleteByUser(userRepository.findByUsername(username));
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token was expired. Please make ActivityType.java new signin request");
        }

        return token;
    }
}