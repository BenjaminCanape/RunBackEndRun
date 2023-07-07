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
     * Create an activity associated with a user.
     *
     * @param activity the activity to create
     * @param token    the user's token
     * @return the created activity
     */
    Activity create(Activity activity, String token);
}
