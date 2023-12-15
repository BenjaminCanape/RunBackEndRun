package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.services.IPasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.security.SecureRandom;

@Service
@AllArgsConstructor
public class PasswordResetServiceImpl  implements IPasswordResetService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Send a new password by mail
     *
     * @param email the mail to get the user from and to send a mail to
     * @return string a message of success
     */
    public String sendNewPasswordByMail(String email) {
        String newPassword = generateNewPassword();

        User existingUser = userRepository.findByUsername(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        existingUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(existingUser);

        String subject = "New password for Run Flutter Run";
        String message = "Your new password is: " + newPassword;

        try {
            sendMail(email, subject, message);
            return "A new password has been sent to the email: " + email;
        } catch (MessagingException e) {
            return "Failed to send the new password to the email: " + email;
        }
    }


    /**
     * Send the mail
     *
     * @param to the address to send the mail
     * @param subject the subject of the mail
     * @param message the message to send the mail
     */
    private void sendMail(String to, String subject, String message) throws MessagingException, MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true);

        javaMailSender.send(mimeMessage);
    }

    /**
     * Generate a new password
     *
     * @return string the new password
     */
    private String generateNewPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 8;
        SecureRandom random = new SecureRandom();

        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            newPassword.append(characters.charAt(index));
        }

        return newPassword.toString();
    }
}
