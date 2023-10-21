package canape.benjamin.runflutterrun.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing user data.
 */
@Data
public class UserDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
}
