package canape.benjamin.runflutterrun.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "activity_like")
@Getter
@Setter
@NoArgsConstructor
public class ActivityLike {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "like_datetime")
    private Date likeDatetime;
}
