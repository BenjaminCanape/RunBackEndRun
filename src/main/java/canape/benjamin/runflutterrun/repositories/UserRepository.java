package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
     * Retrieves users corresponding to the search text
     *
     * @param searchText The search text.
     * @return A List containing the user who correspond to the search text.
     */
    @Query("select u from User u where (u.username like %:searchText% or u.firstname like %:searchText% or u.lastname like %:searchText%)and u.username <> :currentUsername")
    List<User> search(@Param("searchText") String searchText, @Param("currentUsername") String currentUsername);

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user.
     * @return The user if found, or null if not found.
     */
    User findByUsername(String username);
}
