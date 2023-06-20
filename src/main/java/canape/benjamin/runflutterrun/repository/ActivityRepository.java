package canape.benjamin.runflutterrun.repository;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {
    @Query("select a from Activity a left join fetch a.locations locations where a.id = :id")
    public Optional<Activity> findActivityById(@Param("id") long id);

    public Iterable<Activity> findAllByOrderByStartDatetimeDesc();

    @Query("select a from Activity a left join fetch a.user u where u = :user")
    public Iterable<Activity> findAllByOrderByStartDatetimeDescAndUser(@Param("user") User user);
}