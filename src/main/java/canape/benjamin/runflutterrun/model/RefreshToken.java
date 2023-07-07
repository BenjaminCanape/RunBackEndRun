package canape.benjamin.runflutterrun.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity()
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends AbstractEntity {

    /**
     * The user associated with the refresh token.
     */
    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * The refresh token string.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * The expiry date of the refresh token.
     */
    @Column(nullable = false)
    private Instant expiryDate;
}
