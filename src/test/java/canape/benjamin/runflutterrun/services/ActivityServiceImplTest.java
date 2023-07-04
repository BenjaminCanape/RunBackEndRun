package canape.benjamin.runflutterrun.services;

import canape.benjamin.runflutterrun.model.Activity;
import canape.benjamin.runflutterrun.model.Location;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.model.enums.ActivityType;
import canape.benjamin.runflutterrun.repositories.ActivityRepository;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.impl.ActivityServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityServiceImplTest {

    private ActivityServiceImpl activityService;

    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ActivityRepository activityRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activityService = new ActivityServiceImpl(jwtUtils, userRepository, activityRepository);
    }

    @Test
    void getAll_ReturnsAllActivities() {
        // Mock
        Activity activity1 = createSampleActivity();
        Activity activity2 = createSampleActivity();
        when(activityRepository.findAllByOrderByStartDatetimeDesc()).thenReturn(List.of(activity1, activity2));

        // Call
        Iterable<Activity> activities = activityService.getAll();

        // Verify
        assertNotNull(activities);
        List<Activity> activityList = StreamSupport.stream(activities.spliterator(), false).collect(Collectors.toList());
        assertEquals(2, activityList.size());
        assertTrue(activityList.contains(activity1));
        assertTrue(activityList.contains(activity2));
    }

    @Test
    void create_ReturnsCreatedActivity() {
        // Mock
        Activity activity = createSampleActivity();
        when(activityRepository.save(activity)).thenReturn(activity);

        // Call
        Activity createdActivity = activityService.create(activity);

        // Verify
        assertNotNull(createdActivity);
        assertEquals(activity, createdActivity);
        verify(activityRepository).save(activity);
    }

    @Test
    void getAllWithToken_ReturnsActivitiesForUser() {
        // Mock
        String token = "mock_token";
        String username = "mock_username";
        User user = new User();
        Activity activity1 = createSampleActivity();
        Activity activity2 = createSampleActivity();
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(activityRepository.findAllByOrderByStartDatetimeDescAndUser(user)).thenReturn(List.of(activity1, activity2));

        // Call
        Iterable<Activity> activities = activityService.getAll(token);

        // Verify
        assertNotNull(activities);
        List<Activity> activityList = StreamSupport.stream(activities.spliterator(), false).collect(Collectors.toList());
        assertEquals(2, activityList.size());
        assertTrue(activityList.contains(activity1));
        assertTrue(activityList.contains(activity2));
    }

    @Test
    void createWithToken_ReturnsCreatedActivityForUser() {
        // Mock
        String token = "mock_token";
        String username = "mock_username";
        User user = new User();
        Activity activity = createSampleActivity();

        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(activityRepository.save(activity)).thenReturn(activity);

        // Call
        Activity createdActivity = activityService.create(activity, token);

        // Verify
        assertNotNull(createdActivity);
        assertEquals(activity, createdActivity);
        assertEquals(user, activity.getUser());
        verify(activityRepository).save(activity);
    }

    @Test
    void getById_ReturnsActivityById() {
        // Mock
        long id = 1;
        Activity activity = createSampleActivity();
        when(activityRepository.findById(id)).thenReturn(Optional.of(activity));

        // Call
        Activity result = activityService.getById(id);

        // Verify
        assertNotNull(result);
        assertEquals(activity, result);
    }

    @Test
    void getById_ThrowsEntityNotFoundExceptionWhenActivityNotFound() {
        // Mock
        long id = 1;
        when(activityRepository.findById(id)).thenReturn(Optional.empty());

        // Call and verify
        assertThrows(EntityNotFoundException.class, () -> activityService.getById(id));
    }

    @Test
    void update_ReturnsUpdatedActivity() {
        // Mock
        User user = new User();
        Activity updatedActivity = createSampleActivity();
        Activity existingActivity = createSampleActivity();
        when(activityRepository.findById(existingActivity.getId())).thenReturn(Optional.of(existingActivity));
        when(activityRepository.save(existingActivity)).thenReturn(existingActivity);

        // Call
        Activity result = activityService.update(updatedActivity);

        // Verify
        assertNotNull(result);
        assertEquals(existingActivity, result);
        assertEquals(updatedActivity.getType(), existingActivity.getType());
        assertEquals(updatedActivity.getDistance(), existingActivity.getDistance());
        assertEquals(updatedActivity.getStartDatetime(), existingActivity.getStartDatetime());
        assertEquals(updatedActivity.getEndDatetime(), existingActivity.getEndDatetime());
        assertEquals(updatedActivity.getSpeed(), existingActivity.getSpeed());
        verify(activityRepository).save(existingActivity);
    }

    @Test
    void update_ThrowsEntityNotFoundExceptionWhenActivityNotFound() {
        // Mock
        Activity activity = createSampleActivity();
        when(activityRepository.findById(activity.getId())).thenReturn(Optional.empty());

        // Call and verify
        assertThrows(EntityNotFoundException.class, () -> activityService.update(activity));
    }

    @Test
    void delete_CallsActivityRepositoryDeleteById() {
        // Mock
        long id = 1;

        // Call
        activityService.delete(id);

        // Verify
        verify(activityRepository).deleteById(id);
    }

    // Helper method to create a sample Activity
    private Activity createSampleActivity() {
        User user = new User();
        Location location = new Location();
        location.setDatetime(new Date());
        location.setLatitude(0.4);
        location.setLongitude(0.66797);
        List<Location> locations = new ArrayList<>();
        locations.add(location);

        Activity activity = new Activity();
        activity.setStartDatetime(new Date());
        activity.setEndDatetime(new Date());
        activity.setUser(user);
        activity.setSpeed(12.3);
        activity.setDistance(10.012);
        activity.setType(ActivityType.RUNNING);
        activity.setLocations(locations);
        return activity;
    }
}
