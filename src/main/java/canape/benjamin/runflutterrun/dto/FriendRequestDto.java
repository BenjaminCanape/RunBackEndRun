package canape.benjamin.runflutterrun.dto;

import canape.benjamin.runflutterrun.model.enums.FriendRequestStatus;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing a friend request.
 */
@Data
public class FriendRequestDto {
    private FriendRequestStatus status;
}
