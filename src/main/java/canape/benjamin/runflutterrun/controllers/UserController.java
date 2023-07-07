package canape.benjamin.runflutterrun.controllers;

import canape.benjamin.runflutterrun.dto.RefreshTokenDto;
import canape.benjamin.runflutterrun.dto.UserDto;
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
     * @param userDto The UserDto object containing the user details.
     * @return The ID of the user whose password was edited.
     */
    @PutMapping(value = "/private/user/editPassword", consumes = "application/json")
    public Long editPassword(@RequestBody UserDto userDto) {
        try {
            return userCrudService.editPassword(convertToEntity(userDto));
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

    private User convertToEntity(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        return user;
    }

    private UserDto convertToDTO(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return userDto;
    }
}
