package canape.benjamin.runflutterrun.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "sport_user")
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractEntity {

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
     * The list of activities associated with the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Activity> activities;

    /**
     * The refresh token associated with the user.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private RefreshToken refreshToken;
}
