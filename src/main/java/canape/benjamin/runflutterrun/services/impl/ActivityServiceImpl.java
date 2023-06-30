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

    public Iterable<Activity> getAll() {
        return activityRepository.findAllByOrderByStartDatetimeDesc();
    }

    @Override
    public Activity create(Activity activity) {
        Activity activityWithMetrics = calculateMetrics(activity);
        return activityRepository.save(activityWithMetrics);
    }

    @Override
    public Iterable<Activity> getAll(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        return activityRepository.findAllByOrderByStartDatetimeDescAndUser(user);
    }

    @Override
    public Activity create(Activity activity, String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        Activity activityWithMetrics = calculateMetrics(activity);
        activityWithMetrics.setUser(user);
        return activityRepository.save(activityWithMetrics);
    }

    @Override
    public Activity getById(long id) {
        return activityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + id + " is not available."));
    }

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

    @Override
    public void delete(long id) {
        activityRepository.deleteById(id);
    }

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