package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.FriendRequest;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.model.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    /**
     * Retrieves the friend requests sent to the receiver user with a specific status.
     *
     * @param receiver The receiver user.
     * @param status   The status of the friend requests to search.
     * @return The friend requests sent to the receiver user with a specific status.
     */
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);

    /**
     * Retrieves the friend requests involving the user with a specific status.
     *
     * @param user   The user.
     * @param status The status of the friend requests to search.
     * @return The friend requests involving the user with a specific status.
     */
    @Query("select r from FriendRequest r where (r.sender = :user or r.receiver = :user) and r.status = :status")
    List<FriendRequest> findByUserAndStatus(@Param("user") User user, @Param("status") FriendRequestStatus status);

    /**
     * Retrieves the friend request sent between the current user and another user.
     *
     * @param user       The current user.
     * @param otherUser  The other user.
     * @return The friend request sent between the current user and another user.
     */
    @Query("select r from FriendRequest r where (r.sender = :user and r.receiver = :otherUser) or (r.receiver = :user and r.sender = :otherUser)")
    Optional<FriendRequest> findBySenderAndReceiver(@Param("user") User user, @Param("otherUser") User otherUser);
}