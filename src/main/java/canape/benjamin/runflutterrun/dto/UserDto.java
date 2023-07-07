package canape.benjamin.runflutterrun.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing user data.
 */
@Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
}
