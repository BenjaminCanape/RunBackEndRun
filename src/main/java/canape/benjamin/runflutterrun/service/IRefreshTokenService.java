package canape.benjamin.runflutterrun.service;

import canape.benjamin.runflutterrun.model.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IRefreshTokenService {
    String generateNewAccessTokenFromRequestToken(String requestTokenString);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(String username);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUsername(String username);
}