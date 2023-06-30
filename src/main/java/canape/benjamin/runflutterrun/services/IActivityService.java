package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.Activity;
import org.springframework.stereotype.Service;

@Service
public interface IActivityService extends ICrudService<Activity> {
    Iterable<Activity> getAll(String token);

    Activity create(Activity activity, String token);
}