package canape.benjamin.runflutterrun.repository;

import canape.benjamin.runflutterrun.model.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Integer> {
    @Query("select a from Activity a join fetch a.locations where a.id = ?1")
    public Optional<Activity> findActivityById(int id);
}