package canape.benjamin.runflutterrun.services;

import org.springframework.stereotype.Service;

@Service
public interface IPasswordResetService {
    /**
     * Send a new password by mail
     *
     * @param email the mail to get the user from and to send a mail to
     * @return string a message of success
     */
     String sendNewPasswordByMail(String email);
}
