package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.ActivityComment;
import canape.benjamin.runflutterrun.model.ActivityLike;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.ActivityCommentRepository;
import canape.benjamin.runflutterrun.repositories.ActivityLikeRepository;
import canape.benjamin.runflutterrun.repositories.ActivityRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IActivityCommentService;
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
public class ActivityCommentServiceImpl implements IActivityCommentService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final ActivityCommentRepository activityCommentRepository;

    /**
     * Create a new comment on activity by current user
     *
     * @param comment the comment
     * @param activityId the activity Id
     * @param token the authentication token of the user
     * @return the created activity comment
     * @throws NotFoundException if the activity with the given ID is not found
     */
    @Override
    public ActivityComment create(String comment, Long activityId, String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        Optional<Activity> activity= activityRepository.findById(activityId);

        if (activity.isPresent()) {
            ActivityComment activityComment = new ActivityComment();
            activityComment.setActivity(activity.get());
            activityComment.setUser(user);
            activityComment.setCreatedAt(new Date());
            activityComment.setContent(comment);
            return activityCommentRepository.save(activityComment);
        }
        throw new NotFoundException("Activity not found");
    }

    /**
     * Update an existing activity comment.
     *
     * @param id the comment id
     * @param token the authentication token of the user
     * @return the updated activity comment
     * @throws EntityNotFoundException if the activity comment with the given ID is not found
     * @throws SecurityException if the activity comment does not belong to the authentificated user
     */
    @Override
    public ActivityComment update(Long id, String comment, String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        ActivityComment existingActivityComment = activityCommentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Comment with id: " + id + " is not available."));

        if(existingActivityComment.getUser().getId().equals(user.getId())) {
            existingActivityComment.setContent(comment);
            return activityCommentRepository.save(existingActivityComment);
        }

        throw new SecurityException("You don't have the right to update this activity");
    }

    /**
     * Delete an activity by its ID.
     *
     * @param token the authentication token of the user
     * @param id the ID of the activity to delete
     * @throws SecurityException if the activity does not belong to the authentificated user
     * @throws NotFoundException if the activity comment with the given ID is not found
     */
    @Override
    public void delete(String token, long id) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        Optional<ActivityComment> activityComment = activityCommentRepository.findById(id);

        if (activityComment.isPresent()) {
            if (activityComment.get().getUser().getId().equals(user.getId())) {
                activityCommentRepository.deleteById(id);
                return;
            }

            throw new SecurityException("You don't have the right to delete this activity");
        }

        throw  new NotFoundException("Comment not found");
    }

}
