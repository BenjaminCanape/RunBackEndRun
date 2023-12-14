package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.dto.EditPasswordDto;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = {
        "spring.uploads.folder=uploads/"
})
class UserServiceImplTest {

    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private Environment env;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(jwtUtils, bCryptPasswordEncoder, userRepository, env);
    }

    @Test
    void create_ReturnsUserId() {
        // Mock
        User user = new User();
        user.setId((long) 1.00);
        user.setUsername("test_user");
        user.setPassword("password");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call
        Long result = userService.create(user);

        // Verify
        assertNotNull(result);
        verify(userRepository).findByUsername(user.getUsername());
        verify(bCryptPasswordEncoder).encode("password");
        verify(userRepository).save(user);
    }

    @Test
    void create_ThrowsRuntimeExceptionWhenUsernameAlreadyExists() {
        // Mock
        User user = new User();
        user.setUsername("existing_user");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Call and verify
        assertThrows(RuntimeException.class, () -> userService.create(user));
    }

    @Test
    void findByUsername_ReturnsUser() {
        // Mock
        String username = "test_user";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Call
        Optional<User> result = userService.findByUsername(username);

        // Verify
        assertNotNull(result);
        assertEquals(user, result.get());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void editPassword_ReturnsUserId() {
        // Mock
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("username");
        user.setPassword("password");

        User existing = new User();
        existing.setId(userId);
        existing.setUsername("username");
        existing.setPassword("password");

        String token = "test_token";
        EditPasswordDto dto = new EditPasswordDto();
        dto.setPassword("password");
        dto.setCurrentPassword("password");

        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn(user.getUsername());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call
        Long result = userService.editPassword(token, dto);

        // Verify
        assertNotNull(result);
        assertEquals(userId, result);
        verify(userRepository).findByUsername(user.getUsername());
        verify(bCryptPasswordEncoder).matches(anyString(), anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void editPassword_ThrowsBadCredentialsWhenCurrentPasswordIsIncorrect() {
        // Mock
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("username");
        String token = "test_token";
        EditPasswordDto dto = new EditPasswordDto();
        dto.setPassword("password");
        dto.setCurrentPassword("password");

        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn(user.getUsername());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);


        // Call and verify
        assertThrows(BadCredentialsException.class, () -> userService.editPassword(token, dto));
    }

    @Test
    void delete_DeletesUser() {
        // Mock
        String token = "test_token";
        String username = "test_user";
        User user = new User();
        user.setUsername(username);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn(username);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Call
        userService.delete(token);

        // Verify
        verify(jwtUtils).getUserNameFromJwtToken(token);
        verify(userRepository).findByUsername(username);
        verify(userRepository).deleteById(user.getId());
    }
}
