package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.RefreshToken;
import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    /**
     * Retrieves a refresh token by its token value.
     *
     * @param refreshToken The token value.
     * @return An Optional containing the refresh token if found, or an empty Optional if not found.
     */
    Optional<RefreshToken> findByToken(String refreshToken);


    /**
     * Deletes all refresh tokens associated with a specific user.
     *
     * @param user The user for which to delete refresh tokens.
     * @return The number of refresh tokens deleted.
     */
    @Modifying
    int deleteByUser(User user);
}
