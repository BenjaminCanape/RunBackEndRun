package canape.benjamin.runflutterrun.dto;

import canape.benjamin.runflutterrun.model.enums.ActivityType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ActivityDto {
    private Long id;
    private ActivityType type;
    private Date startDatetime;
    private Date endDatetime;
    private Double distance;
    private Double speed;
    private long time;
    private List<LocationDto> locations;
}