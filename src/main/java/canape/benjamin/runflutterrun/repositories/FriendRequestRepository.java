package canape.benjamin.runflutterrun.repositories;

import canape.benjamin.runflutterrun.model.FriendRequest;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.model.enums.FriendRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FriendRequestRepository extends PagingAndSortingRepository<FriendRequest, Long> {
    /**
     * Retrieves the friend requests sent to the receiver user with a specific status.
     *
     * @param receiver The receiver user.
     * @param status   The status of the friend requests to search.
     * @return The friend requests sent to the receiver user with a specific status.
     */
    Page<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status, Pageable pageable);
}