package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user.
     * @return An Optional containing the user if found, or an empty Optional if not found.
     */
    @Query("select u from User u where u.id = :id")
    Optional<User> findUserById(@Param("id") long id);

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user.
     * @return The user if found, or null if not found.
     */
    User findByUsername(String username);
}