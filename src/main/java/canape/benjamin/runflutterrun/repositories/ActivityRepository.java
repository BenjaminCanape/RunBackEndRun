package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long> {

    /**
     * Retrieves all activities associated with a specific user, sorted by start datetime in descending order.
     *
     * @param user     The user for which to retrieve activities.
     * @param pageable Pageable object for pagination.
     * @return A Page of activities.
     */
    @Query("select a from Activity a where a.user = :user")
    Page<Activity> findByUser(User user, Pageable pageable);

    /**
     * Retrieves all activities associated with specific users, sorted by start datetime in descending order.
     *
     * @param users    The users for which to retrieve activities.
     * @param pageable Pageable object for pagination.
     * @return A Page of activities.
     */
    @Query("select a from Activity a where a.user in :users")
    Page<Activity> findByUsers(@Param("users") List<User> users, Pageable pageable);
}

