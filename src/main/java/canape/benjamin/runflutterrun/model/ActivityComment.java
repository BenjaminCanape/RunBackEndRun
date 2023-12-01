package canape.benjamin.runflutterrun.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "activity_comment")
@Getter
@Setter
@NoArgsConstructor
public class ActivityComment extends AbstractEntity {

    /**
     * The user who wrote the comment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The activity associated with the comment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    /**
     * The content of the comment.
     */
    @Column(name = "content")
    private String content;

    /**
     * The timestamp when the comment was created.
     */
    @Column(name = "created_at")
    private Date createdAt;
}
