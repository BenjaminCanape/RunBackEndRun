package canape.benjamin.runflutterrun.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Activity")
@Getter
@Setter
@NoArgsConstructor
public class Activity extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public ActivityType type;

    @Column(name = "startDatetime")
    public Date startDatetime;

    @Column(name = "endDatetime")
    public Date endDatetime;

    @Column(name = "globalDistance")
    public Double distance;

    @Column(name = "speed")
    public Double speed;

    @OneToMany(targetEntity = Location.class, orphanRemoval = true, mappedBy = "activity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Location> locations;

}
