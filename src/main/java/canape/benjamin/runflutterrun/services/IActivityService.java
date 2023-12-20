package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface IActivityService extends ICrudService<Activity> {

    /**
     * Get all activities associated with a user.
     *
     * @param token the user's token
     * @param pageable the pagination information
     * @return an iterable collection of activities
     */
    Page<Activity> getAll(String token, Pageable pageable);

    /**
     * Get my activities and my friends.
     *
     * @param token the user's token
     * @param pageable the pagination information
     * @return an iterable collection of activities
     */
     Page<Activity> getMineAndMyFriends(String token, Pageable pageable);

    /**
     * Get all activities associated with a user.
     *
     * @param token the user's token
     * @param userId the user id
     * @param pageable the pagination information
     * @return an iterable collection of activities
     */
    Page<Activity> getByUser(String token, Long userId, Pageable pageable);

    /**
     * Create an activity associated with a user.
     *
     * @param activity the activity to create
     * @param token    the user's token
     * @return the created activity
     */
    Activity create(Activity activity, String token);

    /**
     * Like an activity
     *
     * @param id    the activity id to like
     * @param token the user's token
     */
    void like(Long id, String token);

    /**
     * Dislike an activity
     *
     * @param id the activity id to dislike
     * @param token    the user's token
     */
    void dislike(Long id, String token);

    /**
     * Get activity like count
     *
     * @param id the activity id to count likes
     * @return likes count
     */
    long getActivityLikeCount(Long id);

    /**
     * Has current user liked activity
     *
     * @param id the activity id
     * @param token    the user's token
     * @return has current user liked activity
     */
    boolean currentUserLiked(Long id, String token);
}
