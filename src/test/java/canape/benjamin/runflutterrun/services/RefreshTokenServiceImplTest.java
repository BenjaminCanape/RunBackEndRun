package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.exceptions.RefreshTokenException;
import canape.benjamin.runflutterrun.model.RefreshToken;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.RefreshTokenRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceImplTest {

    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, userRepository, jwtUtils);
    }

    @Test
    void findByToken_ReturnsRefreshToken() {
        // Mock
        String tokenString = "test_token";
        RefreshToken refreshToken = new RefreshToken();
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));

        // Call
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(refreshToken, result.get());
        verify(refreshTokenRepository).findByToken(tokenString);
    }

    @Test
    void findByToken_ReturnsEmptyOptionalWhenTokenNotFound() {
        // Mock
        String tokenString = "non_existing_token";
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        // Call
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        // Verify
        assertTrue(result.isEmpty());
        verify(refreshTokenRepository).findByToken(tokenString);
    }

    @Test
    void generateNewAccessTokenFromRequestToken_ReturnsAccessToken() {
        // Mock
        String requestTokenString = "request_token";
        User user = new User();
        user.setUsername("username");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(10000));
        refreshToken.setToken(requestTokenString);
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));
        when(jwtUtils.generateTokenFromUsername(anyString())).thenReturn("access_token");

        // Call
        String result = refreshTokenService.generateNewAccessTokenFromRequestToken(requestTokenString);

        // Verify
        assertNotNull(result);
        assertEquals("access_token", result);
        verify(refreshTokenRepository).findByToken(requestTokenString);
        verify(jwtUtils).generateTokenFromUsername(refreshToken.getUser().getUsername());
    }

    @Test
    void generateNewAccessTokenFromRequestToken_ThrowsRefreshTokenExceptionWhenTokenNotFound() {
        // Mock
        String requestTokenString = "non_existing_token";
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        // Call and verify
        assertThrows(RefreshTokenException.class, () -> refreshTokenService.generateNewAccessTokenFromRequestToken(requestTokenString));
        verify(refreshTokenRepository).findByToken(requestTokenString);
    }

    @Test
    void createRefreshToken_ReturnsRefreshToken() {
        // Mock
        String username = "test_user";
        User user = new User();
        user.setUsername(username);
        RefreshToken existingRefreshToken = new RefreshToken();
        existingRefreshToken.setUser(user);

        Instant date = Instant.now().plusMillis(10000);
        RefreshToken savedRefreshToken = new RefreshToken();
        savedRefreshToken.setUser(user);
        savedRefreshToken.setExpiryDate(date);
        savedRefreshToken.setToken("new_token");
        user.setRefreshToken(existingRefreshToken);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedRefreshToken);

        // Call
        RefreshToken result = refreshTokenService.createRefreshToken(username);

        // Verify
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(date, result.getExpiryDate());
        assertEquals("new_token", result.getToken());
        verify(userRepository).findByUsername(username);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void deleteByUsername_ReturnsDeletedCount() {
        // Mock
        String username = "test_user";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(refreshTokenRepository.deleteByUser(any(User.class))).thenReturn(1);

        // Call
        int result = refreshTokenService.deleteByUsername(username);

        // Verify
        assertEquals(1, result);
        verify(userRepository).findByUsername(username);
        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    void verifyExpiration_ReturnsRefreshTokenWhenNotExpired() {
        // Mock
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusMillis(10000));

        // Call
        RefreshToken result = refreshTokenService.verifyExpiration(token);

        // Verify
        assertEquals(token, result);
    }

    @Test
    void verifyExpiration_ThrowsRefreshTokenExceptionWhenExpired() {
        // Mock
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusMillis(10000));

        // Call and verify
        assertThrows(RefreshTokenException.class, () -> refreshTokenService.verifyExpiration(token));
    }
}
