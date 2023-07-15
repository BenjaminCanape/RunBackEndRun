package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.services.IPasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MailController {

    @Autowired
    private IPasswordResetService passwordResetService;

    /**
     * Send a mail to give new password to user by email
     *
     * @param email The email of the user
     * @return A message to give the result of the action
     */
    @PostMapping("/sendNewPasswordByMail")
    public String sendNewPasswordByMail(@RequestParam String email) {
        return passwordResetService.sendNewPasswordByMail(email);
    }
}
