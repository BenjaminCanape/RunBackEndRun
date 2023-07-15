package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.ActivityRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IActivityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements IActivityService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    /**
     * Retrieve all activities in descending order of start datetime.
     *
     * @return Iterable of activities
     */
    public Iterable<Activity> getAll() {
        return activityRepository.findAllByOrderByStartDatetimeDesc();
    }

    /**
     * Create a new activity.
     *
     * @param activity the activity to create
     * @return the created activity
     */
    @Override
    public Activity create(Activity activity) {
        Activity activityWithMetrics = calculateMetrics(activity);
        return activityRepository.save(activityWithMetrics);
    }

    /**
     * Retrieve all activities for a specific user in descending order of start datetime.
     *
     * @param token the authentication token of the user
     * @return Iterable of activities
     */
    @Override
    public Iterable<Activity> getAll(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        return activityRepository.findAllByOrderByStartDatetimeDescAndUser(user);
    }

    /**
     * Create a new activity for a specific user.
     *
     * @param activity the activity to create
     * @param token    the authentication token of the user
     * @return the created activity
     */
    @Override
    public Activity create(Activity activity, String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        Activity activityWithMetrics = calculateMetrics(activity);
        activityWithMetrics.setUser(user);
        return activityRepository.save(activityWithMetrics);
    }

    /**
     * Retrieve an activity by its ID.
     *
     * @param id the ID of the activity
     * @return the retrieved activity
     * @throws EntityNotFoundException if the activity with the given ID is not found
     */
    @Override
    public Activity getById(long id) {
        return activityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + id + " is not available."));
    }

    /**
     * Update an existing activity.
     *
     * @param activity the updated activity
     * @return the updated activity
     * @throws EntityNotFoundException if the activity with the given ID is not found
     */
    @Override
    public Activity update(Activity activity) {
        Activity existingActivity = activityRepository.findById(activity.getId()).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + activity.getId() + " is not available."));

        Activity updatedActivity = calculateMetrics(activity);
        existingActivity.setType(updatedActivity.getType());
        existingActivity.setDistance(updatedActivity.getDistance());
        existingActivity.setStartDatetime(updatedActivity.getStartDatetime());
        existingActivity.setEndDatetime(updatedActivity.getEndDatetime());
        existingActivity.setSpeed(updatedActivity.getSpeed());

        return activityRepository.save(existingActivity);
    }

    /**
     * Delete an activity by its ID.
     *
     * @param id the ID of the activity to delete
     */
    @Override
    public void delete(long id) {
        activityRepository.deleteById(id);
    }

    /**
     * Calculate the speed of an activity based on its distance and duration.
     *
     * @param activity the activity to calculate the metrics for
     * @return the activity with the calculated speed
     */
    private Activity calculateMetrics(Activity activity) {
        Date start = activity.getStartDatetime();
        Date end = activity.getEndDatetime();

        long time = end.getTime() - start.getTime();
        double speed = 0;
        if (time > 0) {
            double hours = (double) time / 3600000.0;
            speed = activity.getDistance() / hours;
        }
        activity.setSpeed(speed);
        return activity;
    }
}