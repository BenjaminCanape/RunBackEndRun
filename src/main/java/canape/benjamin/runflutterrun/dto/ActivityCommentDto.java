package canape.benjamin.runflutterrun.dto;

import lombok.Data;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for representing an activity comment.
 */
@Data
public class ActivityCommentDto {
    private Long id;
    private UserSearchDto user;
    private Date createdAt;
    private String content;
}
