package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.Activity;
import org.springframework.stereotype.Service;

@Service
public interface IActivityService extends ICrudService<Activity> {

    /**
     * Get all activities associated with a user.
     *
     * @param token the user's token
     * @return an iterable collection of activities
     */
    Iterable<Activity> getAll(String token);

    /**
     * Get my activities and my friends.
     *
     * @param token the user's token
     * @return an iterable collection of activities
     */
    Iterable<Activity> getMineAndMyFriends(String token);

    /**
     * Get all activities associated with a user.
     *
     * @param token the user's token
     * @param userId the user id
     * @return an iterable collection of activities
     */
    Iterable<Activity> getByUser(String token, Long userId);

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
