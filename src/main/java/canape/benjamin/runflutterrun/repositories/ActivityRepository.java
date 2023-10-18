package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {

    /**
     * Retrieves an activity by its ID, including its associated locations.
     *
     * @param id The ID of the activity.
     * @return An Optional containing the activity if found, or an empty Optional if not found.
     */
    @Query("select a from Activity a left join fetch a.locations locations where a.id = :id")
    Optional<Activity> findActivityById(@Param("id") long id);

    /**
     * Retrieves all activities sorted by start datetime in descending order.
     *
     * @return An iterable collection of activities.
     */
    Iterable<Activity> findAllByOrderByStartDatetimeDesc();

    /**
     * Retrieves all activities associated with a specific user, sorted by start datetime in descending order.
     *
     * @param user The user for which to retrieve activities.
     * @return An iterable collection of activities.
     */
    @Query("select a from Activity a left join fetch a.user u where u = :user order by a.startDatetime DESC")
    Iterable<Activity> findAllByOrderByStartDatetimeDescAndUser(@Param("user") User user);

    /**
     * Retrieves all activities associated with specific users, sorted by start datetime in descending order.
     *
     * @param users The users for which to retrieve activities.
     * @return An iterable collection of activities.
     */
    @Query("select a from Activity a left join fetch a.user u where u in :users order by a.startDatetime DESC")
    Iterable<Activity> findAllByOrderByStartDatetimeDescAndUsers(@Param("users") List<User> users);
}
