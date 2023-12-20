package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.ActivityComment;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.ActivityCommentRepository;
import canape.benjamin.runflutterrun.repositories.ActivityCrudRepository;
import canape.benjamin.runflutterrun.repositories.ActivityRepository;
import canape.benjamin.runflutterrun.services.IActivityCommentService;
import canape.benjamin.runflutterrun.services.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.webjars.NotFoundException;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ActivityCommentServiceImpl implements IActivityCommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityCommentServiceImpl.class);
    private static final String COMMENT_NOT_FOUND_MESSAGE = "Comment not found for ID: %d";
    private static final String COMMENT_SECURITY_EXCEPTION_MESSAGE = "You don't have the right to %s this activity comment";

    private final ActivityCrudRepository activityRepository;
    private final ActivityCommentRepository activityCommentRepository;
    private  final IUserService userService;

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
    @Transactional
    public ActivityComment create(String comment, Long activityId, String token) {
        User user = userService.getUserFromToken(token);

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        ActivityComment activityComment = new ActivityComment();
        activityComment.setActivity(activity);
        activityComment.setUser(user);
        activityComment.setCreatedAt(new Date());

        if (StringUtils.isEmpty(comment)) {
            throw new IllegalArgumentException("Comment cannot be null or empty");
        }

        activityComment.setContent(comment);
        LOGGER.info("User {} is creating a comment on activity with ID {}", user.getUsername(), activityId);

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
    @Transactional
    public ActivityComment update(Long id, String comment, String token) {
        User user = userService.getUserFromToken(token);

        Optional<ActivityComment> existingActivityComment = activityCommentRepository.findById(id);
        if (existingActivityComment.isPresent()) {
            ActivityComment commentToUpdate = existingActivityComment.get();
            if (commentToUpdate.getUser().getId().equals(user.getId())) {
                commentToUpdate.setContent(comment);
                LOGGER.info("User {} is updating a comment with ID {}", user.getUsername(), id);
                return activityCommentRepository.save(commentToUpdate);
            } else {
                throw new SecurityException(String.format(COMMENT_SECURITY_EXCEPTION_MESSAGE, "update"));
            }
        } else {
            throw new NotFoundException(String.format(COMMENT_NOT_FOUND_MESSAGE, id));
        }
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
    @Transactional
    public void delete(String token, long id) {
        User user = userService.getUserFromToken(token);

        Optional<ActivityComment> existingActivityComment = activityCommentRepository.findById(id);
        if (existingActivityComment.isPresent()) {
            ActivityComment commentToDelete = existingActivityComment.get();
            if (commentToDelete.getUser().getId().equals(user.getId())) {
                LOGGER.info("User {} is deleting a comment with ID {}", user.getUsername(), id);
                activityCommentRepository.deleteById(id);
            } else {
                throw new SecurityException(String.format(COMMENT_SECURITY_EXCEPTION_MESSAGE, "delete"));
            }
        } else {
            throw new NotFoundException(String.format(COMMENT_NOT_FOUND_MESSAGE, id));
        }
    }
}
