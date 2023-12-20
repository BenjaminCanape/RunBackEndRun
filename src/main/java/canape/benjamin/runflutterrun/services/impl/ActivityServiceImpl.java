package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.ActivityLike;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.ActivityCrudRepository;
import canape.benjamin.runflutterrun.repositories.ActivityLikeRepository;
import canape.benjamin.runflutterrun.repositories.ActivityRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.services.IActivityService;
import canape.benjamin.runflutterrun.services.IFriendRequestService;
import canape.benjamin.runflutterrun.services.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements IActivityService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ActivityCrudRepository activityCrudRepository;
    private final IFriendRequestService friendRequestService;
    private final ActivityLikeRepository activityLikeRepository;
    private final IUserService userService;

    @Override
    public Iterable<Activity> getAll() {
        return null;
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
        return activityCrudRepository.save(activityWithMetrics);
    }

    /**
     * Retrieve all activities for a specific user in descending order of start datetime.
     *
     * @param token the authentication token of the user
     * @param pageable the pagination information
     * @return Iterable of activities
     */
    @Override
    public Page<Activity> getAll(String token, Pageable pageable) {
        User user = userService.getUserFromToken(token);
        return activityRepository.findAllByOrderByStartDatetimeDescAndUser(user, pageable);
    }

    /**
     * Retrieve all my activities and my friends in descending order of start datetime.
     *
     * @param token the authentication token of the user
     * @param pageable the pagination information
     * @return Iterable of activities
     */
    @Override
    public Page<Activity> getMineAndMyFriends(String token, Pageable pageable) {
        User user = userService.getUserFromToken(token);
        List<User> friends = friendRequestService.getFriends(token);
        friends.add(user);
        return activityRepository.findAllByOrderByStartDatetimeDescAndUsers(friends, pageable);
    }

    /**
     * Retrieve all a user activities
     *
     * @param token the authentication token of the user
     * @param userId user id
     * @param pageable the pagination information
     * @return Iterable of activities
     */
    @Override
    public Page<Activity> getByUser(String token, Long userId, Pageable pageable) {
        if (friendRequestService.areFriends(token, userId)) {
            Optional<User> otherUser = userRepository.findById(userId);
            if (otherUser.isPresent()) {
                return activityRepository.findAllByOrderByStartDatetimeDescAndUser(otherUser.get(), pageable);
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
        User user = userService.getUserFromToken(token);
        Activity activityWithMetrics = calculateMetrics(activity);
        activityWithMetrics.setUser(user);
        return activityCrudRepository.save(activityWithMetrics);
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
        User user = userService.getUserFromToken(token);
        Activity activity = activityCrudRepository.findById(id).orElseThrow(
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
        User user = userService.getUserFromToken(token);

        Activity existingActivity = activityCrudRepository.findById(activity.getId()).orElseThrow(
                () -> new EntityNotFoundException("Activity with id: " + activity.getId() + " is not available."));

        if(existingActivity.getUser().getId().equals(user.getId())) {
            Activity updatedActivity = calculateMetrics(activity);
            existingActivity.setType(updatedActivity.getType());
            existingActivity.setDistance(updatedActivity.getDistance());
            existingActivity.setStartDatetime(updatedActivity.getStartDatetime());
            existingActivity.setEndDatetime(updatedActivity.getEndDatetime());
            existingActivity.setSpeed(updatedActivity.getSpeed());

            return activityCrudRepository.save(existingActivity);
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
        User user = userService.getUserFromToken(token);

        Activity activity = getById(token, id);
        if(activity.getUser().getId().equals(user.getId())) {
            activityCrudRepository.deleteById(id);
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
        User user = userService.getUserFromToken(token);

        Activity activity = activityCrudRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        Optional<ActivityLike> optionalActivityLike = activityLikeRepository.findByActivityAndUser(activity, user);

        if (optionalActivityLike.isEmpty()) {
            ActivityLike like = new ActivityLike();
            like.setActivity(activity);
            like.setUser(user);
            like.setLikeDatetime(new Date());
            activityLikeRepository.save(like);
        }
    }

    /**
     * Dislike an activity
     *
     * @param id the activity id to dislike
     * @param token    the user's token
     */
    @Override
    public void dislike(Long id, String token) {
        User user = userService.getUserFromToken(token);

        Activity activity = activityCrudRepository.findById(id)
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
        User user = userService.getUserFromToken(token);

        Activity activity = activityCrudRepository.findById(id)
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
