package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.FriendRequest;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.model.enums.FriendRequestStatus;
import canape.benjamin.runflutterrun.repositories.FriendRequestCrudRepository;
import canape.benjamin.runflutterrun.repositories.FriendRequestRepository;
import canape.benjamin.runflutterrun.services.IFriendRequestService;
import canape.benjamin.runflutterrun.services.IUserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This service implementation class provides methods for managing friend requests, including sending, accepting, rejecting, and canceling them,
 * as well as retrieving pending friend requests and specific friend requests for a user.
 */
@Service
@AllArgsConstructor
public class FriendRequestServiceImpl implements IFriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRequestCrudRepository friendRequestCrudRepository;
    private final IUserService userService;

    /**
     * Retrieves a list of pending friend requests for the user associated with the given token.
     *
     * @param token The authorization token of the user.
     * @param pageable the pagination information
     * @return A list of FriendRequest objects representing pending friend requests.
     */
    public Page<FriendRequest> getPendingFriendRequests(String token, Pageable pageable) {
        User user = userService.getUserFromToken(token);
        return friendRequestRepository.findByReceiverAndStatus(user, FriendRequestStatus.PENDING, pageable);
    }

    /**
     * Retrieves a specific friend request sent to the user associated with the given token and the specified userId.
     *
     * @param token  The authorization token of the user.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @return An Optional containing the FriendRequest object if it exists, or an empty Optional if not.
     * @throws EntityNotFoundException if the specified user with userId does not exist.
     */
    public Optional<FriendRequest> getFriendRequestForUser(String token, Long userId) {
        User user = userService.getUserFromToken(token);
        User otherUser = userService.getUserById(userId);
        return friendRequestCrudRepository.findBySenderAndReceiver(user, otherUser);
    }

    /**
     * Sends a friend request from the user associated with the given token to the user with the specified receiverId.
     *
     * @param token      The authorization token of the user sending the request.
     * @param receiverId The ID of the user to whom the friend request is being sent.
     * @return The created FriendRequest object.
     * @throws EntityNotFoundException    if the specified user with receiverId does not exist.
     * @throws EntityExistsException      if a friend request already exists between the sender and receiver.
     */
    public FriendRequest sendFriendRequest(String token, Long receiverId) {
        User sender = userService.getUserFromToken(token);
        User receiver = userService.getUserById(receiverId);

        FriendRequest existingFriendRequest = friendRequestCrudRepository.findBySenderAndReceiver(sender, receiver)
                .orElseGet(() -> createFriendRequest(sender, receiver));

        existingFriendRequest.setStatus(FriendRequestStatus.PENDING);
        return friendRequestCrudRepository.save(existingFriendRequest);
    }

    /**
     * Accepts a specific friend request with the given requestId.
     *
     * @param token    The authorization token of the user accepting the request.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @return The accepted FriendRequest object.
     * @throws EntityNotFoundException    if the specified friend request with requestId does not exist.
     * @throws SecurityException          if the authenticated user is not the receiver of the friend request.
     */
    public FriendRequest acceptFriendRequest(String token, Long userId) {
        return updateFriendRequestStatus(token, userId, FriendRequestStatus.ACCEPTED);
    }

    /**
     * Rejects a specific friend request with the given requestId.
     *
     * @param token    The authorization token of the user rejecting the request.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @return The rejected FriendRequest object.
     * @throws EntityNotFoundException    if the specified friend request with requestId does not exist.
     * @throws SecurityException          if the authenticated user is not the receiver of the friend request.
     */
    public FriendRequest rejectFriendRequest(String token, Long userId) {
        return updateFriendRequestStatus(token, userId, FriendRequestStatus.REJECTED);
    }

    /**
     * Cancels a specific friend request sent by the user associated with the given token.
     *
     * @param token    The authorization token of the user canceling the request.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @return The canceled FriendRequest object.
     * @throws EntityNotFoundException    if the specified friend request with requestId does not exist.
     * @throws SecurityException          if the authenticated user is not the sender of the friend request.
     */
    public FriendRequest cancelFriendRequest(String token, Long userId) {
        return updateFriendRequestStatus(token, userId, FriendRequestStatus.CANCELED);
    }


    /**
     * checks if the two users are friends
     *
     * @param token the current user token
     * @param userId The other user id
     * @return true if they are friends, else false
     */
    public boolean areFriends(String token, Long userId) {
        User user = userService.getUserFromToken(token);
        User receiver = userService.getUserById(userId);

        Optional<FriendRequest> existingFriendRequest = friendRequestCrudRepository.findBySenderAndReceiver(user, receiver);

        return existingFriendRequest.isPresent() && existingFriendRequest.get().getStatus() == FriendRequestStatus.ACCEPTED;
    }

    /**
     * get the friends of the current user
     *
     * @param token the current user token
     * @return list of user
     */
    public List<User> getFriends(String token) {
        User user = userService.getUserFromToken(token);

        List<FriendRequest> friendRequests = friendRequestCrudRepository.findByUserAndStatus(user, FriendRequestStatus.ACCEPTED);

        List<User> friends = new ArrayList<>();
        for (FriendRequest item : friendRequests) {
            if (item.getSender().equals(user)) {
                friends.add(item.getReceiver());
            } else {
                friends.add(item.getSender());
            }
        }
        return friends;
    }

    /**
     * Updates the status of a specific friend request with the given requestId.
     *
     * @param sender the user who want to ask a friend request
     * @param receiver the user who will receive the friend request
     * @return the friend requested created
     */
    private FriendRequest createFriendRequest(User sender, User receiver) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        return friendRequest;
    }

    /**
     * Updates the status of a specific friend request with the given requestId.
     *
     * @param token    The authorization token of the user performing the update.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @param status   The new status of the friend request (Accepted, Rejected, or Canceled).
     * @return The updated FriendRequest object.
     * @throws EntityNotFoundException    if the specified friend request with requestId does not exist.
     * @throws SecurityException          if the authenticated user is not authorized to update the friend request.
     */
    private FriendRequest updateFriendRequestStatus(String token, Long userId, FriendRequestStatus status) {
        User user = userService.getUserFromToken(token);
        User otherUser = userService.getUserById(userId);

        Optional<FriendRequest> existingFriendRequest = friendRequestCrudRepository.findBySenderAndReceiver(user, otherUser);

        if (existingFriendRequest.isEmpty()) {
            throw new EntityNotFoundException("Friend request doesn't exist.");
        }

        FriendRequest friendRequest = existingFriendRequest.get();

        if (!friendRequest.getReceiver().equals(user) && !friendRequest.getSender().equals(user)) {
            throw new SecurityException("Friend request doesn't concern you.");
        }

        friendRequest.setStatus(status);
        return friendRequestCrudRepository.save(friendRequest);
    }
}