package canape.benjamin.runflutterrun.service.impl;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.repository.ActivityRepository;
import canape.benjamin.runflutterrun.service.IActivityService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ActivityServiceImpl implements IActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public Iterable<Activity> getAll() {
        return activityRepository.findAll();
    }

    @Override
    public Activity create(Activity activity) {
        Activity activityWithMetrics = calculateMetrics(activity);
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

    private Activity calculateMetrics(Activity activity){
        Date start = activity.getStartDatetime();
        Date end = activity.getEndDatetime();

        long time = end.getTime() - start.getTime();
        Double ms = Double.valueOf(time / 3600000);
        Double speed = activity.getDistance() / ms;

        activity.setSpeed(speed);
        return activity;
    }
}