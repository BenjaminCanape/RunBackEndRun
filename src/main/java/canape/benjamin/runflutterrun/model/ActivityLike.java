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
public class ActivityLike extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "activity_id", referencedColumnName = "id")
    private Activity activity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "like_datetime")
    private Date likeDatetime;
}
