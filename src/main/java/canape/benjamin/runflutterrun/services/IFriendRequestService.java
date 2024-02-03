package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.FriendRequest;
import canape.benjamin.runflutterrun.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This service interface defines methods for managing friend requests, including sending, accepting, rejecting, and canceling them,
 * as well as retrieving pending friend requests and specific friend requests for a user.
 */
@Service
public interface IFriendRequestService {

    /**
     * Retrieves a list of pending friend requests for the user associated with the given token.
     *
     * @param token The authorization token of the user.
     * @return A list of FriendRequest objects representing pending friend requests.
     */
    Page<FriendRequest> getPendingFriendRequests(String token, Pageable pageable) ;

    /**
     * Retrieves a specific friend request sent to the user associated with the given token and the specified userId.
     *
     * @param token  The authorization token of the user.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @return An Optional containing the FriendRequest object if it exists, or an empty Optional if not.
     */
    Optional<FriendRequest> getFriendRequestForUser(String token, Long userId);

    /**
     * Sends a friend request from the user associated with the given token to the user with the specified receiverId.
     *
     * @param token      The authorization token of the user sending the request.
     * @param receiverId The ID of the user to whom the friend request is being sent.
     * @return The created FriendRequest object.
     */
    FriendRequest sendFriendRequest(String token, Long receiverId);

    /**
     * Accepts a specific friend request with the given requestId.
     *
     * @param token    The authorization token of the user accepting the request.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @return The accepted FriendRequest object.
     */
    FriendRequest acceptFriendRequest(String token, Long userId);

    /**
     * Rejects a specific friend request with the given requestId.
     *
     * @param token    The authorization token of the user rejecting the request.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @return The rejected FriendRequest object.
     */
    FriendRequest rejectFriendRequest(String token, Long userId);

    /**
     * Cancels a specific friend request sent by the user associated with the given token.
     *
     * @param token    The authorization token of the user canceling the request.
     * @param userId The ID of the user for whom the friend request is being retrieved.
     * @return The canceled FriendRequest object.
     */
    FriendRequest cancelFriendRequest(String token, Long userId);


    /**
     * checks if the two users are friends
     *
     * @param token the current user token
     * @param userId The other user id
     * @return true if they are friends, else false
     */
    boolean areFriends(String token, Long userId);

    /**
     * get the friends of the current user
     *
     * @param token the current user token
     * @return list of user
     */
    List<User> getFriends(String token);
}

