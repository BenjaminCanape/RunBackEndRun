package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    Long create(User user);

    User findByUsername(String username);

    Long editPassword(User user);

    void delete(String token);
}