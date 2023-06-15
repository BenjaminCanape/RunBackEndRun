package canape.benjamin.runflutterrun.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import canape.benjamin.runflutterrun.model.ActivityType;
import lombok.Data;

@Data
public class ActivityDto {
    private long id;
    private ActivityType type;
    private Date startDatetime;
    private Date endDatetime;
    private Double distance;

    private Double speed;

    private long time;

    private List<LocationDto> locations;
}