package canape.benjamin.runflutterrun.service.impl;

import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repository.UserRepository;
import canape.benjamin.runflutterrun.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Integer create(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("An account already exist for this email");
        }
        user.setPassword(bCryptPasswordEncoder
                .encode(user.getPassword()));
        return userRepository.save(user).getId();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Integer editPassword(User user) {
        User finalUser = user;
        User user1 = userRepository.findUserById(user.getId()).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + finalUser.getId() + ", not available."));
        user1.setPassword(bCryptPasswordEncoder
                .encode(user.getPassword()));
        return userRepository.save(user1).getId();
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }
}