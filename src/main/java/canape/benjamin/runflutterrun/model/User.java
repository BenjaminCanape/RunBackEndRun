package canape.benjamin.runflutterrun.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.List;

@Entity
@Table(name = "sport_user")
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractEntity {
    /**
     * The firstname of the user.
     */
    @Column(name = "firstname")
    private String firstname;

    /**
     * The lastname of the user.
     */
    @Column(name = "lastname")
    private String lastname;

    /**
     * The username of the user.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * The password of the user.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The profile picture of the user.
     */
    @JdbcTypeCode(Types.VARBINARY)
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    /**
     * The list of activities associated with the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Activity> activities;

    /**
     * The refresh token associated with the user.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private RefreshToken refreshToken;

    /**
     * The friend requests send by this user
     */
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FriendRequest> sentFriendRequests;

    /**
     * The friend requests received by this user
     */
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FriendRequest> receivedFriendRequests;
}
