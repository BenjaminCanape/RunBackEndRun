package canape.benjamin.runflutterrun.service;

import canape.benjamin.runflutterrun.model.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    Integer create(User user);

    User findByUsername(String username);

    Integer editPassword(User user);

    void delete(long id);
}