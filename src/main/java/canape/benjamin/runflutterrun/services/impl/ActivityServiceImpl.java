package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.ActivityLike;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.ActivityLikeRepository;
import canape.benjamin.runflutterrun.repositories.ActivityRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IActivityService;
import canape.benjamin.runflutterrun.services.IFriendRequestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements IActivityService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final IFriendRequestService friendRequestService;
    private final ActivityLikeRepository activityLikeRepository;

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
     * Retrieve all my activities and my friends in descending order of start datetime.
     *
     * @param token the authentication token of the user
     * @return Iterable of activities
     */
    @Override
    public Iterable<Activity> getMineAndMyFriends(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        List<User> friends = friendRequestService.getFriends(token);
        friends.add(user);
        return activityRepository.findAllByOrderByStartDatetimeDescAndUsers(friends);
    }

    /**
     * Retrieve all a user activities
     *
     * @param token the authentication token of the user
     * @param userId user id
     * @return Iterable of activities
     */
    @Override
    public Iterable<Activity> getByUser(String token, Long userId) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        if (friendRequestService.areFriends(token, userId)) {
            Optional<User> otherUser = userRepository.findUserById(userId);
            if (otherUser.isPresent()) {
                return activityRepository.findAllByOrderByStartDatetimeDescAndUser(otherUser.get());
            }
        }

        throw new SecurityException("You don't have the right to retrieve this user's activities");
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
     * @param token the authentication token of the user
     * @param id the ID of the activity
     * @return the retrieved activity
     * @throws EntityNotFoundException if the activity with the given ID is not found
     * @throws SecurityException if the activity does not belong to the authentificated user
     */
    @Override
    public Activity getById(String token, long id) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        Activity activity = activityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + id + " is not available."));

        if (activity.getUser().getId().equals(user.getId())){
            return activity;
        }

        throw new SecurityException("You don't have the right to retrieve this activity");
    }

    /**
     * Update an existing activity.
     *
     * @param token the authentication token of the user
     * @param activity the updated activity
     * @return the updated activity
     * @throws EntityNotFoundException if the activity with the given ID is not found
     * @throws SecurityException if the activity does not belong to the authentificated user
     */
    @Override
    public Activity update(String token, Activity activity) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        Activity existingActivity = activityRepository.findById(activity.getId()).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + activity.getId() + " is not available."));

        if(existingActivity.getUser().getId().equals(user.getId())) {
            Activity updatedActivity = calculateMetrics(activity);
            existingActivity.setType(updatedActivity.getType());
            existingActivity.setDistance(updatedActivity.getDistance());
            existingActivity.setStartDatetime(updatedActivity.getStartDatetime());
            existingActivity.setEndDatetime(updatedActivity.getEndDatetime());
            existingActivity.setSpeed(updatedActivity.getSpeed());

            return activityRepository.save(existingActivity);
        }

        throw new SecurityException("You don't have the right to update this activity");
    }

    /**
     * Delete an activity by its ID.
     *
     * @param token the authentication token of the user
     * @param id the ID of the activity to delete
     * @throws SecurityException if the activity does not belong to the authentificated user
     */
    @Override
    public void delete(String token, long id) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        Activity activity = getById(token, id);
        if(activity.getUser().getId().equals(user.getId())) {
            activityRepository.deleteById(id);
            return;
        }

        throw new SecurityException("You don't have the right to delete this activity");
    }

    /**
     * Like an activity
     *
     * @param id    the activity id to like
     * @param token the user's token
     */
    @Override
    public void like(Long id, String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        ActivityLike like = new ActivityLike();
        like.setActivity(activity);
        like.setUser(user);
        like.setLikeDatetime(new Date());

        activityLikeRepository.save(like);
    }

    /**
     * Dislike an activity
     *
     * @param id the activity id to dislike
     * @param token    the user's token
     */
    @Override
    public void dislike(Long id, String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        activityLikeRepository.findByActivityAndUser(activity, user)
                .ifPresent(activityLikeRepository::delete);
    }

    /**
     * Get activity like count
     *
     * @param id the activity id to count likes
     * @return likes count
     */
    @Override
    public long getActivityLikeCount(Long id) {
        return activityLikeRepository.countByActivityId(id);
    }

    /**
     * Has current user liked activity
     *
     * @param id the activity id
     * @param token    the user's token
     * @return has current user liked activity
     */
    @Override
    public boolean currentUserLiked(Long id, String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        return activityLikeRepository.findByActivityAndUser(activity, user).isPresent();
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
