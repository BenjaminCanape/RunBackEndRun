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
    public Activity getById(int id) {
        return activityRepository.findActivityById(id).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + id + ", not available."));
    }

    @Override
    public Activity update(Activity activity) {
        Activity activity1 = activityRepository.findActivityById(activity.getId()).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + activity.getId() + ", not available."));
        return activityRepository.save(activity1);
    }

    @Override
    public void delete(int id) {
        activityRepository.deleteById(id);
    }

    private Activity calculateMetrics(Activity activity){
        Date start = activity.startDatetime;
        Date end = activity.endDatetime;

        long time = Math.abs(end.getTime() - start.getTime());
        Double speed = activity.distance / (time / 3600000);

        activity.setSpeed(speed);
        return activity;
    }
}