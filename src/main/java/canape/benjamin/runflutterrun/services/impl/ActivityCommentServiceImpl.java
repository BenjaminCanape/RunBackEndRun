package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.ActivityComment;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.ActivityCommentRepository;
import canape.benjamin.runflutterrun.repositories.ActivityRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IActivityCommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Date;

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

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        ActivityComment activityComment = new ActivityComment();
        activityComment.setActivity(activity);
        activityComment.setUser(user);
        activityComment.setCreatedAt(new Date());
        activityComment.setContent(comment);
        return activityCommentRepository.save(activityComment);
    }

    /**
     * Update an existing activity comment.
     *
     * @param id the comment id
     * @param comment the comment string
     * @param token the authentication token of the user
     * @return the updated activity comment
     * @throws EntityNotFoundException if the activity comment with the given ID is not found
     * @throws SecurityException if the activity comment does not belong to the authentificated user
     */
    @Override
    public ActivityComment update(Long id, String comment, String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        ActivityComment existingActivityComment = activityCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id: " + id + " is not available."));

        if (existingActivityComment.getUser().getId().equals(user.getId())) {
            existingActivityComment.setContent(comment);
            return activityCommentRepository.save(existingActivityComment);
        }

        throw new SecurityException("You don't have the right to update this activity comment");
    }

    /**
     * Delete an activity comment by its ID.
     *
     * @param token the authentication token of the user
     * @param id the ID of the activity comment to delete
     * @throws SecurityException if the activity comment does not belong to the authentificated user
     * @throws NotFoundException if the activity comment with the given ID is not found
     */
    @Override
    public void delete(String token, long id) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        ActivityComment activityComment = activityCommentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (activityComment.getUser().getId().equals(user.getId())) {
            activityCommentRepository.deleteById(id);
        } else {
            throw new SecurityException("You don't have the right to delete this activity comment");
        }
    }
}
