package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.services.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private IUserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userService);
    }

    @Test
    void loadUserByUsername_ReturnsUserDetails() {
        // Mock
        String username = "test_user";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded_password");
        when(userService.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Call
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Verify
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(userService).findByUsername(username);
    }

    @Test
    void loadUserByUsername_ThrowsUsernameNotFoundExceptionWhenUserNotFound() {
        // Mock
        String username = "non_existing_user";
        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());

        // Call and verify
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
        verify(userService).findByUsername(username);
    }
}
