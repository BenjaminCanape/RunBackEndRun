package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.FriendRequest;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.model.enums.FriendRequestStatus;
import canape.benjamin.runflutterrun.repositories.FriendRequestRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.impl.FriendRequestServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FriendRequestServiceImplTest {

    private FriendRequestServiceImpl friendRequestService;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        friendRequestService = new FriendRequestServiceImpl(friendRequestRepository, jwtUtils, userRepository);
    }

    @Test
    public void testGetPendingFriendRequests() {
        // Arrange
        String token = "validToken";
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(friendRequestRepository.findByReceiverAndStatus(user, FriendRequestStatus.PENDING)).thenReturn(
                List.of(
                        createFriendRequest(1L, user, createUser(2L, "user2"), FriendRequestStatus.PENDING),
                        createFriendRequest(2L, user, createUser(3L, "user3"), FriendRequestStatus.PENDING)
                )
        );

        // Act
        List<FriendRequest> pendingRequests = friendRequestService.getPendingFriendRequests(token);

        // Assert
        assertEquals(2, pendingRequests.size());
    }

    @Test
    public void testGetFriendRequestForUser() {
        // Arrange
        String token = "validToken";
        Long userId = 2L;
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        User otherUser = createUser(userId, "user2");
        when(userRepository.findById(userId)).thenReturn(Optional.of(otherUser));

        FriendRequest friendRequest = createFriendRequest(1L, user, otherUser, FriendRequestStatus.PENDING);
        when(friendRequestRepository.findBySenderAndReceiver(user, otherUser)).thenReturn(Optional.of(friendRequest));

        // Act
        Optional<FriendRequest> request = friendRequestService.getFriendRequestForUser(token, userId);

        // Assert
        assertTrue(request.isPresent());
        assertEquals(userId, request.get().getReceiver().getId());
    }

    @Test
    public void testSendFriendRequest() {
        // Arrange
        String token = "validToken";
        Long receiverId = 2L;
        User sender = createUser(1L, "sender");
        User receiver = createUser(receiverId, "receiver");
        FriendRequest request = new FriendRequest();
        request.setStatus(FriendRequestStatus.PENDING);
        request.setReceiver(receiver);
        request.setSender(sender);

        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(sender.getUsername());
        when(userRepository.findByUsername(sender.getUsername())).thenReturn(sender);
        when(userRepository.findUserById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.empty());
        when(friendRequestRepository.save(any(FriendRequest.class))).thenReturn(request);

        // Act
        FriendRequest sentRequest = friendRequestService.sendFriendRequest(token, receiverId);

        // Assert
        assertNotNull(sentRequest);
        assertEquals(sender, sentRequest.getSender());
        assertEquals(receiver, sentRequest.getReceiver());
        assertEquals(FriendRequestStatus.PENDING, sentRequest.getStatus());
    }

    @Test
    public void testSendFriendRequestWhenReceiverNotFound() {
        // Arrange
        String token = "validToken";
        Long receiverId = 2L;
        User sender = createUser(1L, "sender");

        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(sender.getUsername());
        when(userRepository.findByUsername(sender.getUsername())).thenReturn(sender);
        when(userRepository.findUserById(receiverId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> friendRequestService.sendFriendRequest(token, receiverId));
    }

    @Test
    public void testSendFriendRequestWhenFriendRequestExists() {
        // Arrange
        String token = "validToken";
        Long receiverId = 2L;
        User sender = createUser(1L, "sender");
        User receiver = createUser(receiverId, "receiver");
        FriendRequest request = new FriendRequest();
        request.setStatus(FriendRequestStatus.PENDING);
        request.setReceiver(receiver);
        request.setSender(sender);

        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(sender.getUsername());
        when(userRepository.findByUsername(sender.getUsername())).thenReturn(sender);
        when(userRepository.findUserById(receiverId)).thenReturn(Optional.of(receiver));
        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(
                Optional.of(request)
        );
        when(friendRequestRepository.save(any(FriendRequest.class))).thenReturn(request);

        // Act
        FriendRequest sentRequest = friendRequestService.sendFriendRequest(token, receiverId);

        // Assert
        assertNotNull(request);
        assertEquals(sender, request.getSender());
        assertEquals(receiver, request.getReceiver());
        assertEquals(FriendRequestStatus.PENDING, request.getStatus());
    }

    // Implement other test methods similarly

    private FriendRequest createFriendRequest(Long id, User sender, User receiver, FriendRequestStatus status) {
        FriendRequest request = new FriendRequest();
        request.setId(id);
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(status);
        return request;
    }

    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}
