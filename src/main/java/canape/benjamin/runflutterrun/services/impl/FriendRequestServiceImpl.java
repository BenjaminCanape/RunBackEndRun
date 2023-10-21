package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.model.FriendRequest;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.model.enums.FriendRequestStatus;
import canape.benjamin.runflutterrun.repositories.FriendRequestRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IFriendRequestService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
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
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    /**
     * Retrieves a list of pending friend requests for the user associated with the given token.
     *
     * @param token The authorization token of the user.
     * @return A list of FriendRequest objects representing pending friend requests.
     */
    public List<FriendRequest> getPendingFriendRequests(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        return friendRequestRepository.findByReceiverAndStatus(user, FriendRequestStatus.PENDING);
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
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        Optional<User> otherUser = userRepository.findById(userId);
        if (otherUser.isEmpty()) {
            throw new EntityNotFoundException("No user with id: " + userId.toString());
        }
        return friendRequestRepository.findBySenderAndReceiver(user, otherUser.get());
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
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        User receiver = userRepository.findUserById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("No user with id: " + receiverId));

        FriendRequest existingFriendRequest = friendRequestRepository.findBySenderAndReceiver(user, receiver)
                .orElseGet(() -> {
                    FriendRequest friendRequest = new FriendRequest();
                    friendRequest.setSender(user);
                    friendRequest.setReceiver(receiver);
                    friendRequest.setStatus(FriendRequestStatus.PENDING);
                    return friendRequest;
                });
        existingFriendRequest.setSender(user);
        existingFriendRequest.setReceiver(receiver);
        existingFriendRequest.setStatus(FriendRequestStatus.PENDING);
        return friendRequestRepository.save(existingFriendRequest);
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
        return this.updateFriendRequest(token, userId, FriendRequestStatus.ACCEPTED);
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
        return this.updateFriendRequest(token, userId, FriendRequestStatus.REJECTED);
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
        return this.updateFriendRequest(token, userId, FriendRequestStatus.CANCELED);
    }


    /**
     * checks if the two users are friends
     *
     * @param token the current user token
     * @param userId The other user id
     * @return true if they are friends, else false
     */
    public boolean areFriends(String token, Long userId) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        Optional<User> receiver = userRepository.findUserById(userId);

        if (receiver.isPresent()) {
            Optional<FriendRequest> existingFriendRequest = friendRequestRepository.findBySenderAndReceiver(user, receiver.get());

            if (existingFriendRequest.isPresent()) {
                return existingFriendRequest.get().getStatus() == FriendRequestStatus.ACCEPTED;
            }
        }

        return false;
    }

    /**
     * get the friends of the current user
     *
     * @param token the current user token
     * @return list of user
     */
    public List<User> getFriends(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

        List<FriendRequest> friendRequests = friendRequestRepository.findByUserAndStatus(user, FriendRequestStatus.ACCEPTED);

        List<User> friends = new ArrayList<>();
        for (FriendRequest item : friendRequests) {
            if (item.getSender() == user) {
                friends.add(item.getReceiver());
            }
            else {
                friends.add(item.getSender());
            }
        }
        return friends;
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
    private FriendRequest updateFriendRequest(String token, Long userId, FriendRequestStatus status) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        Optional<User> otherUser = userRepository.findById(userId);
        if (otherUser.isEmpty()) {
            throw new EntityNotFoundException("No user with id: " + userId);
        }

        Optional<FriendRequest> existingFriendRequest = friendRequestRepository.findBySenderAndReceiver(user, otherUser.get());

        if (existingFriendRequest.isEmpty()) {
            throw new EntityNotFoundException("Friend request doesn't exist.");
        }

        FriendRequest friendRequest = existingFriendRequest.get();

        if (!friendRequest.getReceiver().getId().equals(user.getId())) {
            throw new SecurityException("Friend request doesn't concern you.");
        }

        friendRequest.setStatus(status);
        return friendRequestRepository.save(friendRequest);
    }
}
