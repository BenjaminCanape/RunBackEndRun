package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.dto.FriendRequestDto;
import canape.benjamin.runflutterrun.dto.UserSearchDto;
import canape.benjamin.runflutterrun.model.FriendRequest;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.services.IFriendRequestService;
import canape.benjamin.runflutterrun.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The controller manages operations related to friend requests, such as sending requests, accepting, rejecting, and canceling them.
 * It also provides methods to fetch information about pending friend requests and the status of a specific request.
 */
@RestController
@RequestMapping("/api/private/friends")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FriendRequestController {

    /**
     * Mapper for converting DTO objects to entities and vice versa.
     */
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Service for handling user-related operations.
     */
    @Autowired
    private IUserService userCrudService;

    /**
     * Service for handling friend request-related operations.
     */

    @Autowired
    private IFriendRequestService friendRequestService;

    /**
     * Retrieves a list of pending friend requests for the authenticated user.
     *
     * @param token The authorization token for the user.
     * @return A list of UserSearchDto representing users who have sent friend requests.
     */
    @GetMapping("/pending")
    public List<UserSearchDto> getPendingFriendRequests(@RequestHeader(name = "Authorization") String token) {
        return friendRequestService.getPendingFriendRequests(token)
                .stream()
                .map(request -> convertToUserDTO(request.getSender()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the status of a friend request sent to a specific user by the authenticated user.
     *
     * @param token  The authorization token for the user.
     * @param userId The ID of the user for whom the status of the friend request is requested.
     * @return An optional FriendRequestDto representing the status of the friend request, if it exists.
     */
    @GetMapping("/getStatus")
    public Optional<FriendRequestDto> getStatus(@RequestHeader(name = "Authorization") String token, @RequestParam Long userId) {
        Optional<FriendRequest> request = friendRequestService.getFriendRequestForUser(token, userId);
        return request.map(this::convertToDTO);
    }

    /**
     * Sends a friend request to another user.
     *
     * @param token      The authorization token for the user sending the request.
     * @param receiverId The ID of the user to whom the friend request is being sent.
     * @return The ID of the created friend request.
     */
    @PostMapping("/sendRequest")
    public Long sendFriendRequest(@RequestHeader(name = "Authorization") String token, @RequestParam Long receiverId) {
        try {
            return friendRequestService.sendFriendRequest(token, receiverId).getId();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to send the friend request", e);
        }
    }

    /**
     * Accepts a friend request.
     *
     * @param token    The authorization token for the user accepting the request.
     * @param requestId The ID of the friend request to be accepted.
     * @return A FriendRequestDto representing the accepted friend request.
     */
    @PostMapping("/acceptRequest")
    public FriendRequestDto acceptFriendRequest(@RequestHeader(name = "Authorization") String token, @RequestParam Long userId) {
        try {
            return convertToDTO(friendRequestService.acceptFriendRequest(token, userId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to accept the friend request", e);
        }
    }

    /**
     * Rejects a friend request.
     *
     * @param token    The authorization token for the user rejecting the request.
     * @param requestId The ID of the friend request to be rejected.
     * @return A FriendRequestDto representing the rejected friend request.
     */
    @PostMapping("/rejectRequest")
    public FriendRequestDto rejectFriendRequest(@RequestHeader(name = "Authorization") String token, @RequestParam Long userId) {
        try {
            return convertToDTO(friendRequestService.rejectFriendRequest(token, userId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to reject the friend request", e);
        }
    }

    /**
     * Cancels a friend request sent by the authenticated user.
     *
     * @param token    The authorization token for the user canceling the request.
     * @param requestId The ID of the friend request to be canceled.
     * @return A FriendRequestDto representing the canceled friend request.
     */
    @PostMapping("/cancelRequest")
    public FriendRequestDto cancelFriendRequest(@RequestHeader(name = "Authorization") String token, @RequestParam Long userId) {
        try {
            return convertToDTO(friendRequestService.cancelFriendRequest(token, userId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to cancel the friend request", e);
        }
    }

    private FriendRequest convertToEntity(FriendRequestDto dto) {
        return modelMapper.map(dto, FriendRequest.class);
    }

    private FriendRequestDto convertToDTO(FriendRequest entity) {
        return modelMapper.map(entity, FriendRequestDto.class);
    }

    private UserSearchDto convertToUserDTO(User entity) {
        return modelMapper.map(entity, UserSearchDto.class);
    }
}
