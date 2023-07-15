package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.services.IPasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

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

        User existingUser = userRepository.findByUsername(email);
        existingUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(existingUser);

        String subject = "New password for Run Flutter Run";
        String message = "Your new password is : " + newPassword;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);

        return "A new password was send to the mail" + email;
    }

    /**
     * Generate a new password
     *
     * @return string the new password
     */
    private String generateNewPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 8;
        StringBuilder newPassword = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            newPassword.append(characters.charAt(index));
        }

        return newPassword.toString();
    }
}
