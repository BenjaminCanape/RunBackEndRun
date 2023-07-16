package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.dto.EditPasswordDto;
import canape.benjamin.runflutterrun.model.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    /**
     * Create a new user.
     *
     * @param user the user to create
     * @return the ID of the created user
     */
    Long create(User user);

    /**
     * Find a user by username.
     *
     * @param username the username
     * @return the found user, or null if not found
     */
    User findByUsername(String username);

    /**
     * Edit the password of a user.
     *
     * @param token the token associated with the user
     * @param dto the dto with the updated password
     * @return the ID of the user
     */
    Long editPassword(String token, EditPasswordDto dto);

    /**
     * Delete a user based on the provided token.
     *
     * @param token the token associated with the user to delete
     */
    void delete(String token);
}
