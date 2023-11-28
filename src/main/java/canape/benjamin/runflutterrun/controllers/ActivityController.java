package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.dto.ActivityDto;
import canape.benjamin.runflutterrun.dto.LocationDto;
import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.Location;
import canape.benjamin.runflutterrun.services.IActivityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * Retrieves all activities.
     *
     * @param token The authorization token.
     * @return A list of ActivityDto objects.
     */
    @GetMapping(value = "/all", produces = "application/json")
    public List<ActivityDto> getAll(@RequestHeader(name = "Authorization") String token) {
        try {
            return StreamSupport
                    .stream(activityCrudService.getAll(token).spliterator(), false)
                    .collect(Collectors.toList()).stream().map(activity -> convertToDTO(token, activity))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get activities", e);
        }
    }


    /**
     * Retrieves my activities and my friends.
     *
     * @param token The authorization token.
     * @return A list of ActivityDto objects.
     */
    @GetMapping(value = "/friends", produces = "application/json")
    public List<ActivityDto> getMineAndMyFriends(@RequestHeader(name = "Authorization") String token) {
        try {
            return StreamSupport
                    .stream(activityCrudService.getMineAndMyFriends(token).spliterator(), false)
                    .collect(Collectors.toList()).stream().map(activity -> convertToDTO(token, activity))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get activities", e);
        }
    }

    /**
     * Retrieves the activities of a specific user
     *
     * @param id The ID of the user.
     * @param token The authorization token.
     * @return A list of ActivityDto objects.
     */
    @GetMapping(value = "/user/{id}", produces = "application/json")
    public List<ActivityDto> getByUser(@PathVariable long id, @RequestHeader(name = "Authorization") String token) {
        try {
            return StreamSupport
                    .stream(activityCrudService.getByUser(token, id).spliterator(), false)
                    .collect(Collectors.toList()).stream().map(activity -> convertToDTO(token, activity))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get activities", e);
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
    public ActivityDto create(@RequestBody ActivityDto activity, @RequestHeader(name = "Authorization") String token) {
        try {
            return convertToDTO(token, activityCrudService.create(convertToEntity(activity), token));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create activity", e);
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
    public ActivityDto retrieve(@PathVariable long id, @RequestHeader(name = "Authorization") String token) {
        try {
            return convertToDTO(token, activityCrudService.getById(token, id), true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get activity", e);
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
    public ActivityDto update(@RequestBody ActivityDto activity, @RequestHeader(name = "Authorization") String token) {
        try {
            return convertToDTO(token, activityCrudService.update(token, convertToEntity(activity)));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update activity", e);
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Activity deletion failed");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Activity liked failed");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Activity disliked failed");
        }
    }

    private Activity convertToEntity(ActivityDto activityDto) {
        Activity activity = modelMapper.map(activityDto, Activity.class);

        List<Location> locations = new ArrayList<>();
        activityDto.getLocations().forEach((location) -> {
            Location loc = modelMapper.map(location, Location.class);
            loc.setActivity(activity);
            locations.add(loc);
        });

        activity.setLocations(locations);
        return activity;
    }

    private ActivityDto convertToDTO(String token, Activity activity) {
        return convertToDTO(token, activity, false);
    }

    private ActivityDto convertToDTO(String token, Activity activity, Boolean fetchLocations) {
        List<LocationDto> locations = new ArrayList<>();
        if (fetchLocations) {
            activity.getLocations().forEach((location) ->
                    locations.add(modelMapper.map(location, LocationDto.class))
            );
        }

        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        long time = Math.abs(activity.getEndDatetime().getTime() - activity.getStartDatetime().getTime());
        activityDto.setTime(time);
        activityDto.setLocations(locations);

        long count = activityCrudService.getActivityLikeCount(activity.getId());
        activityDto.setLikesCount(count);

        boolean hasCurrentUserLiked = activityCrudService.currentUserLiked(activity.getId(), token);
        activityDto.setHasCurrentUserLiked(hasCurrentUserLiked);

        return activityDto;
    }
}
