package canape.benjamin.runflutterrun.model;

import canape.benjamin.runflutterrun.model.enums.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "friend_request")
@Getter
@Setter
@NoArgsConstructor
public class FriendRequest extends AbstractEntity {

    /**
     * The user who send the friend request
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    /**
     * The user who receive the friend request
     */
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private User receiver;

    /**
     * The current status of the friend request
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendRequestStatus status;
}
