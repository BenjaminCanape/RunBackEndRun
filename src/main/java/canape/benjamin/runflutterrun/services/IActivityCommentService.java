package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.ActivityComment;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
public interface IActivityCommentService {

    /**
     * Create a new comment on activity by current user
     *
     * @param comment the comment
     * @param activityId the activity Id
     * @param token the authentication token of the user
     * @return the created activity comment
     * @throws NotFoundException if the activity with the given ID is not found
     */
    ActivityComment create(String comment, Long activityId, String token);

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
    ActivityComment update(Long id, String comment, String token);

    /**
     * Delete an activity comment by its ID.
     *
     * @param token the authentication token of the user
     * @param id the ID of the activity comment to delete
     * @throws SecurityException if the activity comment does not belong to the authentificated user
     * @throws NotFoundException if the activity comment with the given ID is not found
     */
    void delete(String token, long id);
}
