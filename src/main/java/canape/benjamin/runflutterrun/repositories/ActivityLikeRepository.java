package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.ActivityLike;
import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityLikeRepository extends CrudRepository<ActivityLike, Long> {

    /**
     * Retrieves the number of likes by activity
     *
     * @param activityId The activity for which to retrieve likes.
     * @return the number of activity likes by activity
     */
    long countByActivityId(Long activityId);

    /**
     * Retrieves the like from an activity and a user.
     *
     * @param activity The activity for which to retrieve likes.
     * @param user     The user for which to retrieve likes.
     * @return An optional of activity like.
     */
    Optional<ActivityLike> findByActivityAndUser(@Param("activity") Activity activity, @Param("user") User user);

}