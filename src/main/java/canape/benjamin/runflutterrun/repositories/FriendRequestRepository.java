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
     * Retrieves the friend request send to the receiver user which have a specific status
     *
     * @param receiver The receiver user.
     * @param status The status of the friend request we search.
     * @return The friend request send to the receiver user which have a specific status.
     */
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);
    /**
     * Retrieves the friend request send to the receiver user by the sender user
     *
     * @param user The current user.
     * @param otherUser The other user.
     * @return the friend request send to the receiver user by the sender user.
     */
    @Query("select r from FriendRequest r where (r.sender = :user and r.receiver = :otherUser) or (r.receiver = :user and r.sender = :otherUser)")
    Optional<FriendRequest> findBySenderAndReceiver(@Param("user") User user, @Param("otherUser") User otherUser);

}