package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.dto.*;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.services.IRefreshTokenService;
import canape.benjamin.runflutterrun.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IUserService userCrudService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    /**
     * Creates a new user.
     *
     * @param userDto The UserDto object containing the user details.
     * @return The ID of the created user.
     */
    @PostMapping(value = "/user/register", consumes = "application/json")
    public ResponseEntity<Long> create(@RequestBody UserDto userDto) {
        try {
            Long userId = userCrudService.create(convertToEntity(userDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Generates a new access token from a refresh token.
     *
     * @param refreshTokenDto The RefreshTokenDto object containing the refresh token.
     * @return A ResponseEntity containing the new access token.
     */
    @PostMapping("/user/refreshToken")
    public ResponseEntity<RefreshTokenDto> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        try {
            String newToken = refreshTokenService.generateNewAccessTokenFromRequestToken(refreshTokenDto.getToken());
            RefreshTokenDto response = new RefreshTokenDto();
            response.setToken(newToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Edits the password of a user.
     *
     * @param token The authorization token.
     * @param editPasswordDto The EditPasswordDto object containing the password details.
     * @return The ID of the user whose password was edited.
     */
    @PutMapping(value = "/private/user/editPassword", consumes = "application/json")
    public ResponseEntity<Long> editPassword(@RequestHeader(name = "Authorization") String token, @RequestBody EditPasswordDto editPasswordDto) {
        try {
            Long userId = userCrudService.editPassword(token, editPasswordDto);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Edits the profile of a user.
     *
     * @param token The authorization token.
     * @param editProfileDto The EditProfileDto object containing the profile details.
     * @return The ID of the user whose profile was edited.
     */
    @PutMapping(value = "/private/user/editProfile", consumes = "application/json")
    public ResponseEntity<Long> editProfile(@RequestHeader(name = "Authorization") String token, @RequestBody EditProfileDto editProfileDto) {
        try {
            Long userId = userCrudService.editProfile(token, editProfileDto);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Deletes a user.
     *
     * @param token The authorization token.
     * @return A ResponseEntity with the deletion status.
     */
    @DeleteMapping(value = "/private/user")
    public ResponseEntity<String> delete(@RequestHeader(name = "Authorization") String token) {
        try {
            userCrudService.delete(token);
            return ResponseEntity.ok("User successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User deletion failed");
        }
    }

    /**
     * Retrieves users based on the search value
     *
     * @param token The authorization token.
     * @return A list of ActivityDto objects.
     */
    @GetMapping(value = "/private/user/search", produces = "application/json")
    public ResponseEntity<List<UserSearchDto>> search(@RequestHeader(name = "Authorization") String token, @RequestParam String searchText) {
        try {
            List<UserSearchDto> searchResults = userCrudService.search(token, searchText).stream()
                    .map(this::convertToSearchDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Upload the profile picture for the current user
     *
     * @param token The authorization token.
     * @param file The file to upload
     */
    @PostMapping("/private/user/picture/upload")
    public ResponseEntity<String> uploadProfilePicture(@RequestHeader(name = "Authorization") String token, @RequestParam("file") MultipartFile file) throws IOException {
        try {
            userCrudService.uploadProfilePicture(token, file);
            return ResponseEntity.ok("Successfully uploaded file");
        }  catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to upload the profile picture");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Get the profile picture for a user whose id is passed in parameter
     *
     * @param id The id of the user
     */
    @GetMapping("/user/picture/download/{id}")
    public ResponseEntity<byte[]> downloadProfilePicture(@PathVariable String id) {
        try {
            Map<String, Object> picture = userCrudService.getProfilePicture(id);
            MediaType mediaType = MediaType.parseMediaType((String) picture.get("contentType"));
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body((byte[]) picture.get("imageData"));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserSearchDto convertToSearchDTO(User user) {
        return modelMapper.map(user, UserSearchDto.class);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
