package canape.benjamin.runflutterrun.dto;

import java.util.Date;

import lombok.Data;

@Data
public class LocationDto {
    private long id;
    private Date datetime;
    private Double latitude;
    private Double longitude;
}