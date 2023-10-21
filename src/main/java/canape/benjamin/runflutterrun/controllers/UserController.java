package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.dto.*;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.services.IRefreshTokenService;
import canape.benjamin.runflutterrun.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    public Long create(@RequestBody UserDto userDto) {
        try {
            return userCrudService.create(convertToEntity(userDto));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create user", e);
        }
    }

    /**
     * Generates a new access token from a refresh token.
     *
     * @param refreshTokenDto The RefreshTokenDto object containing the refresh token.
     * @return A ResponseEntity containing the new access token.
     */
    @PostMapping("/user/refreshToken")
    public ResponseEntity<RefreshTokenDto> refreshtoken(@RequestBody RefreshTokenDto refreshTokenDto) {
        try {
            String newToken = refreshTokenService.generateNewAccessTokenFromRequestToken(refreshTokenDto.getToken());
            RefreshTokenDto response = new RefreshTokenDto();
            response.setToken(newToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed generate new access token", e);
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
    public Long editPassword(@RequestHeader(name = "Authorization") String token, @RequestBody EditPasswordDto editPasswordDto) {
        try {
            return userCrudService.editPassword(token, editPasswordDto);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed edit password", e);
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
    public List<UserSearchDto> search(@RequestHeader(name = "Authorization") String token, @RequestParam String searchText) {
        try {
            return userCrudService.search(token, searchText).stream()
                    .toList().stream().map(this::convertToSearchDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to get users", e);
        }
    }

    private User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserDto convertToDTO(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private UserSearchDto convertToSearchDTO(User user) {
        return modelMapper.map(user, UserSearchDto.class);
    }
}
