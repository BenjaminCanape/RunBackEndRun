package canape.benjamin.runflutterrun.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing edit password data.
 */
@Data
public class EditPasswordDto {
    private String currentPassword;
    private String password;
}
