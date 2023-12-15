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
    Optional<User> findById(long id);

    /**
     * Retrieves users corresponding to the search text
     *
     * @param searchText The search text.
     * @return A List containing the user who correspond to the search text.
     */
    @Query("SELECT u FROM User u WHERE lower(u.username) LIKE lower(concat('%', :searchText, '%')) OR lower(u.firstname) LIKE lower(concat('%', :searchText, '%')) OR lower(u.lastname) LIKE lower(concat('%', :searchText, '%')) AND u.username <> :currentUsername")
    List<User> findByUsernameOrFirstNameOrLastName(@Param("searchText") String searchText, @Param("currentUsername") String currentUsername);

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user.
     * @return The user if found, or null if not found.
     */
    Optional<User> findByUsername(String username);
}
