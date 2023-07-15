package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.services.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PasswordResetServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendNewPasswordByMail() {
        String email = "test@example.com";
        String newPassword = "newPassword123";

        User existingUser = new User();
        existingUser.setUsername(email);
        when(userRepository.findByUsername(email)).thenReturn(existingUser);

        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("$2a$10$S2mc7p84qNTozR9oiOEEfeCJ08VUZfUtnQIQs.GzupC6QSwTY5XCq");

        String result = passwordResetService.sendNewPasswordByMail(email);

        verify(userRepository, times(1)).findByUsername(email);
        verify(userRepository, times(1)).save(any(User.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));

        assertEquals("A new password was send to the mail" + email, result);
    }
}
