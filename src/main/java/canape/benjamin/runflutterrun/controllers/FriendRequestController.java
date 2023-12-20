package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.dto.FriendRequestDto;
import canape.benjamin.runflutterrun.dto.UserSearchDto;
import canape.benjamin.runflutterrun.model.FriendRequest;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.services.IFriendRequestService;
import canape.benjamin.runflutterrun.services.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
     * @param page The page to display
     * @param size The number of elements to get
     * @return A Page of UserSearchDto representing users who have sent friend requests.
     */
    @GetMapping("/pending")
    public ResponseEntity<Page<UserSearchDto>> getPendingFriendRequests(@RequestHeader(name = "Authorization") String token,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserSearchDto> pendingRequests = friendRequestService.getPendingFriendRequests(token, pageable)
                    .map(request -> convertToUserDTO(request.getSender()));
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves the status of a friend request sent to a specific user by the authenticated user.
     *
     * @param token  The authorization token for the user.
     * @param userId The ID of the user for whom the status of the friend request is requested.
     * @return An optional FriendRequestDto representing the status of the friend request, if it exists.
     */
    @GetMapping("/getStatus")
    public ResponseEntity<FriendRequestDto> getStatus(@RequestHeader(name = "Authorization") String token, @RequestParam Long userId) {
        try {
            Optional<FriendRequest> request = friendRequestService.getFriendRequestForUser(token, userId);
            return request.map(r -> ResponseEntity.ok(convertToDTO(r)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Sends a friend request to another user.
     *
     * @param token      The authorization token for the user sending the request.
     * @param receiverId The ID of the user to whom the friend request is being sent.
     * @return The ID of the created friend request.
     */
    @PostMapping("/sendRequest")
    public ResponseEntity<Long> sendFriendRequest(@RequestHeader(name = "Authorization") String token, @RequestParam Long receiverId) {
        try {
            Long requestId = friendRequestService.sendFriendRequest(token, receiverId).getId();
            return ResponseEntity.status(HttpStatus.CREATED).body(requestId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Accepts a friend request.
     *
     * @param token    The authorization token for the user accepting the request.
     * @param userId The ID of the friend request to be accepted.
     * @return A FriendRequestDto representing the accepted friend request.
     */
    @PostMapping("/acceptRequest")
    public ResponseEntity<FriendRequestDto> acceptFriendRequest(@RequestHeader(name = "Authorization") String token, @RequestParam Long userId) {
        try {
            FriendRequestDto acceptedRequest = convertToDTO(friendRequestService.acceptFriendRequest(token, userId));
            return ResponseEntity.ok(acceptedRequest);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Rejects a friend request.
     *
     * @param token    The authorization token for the user rejecting the request.
     * @param userId The ID of the friend request to be rejected.
     * @return A FriendRequestDto representing the rejected friend request.
     */
    @PostMapping("/rejectRequest")
    public ResponseEntity<FriendRequestDto> rejectFriendRequest(@RequestHeader(name = "Authorization") String token, @RequestParam Long userId) {
        try {
            FriendRequestDto rejectedRequest = convertToDTO(friendRequestService.rejectFriendRequest(token, userId));
            return ResponseEntity.ok(rejectedRequest);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Cancels a friend request sent by the authenticated user.
     *
     * @param token    The authorization token for the user canceling the request.
     * @param userId The ID of the friend request to be canceled.
     * @return A FriendRequestDto representing the canceled friend request.
     */
    @PostMapping("/cancelRequest")
    public ResponseEntity<FriendRequestDto> cancelFriendRequest(@RequestHeader(name = "Authorization") String token, @RequestParam Long userId) {
        try {
            FriendRequestDto canceledRequest = convertToDTO(friendRequestService.cancelFriendRequest(token, userId));
            return ResponseEntity.ok(canceledRequest);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
