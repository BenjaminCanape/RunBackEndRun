package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.services.IPasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MailController {

    private final IPasswordResetService passwordResetService;

    /**
     * Send a mail to give a new password to the user by email.
     *
     * @param email The email of the user.
     * @return A message indicating the result of the action.
     */
    @PostMapping("/sendNewPasswordByMail")
    public String sendNewPasswordByMail(@RequestParam String email) {
        return passwordResetService.sendNewPasswordByMail(email);
    }
}
