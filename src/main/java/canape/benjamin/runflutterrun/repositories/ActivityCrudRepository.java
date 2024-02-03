package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityCrudRepository extends CrudRepository<Activity, Long> {
    /**
     * Retrieves an activity by its ID, including its associated locations.
     *
     * @param id The ID of the activity.
     * @return An Optional containing the activity if found, or an empty Optional if not found.
     */
    @Query("select a from Activity a left join fetch a.locations locations where a.id = :id")
    Optional<Activity> findById(@Param("id") long id);

}
