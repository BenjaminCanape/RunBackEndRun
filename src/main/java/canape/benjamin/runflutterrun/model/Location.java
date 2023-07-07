package canape.benjamin.runflutterrun.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
public class Location extends AbstractEntity {

    /**
     * The activity associated with the location.
     */
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    /**
     * The datetime of the location.
     */
    @Column(name = "datetime")
    private Date datetime;

    /**
     * The latitude coordinate of the location.
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * The longitude coordinate of the location.
     */
    @Column(name = "longitude")
    private Double longitude;
}
