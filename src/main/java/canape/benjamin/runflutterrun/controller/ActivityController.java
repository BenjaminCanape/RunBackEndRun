package canape.benjamin.runflutterrun.controller;

import canape.benjamin.runflutterrun.dto.ActivityDto;

import org.modelmapper.ModelMapper;
import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.service.IActivityService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("api/activity")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ActivityController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IActivityService activityCrudService;

    @GetMapping(value = "/all", produces = "application/json")
    public List<ActivityDto> getAll() {
        return StreamSupport
                .stream(activityCrudService.getAll().spliterator(), false)
                .collect(Collectors.toList()).stream().map(activity -> convertToDTO(activity))
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/", consumes = "application/json")
    public ActivityDto create(@RequestBody ActivityDto activity) {
        return convertToDTO(activityCrudService.create(convertToEntity(activity)));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ActivityDto retrieve(@PathVariable int id) {
        return convertToDTO(activityCrudService.getById(id));
    }

    @PutMapping(value = "/", consumes = "application/json")
    public ActivityDto update(@RequestBody ActivityDto activity) {
        return convertToDTO(activityCrudService.update(convertToEntity(activity)));
    }

    @DeleteMapping(value = "/")
    public String delete(int id) {
        activityCrudService.delete(id);
        return "Done";
    }

    private Activity convertToEntity (ActivityDto activityDto)
    {
        Activity activity = modelMapper.map(activityDto, Activity.class);
        return activity;
    }

    private ActivityDto convertToDTO (Activity activity)
    {
        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        long time = Math.abs(activity.getEndDatetime().getTime() - activity.getStartDatetime().getTime());
        activityDto.setTime(time);
        return activityDto;
    }
}