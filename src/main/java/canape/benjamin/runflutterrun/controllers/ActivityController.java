package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.dto.*;
import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.ActivityComment;
import canape.benjamin.runflutterrun.model.Location;
import canape.benjamin.runflutterrun.services.IActivityCommentService;
import canape.benjamin.runflutterrun.services.IActivityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("api/private/activity")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ActivityController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IActivityService activityCrudService;

    @Autowired
    private IActivityCommentService activityCommentService;

    /**
     * Retrieves all activities.
     *
     * @param token The authorization token.
     * @param page The page to display
     * @param size The number of elements to get
     * @param sort The sorting conf
     * @return A Page of ActivityDto objects.
     */
    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<Page<ActivityDto>> getAll(@RequestHeader(name = "Authorization") String token,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "startDatetime,desc") String sort) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<ActivityDto> activities = activityCrudService.getAll(token, pageable).map(
                    activity -> convertToDTO(token, activity, false, true)
            );
            return ResponseEntity.ok().body(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * Retrieves my activities and my friends.
     *
     * @param token The authorization token
     * @param page The page to display
     * @param size The number of elements to get
     * @param sort The sorting conf
     * @return A Page of ActivityDto objects.
     */
    @GetMapping(value = "/friends", produces = "application/json")
    public ResponseEntity<Page<ActivityDto>> getMineAndMyFriends(@RequestHeader(name = "Authorization") String token,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "startDatetime,desc") String sort) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<ActivityDto> activities = activityCrudService.getMineAndMyFriends(token, pageable).map(
                    activity -> convertToDTO(token, activity, false, true)
            );
            return ResponseEntity.ok().body(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves the activities of a specific user
     *
     * @param id The ID of the user.
     * @param token The authorization token.
     * @param page The page to display
     * @param size The number of elements to get
     * @param sort The sorting conf
     * @return A Page of ActivityDto objects.
     */
    @GetMapping(value = "/user/{id}", produces = "application/json")
    public ResponseEntity<Page<ActivityDto>> getByUser(@PathVariable long id, @RequestHeader(name = "Authorization") String token,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(defaultValue = "startDatetime,desc") String sort) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            Page<ActivityDto> activities = activityCrudService.getByUser(token, id, pageable).map(
                    activity -> convertToDTO(token, activity, false, true)
            );
            return ResponseEntity.ok().body(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Creates a new activity.
     *
     * @param activity The ActivityDto object containing the activity details.
     * @param token    The authorization token.
     * @return The created ActivityDto object.
     */
    @PostMapping(value = "/", consumes = "application/json")
    public ResponseEntity<ActivityDto> create(@RequestBody ActivityDto activity, @RequestHeader(name = "Authorization") String token) {
        try {
            return ResponseEntity.ok().body(convertToDTO(token, activityCrudService.create(convertToEntity(activity), token)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves an activity by its ID.
     *
     * @param id The ID of the activity.
     * @param token The authorization token.
     * @return The retrieved ActivityDto object.
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ActivityDto> retrieve(@PathVariable long id, @RequestHeader(name = "Authorization") String token) {
        try {
            return ResponseEntity.ok().body(convertToDTO(token, activityCrudService.getById(token, id), true, false));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Updates an existing activity.
     *
     * @param activity The updated ActivityDto object.
     * @param token The authorization token.
     * @return The updated ActivityDto object.
     */
    @PutMapping(value = "/", consumes = "application/json")
    public ResponseEntity<ActivityDto> update(@RequestBody ActivityDto activity, @RequestHeader(name = "Authorization") String token) {
        try {
            return ResponseEntity.ok().body(convertToDTO(token, activityCrudService.update(token, convertToEntity(activity)), false, false));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Deletes an activity.
     *
     * @param id The ID of the activity to delete.
     * @param token The authorization token.
     * @return A ResponseEntity with the deletion status.
     */
    @DeleteMapping(value = "/")
    public ResponseEntity<String> delete(@RequestParam(value = "id") Long id, @RequestHeader(name = "Authorization") String token) {
        try {
            activityCrudService.delete(token, id);
            return ResponseEntity.ok("Activity successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Like an activity.
     *
     * @param id    The ID of the activity to like.
     * @param token The authorization token.
     * @return boolean
     */
    @PostMapping("/like")
    public ResponseEntity<String> likeActivity(@RequestParam(value = "id") Long id, @RequestHeader(name = "Authorization") String token) {
        try {
            activityCrudService.like(id, token);
            return ResponseEntity.ok("Activity liked successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Dislike an activity.
     *
     * @param id    The ID of the activity to dislike.
     * @param token The authorization token.
     * @return boolean
     */
    @PostMapping("/dislike")
    public ResponseEntity<String> dislikeActivity(@RequestParam(value = "id") Long id, @RequestHeader(name = "Authorization") String token) {
        try {
            activityCrudService.dislike(id, token);
            return ResponseEntity.ok("Activity disliked successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Creates a comment
     *
     * @param comment The string containing the activity comment
     * @param activityId The activity Id
     * @param token The authorization token.
     * @return The created ActivityCommentDto object.
     */
    @PostMapping(value = "/comment")
    public ResponseEntity<ActivityCommentDto> createComment(@RequestParam("comment") String comment, @RequestParam("activityId") Long activityId, @RequestHeader(name = "Authorization") String token) {
        try {
            return ResponseEntity.ok().body(convertCommentToDTO(activityCommentService.create(comment, activityId, token)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Updates an existing comment.
     *
     * @param id The id of the comment.
     * @param comment The comment.
     * @param token The authorization token.
     * @return The updated ActivityCommentDto object.
     */
    @PutMapping(value = "/comment")
    public ResponseEntity<ActivityCommentDto> updateComment(@RequestParam("id") Long id, @RequestParam("comment") String comment, @RequestHeader(name = "Authorization") String token) {
        try {
            return ResponseEntity.ok().body(convertCommentToDTO(activityCommentService.update(id, comment, token)));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Deletes a comment.
     *
     * @param id The ID of the activity to delete.
     * @param token The authorization token.
     * @return A ResponseEntity with the deletion status.
     */
    @DeleteMapping(value = "/comment")
    public ResponseEntity<String> deleteComment(@RequestParam(value = "id") Long id, @RequestHeader(name = "Authorization") String token) {
        try {
            activityCommentService.delete(token, id);
            return ResponseEntity.ok("Comment successfully deleted");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private Activity convertToEntity(ActivityDto activityDto) {
        Activity activity = modelMapper.map(activityDto, Activity.class);

        List<Location> locations = activityDto.getLocations().stream()
                .map(locationDto -> {
                    Location loc = modelMapper.map(locationDto, Location.class);
                    loc.setActivity(activity);
                    return loc;
                })
                .collect(Collectors.toList());

        activity.setLocations(locations);
        activity.setComments(new ArrayList<>());
        return activity;
    }

    private ActivityDto convertToDTO(String token, Activity activity) {
        return convertToDTO(token, activity, false, true);
    }

    private ActivityCommentDto convertCommentToDTO(ActivityComment activityComment) {
        UserSearchDto userDto = modelMapper.map(activityComment.getUser(), UserSearchDto.class);
        ActivityCommentDto commentDto = modelMapper.map(activityComment, ActivityCommentDto.class);
        commentDto.setUser(userDto);

        return commentDto;
    }

    private ActivityDto convertToDTO(String token, Activity activity, Boolean fetchLocations, Boolean fetchComments) {
        List<LocationDto> locations = new ArrayList<>();
        if (fetchLocations) {
            locations = activity.getLocations().stream()
                    .map(location -> modelMapper.map(location, LocationDto.class))
                    .collect(Collectors.toList());
        }

        List<ActivityCommentDto> comments = new ArrayList<>();
        if (fetchComments) {
            comments = activity.getComments().stream()
                    .map(this::convertCommentToDTO)
                    .collect(Collectors.toList());
        }

        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        long time = Math.abs(activity.getEndDatetime().getTime() - activity.getStartDatetime().getTime());
        activityDto.setTime(time);
        activityDto.setLocations(locations);

        long count = activityCrudService.getActivityLikeCount(activity.getId());
        activityDto.setLikesCount(count);

        boolean hasCurrentUserLiked = activityCrudService.currentUserLiked(activity.getId(), token);
        activityDto.setHasCurrentUserLiked(hasCurrentUserLiked);
        activityDto.setComments(Optional.of(comments));

        return activityDto;
    }

    private List<ActivityDto> getActivityDtoList(String token, Iterable<Activity> activities, boolean fetchLocations, boolean fetchComments) {
        return StreamSupport.stream(activities.spliterator(), false)
                .map(activity -> convertToDTO(token, activity, fetchLocations, fetchComments))
                .collect(Collectors.toList());
    }
}
