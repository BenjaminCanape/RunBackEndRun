package canape.benjamin.runflutterrun.repository;

import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    @Query("select u from User u where u.id = :id")
    Optional<User> findUserById(@Param("id") long id);

    User findByUsername(String username);
}