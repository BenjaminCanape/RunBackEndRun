package canape.benjamin.runflutterrun.service.impl;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repository.ActivityRepository;
import canape.benjamin.runflutterrun.repository.UserRepository;
import canape.benjamin.runflutterrun.security.JwtUtils;
import canape.benjamin.runflutterrun.service.IActivityService;
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
        User u = userRepository.findByUsername(username);
        return activityRepository.findAllByOrderByStartDatetimeDescAndUser(u);
    }

    @Override
    public Activity create(Activity activity, String token){
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User u = userRepository.findByUsername(username);
        Activity activityWithMetrics = calculateMetrics(activity);
        activityWithMetrics.setUser(u);
        return activityRepository.save(activityWithMetrics);
    }

    @Override
    public Activity getById(long id) {
        return activityRepository.findActivityById(id).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + id + ", not available."));
    }

    @Override
    public Activity update(Activity activity) {
        Activity finalActivity = activity;
        Activity activity1 = activityRepository.findActivityById(activity.getId()).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + finalActivity.getId() + ", not available."));
        activity = calculateMetrics(activity);
        activity1.setType(activity.getType());
        activity1.setDistance(activity.getDistance());
        activity1.setStartDatetime(activity.getStartDatetime());
        activity1.setEndDatetime(activity.getEndDatetime());
        activity1.setSpeed(activity.getSpeed());
        return activityRepository.save(activity1);
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
            double ms = (double) time / 3600000.0;
            speed = activity.getDistance() / ms;
        }
        activity.setSpeed(speed);
        return activity;
    }
}