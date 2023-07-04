package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private JwtUtils jwtUtils;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserRepository userRepository;

    @Override
    public Long create(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("An account already exists for this email");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user).getId();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Long editPassword(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + user.getId() + " not found."));
        existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(existingUser).getId();
    }

    @Override
    public void delete(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        userRepository.deleteById(user.getId());
    }
}