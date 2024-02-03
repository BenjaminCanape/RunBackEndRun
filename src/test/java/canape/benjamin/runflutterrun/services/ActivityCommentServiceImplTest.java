package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.ActivityComment;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.ActivityCommentRepository;
import canape.benjamin.runflutterrun.repositories.ActivityCrudRepository;
import canape.benjamin.runflutterrun.repositories.ActivityRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.impl.ActivityCommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityCommentServiceImplTest {

    @Mock
    private IUserService userService;

    @Mock
    private ActivityCrudRepository activityRepository;

    @Mock
    private ActivityCommentRepository activityCommentRepository;

    @InjectMocks
    private ActivityCommentServiceImpl activityCommentService;

    // Initialization of mocks
    public ActivityCommentServiceImplTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createComment_Success() {
        // Arrange
        String commentText = "Test comment";
        Long activityId = 1L;
        String token = "validToken";

        User mockUser = new User(); // Create a mock user
        Activity mockActivity = new Activity(); // Create a mock activity
        when(userService.getUserFromToken(token)).thenReturn(mockUser);
        when(activityRepository.findById(activityId)).thenReturn(Optional.of(mockActivity));
        when(activityCommentRepository.save(any(ActivityComment.class))).thenAnswer(invocation -> {
            ActivityComment createdComment = invocation.getArgument(0);
            createdComment.setId(1L); // Set a mock ID
            return createdComment;
        });

        // Act
        ActivityComment createdComment = activityCommentService.create(commentText, activityId, token);

        // Assert
        assertNotNull(createdComment);
        assertEquals(commentText, createdComment.getContent());
        assertEquals(mockUser, createdComment.getUser());
        assertEquals(mockActivity, createdComment.getActivity());
        assertNotNull(createdComment.getCreatedAt());
        verify(activityCommentRepository, times(1)).save(any(ActivityComment.class));
    }

    @Test
    void updateComment_Success() {
        // Arrange
        Long commentId = 1L;
        String updatedCommentText = "Updated comment";
        String token = "validToken";

        User mockUser = new User(); // Create a mock user
        mockUser.setId(1L);
        ActivityComment existingComment = new ActivityComment(); // Create a mock existing comment
        existingComment.setUser(mockUser);

        when(userService.getUserFromToken(token)).thenReturn(mockUser);
        when(activityCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(activityCommentRepository.save(any(ActivityComment.class))).thenAnswer(invocation -> {
            ActivityComment updatedComment = invocation.getArgument(0);
            return updatedComment;
        });

        // Act
        ActivityComment updatedComment = activityCommentService.update(commentId, updatedCommentText, token);

        // Assert
        assertNotNull(updatedComment);
        assertEquals(updatedCommentText, updatedComment.getContent());
        assertEquals(mockUser, updatedComment.getUser());
        verify(activityCommentRepository, times(1)).save(any(ActivityComment.class));
    }

    @Test
    void deleteComment_Success() {
        // Arrange
        Long commentId = 1L;
        String token = "validToken";

        User mockUser = new User(); // Create a mock user
        mockUser.setId(1L);
        ActivityComment existingComment = new ActivityComment(); // Create a mock existing comment
        existingComment.setUser(mockUser);

        when(userService.getUserFromToken(token)).thenReturn(mockUser);
        when(activityCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // Act
        assertDoesNotThrow(() -> activityCommentService.delete(token, commentId));

        // Assert
        verify(activityCommentRepository, times(1)).deleteById(commentId);
    }
}
