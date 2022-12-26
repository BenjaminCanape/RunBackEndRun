package canape.benjamin.runflutterrun.dto;

import java.util.Date;
import java.util.Set;

import lombok.Data;

@Data
public class ActivityDto {
    private long id;
    private Date startDatetime;
    private Date endDatetime;
    private long distance;

    private long speed;

    private long time;

    private Set<LocationDto> locations;
}