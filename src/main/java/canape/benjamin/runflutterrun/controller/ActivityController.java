package canape.benjamin.runflutterrun.controller;

import canape.benjamin.runflutterrun.dto.ActivityDto;
import canape.benjamin.runflutterrun.dto.LocationDto;
import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.Location;
import canape.benjamin.runflutterrun.service.IActivityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping(value = "/all", produces = "application/json")
    public List<ActivityDto> getAll(@RequestHeader(name = "Authorization") String token) {
        try {
            return StreamSupport
                    .stream(activityCrudService.getAll(token).spliterator(), false)
                    .collect(Collectors.toList()).stream().map(activity -> convertToDTO(activity))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get activities", e);
        }
    }

    @PostMapping(value = "/", consumes = "application/json")
    public ActivityDto create(@RequestBody ActivityDto activity, @RequestHeader(name = "Authorization") String token) {
        try {
            return convertToDTO(activityCrudService.create(convertToEntity(activity), token));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create activity", e);
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ActivityDto retrieve(@PathVariable long id) {
        try {
            return convertToDTO(activityCrudService.getById(id), true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get activity", e);
        }
    }

    @PutMapping(value = "/", consumes = "application/json")
    public ActivityDto update(@RequestBody ActivityDto activity) {
        try {
            return convertToDTO(activityCrudService.update(convertToEntity(activity)));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update activity", e);
        }
    }

    @DeleteMapping(value = "/")
    public ResponseEntity<String> delete(@RequestParam(value = "id") Long id) {
        try {
            activityCrudService.delete(id);
            return ResponseEntity.ok("Activity successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Activity deletion failed");
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

    private ActivityDto convertToDTO(Activity activity) {
        return convertToDTO(activity, false);
    }

    private ActivityDto convertToDTO(Activity activity, Boolean fetchLocations) {
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
        return activityDto;
    }
}