package canape.benjamin.runflutterrun.dto;

import lombok.Data;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for representing a location.
 */
@Data
public class LocationDto {
    private Long id;
    private Date datetime;
    private Double latitude;
    private Double longitude;
}
