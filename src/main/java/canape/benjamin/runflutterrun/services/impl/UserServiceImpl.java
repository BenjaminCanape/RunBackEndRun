package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.dto.EditPasswordDto;
import canape.benjamin.runflutterrun.dto.EditProfileDto;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private JwtUtils jwtUtils;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private Environment env;

    /**
     * Find a user by its token
     *
     * @param token the token
     * @return the user found
     * @throws NotFoundException No user found
     */
    public User getUserFromToken(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    /**
     * Find a user by its id
     *
     * @param userId the user id
     * @return the user found
     * @throws EntityNotFoundException No user found
     */
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No user with id: " + userId));
    }

    /**
     * Create a new user.
     *
     * @param user the user to create
     * @return the ID of the created user
     * @throws RuntimeException if an account already exists with the provided email
     */
    @Override
    @Transactional
    public Long create(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("An account already exists for this email");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user).getId();
    }

    /**
     * Search users by the search text.
     *
     * @param token the current user token
     * @param searchText the text which will serve to find users
     * @return the user object
     */
    public List<User> search(String token, String searchText) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.findByUsernameOrFirstNameOrLastName(searchText, username);
    }

    /**
     * Find a user by their username.
     *
     * @param username the username
     * @return the user object
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Edit the password of a user.
     *
     * @param token the token associated with the user
     * @param dto the dto with the updated password
     * @return the ID of the updated user
     * @throws EntityNotFoundException if the user is not found
     */
    @Override
    @Transactional
    public Long editPassword(String token, EditPasswordDto dto) {
        User user = getUserFromToken(token);

        if (bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            return userRepository.save(user).getId();
        }

        throw new BadCredentialsException("The current password is incorrect");
    }

    /**
     * Edit the profile of a user.
     *
     * @param token the token associated with the user
     * @param dto the dto with the updated profile
     * @return the ID of the updated user
     */
    @Override
    @Transactional
    public Long editProfile(String token, EditProfileDto dto) {
        User user = getUserFromToken(token);

        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());

        return userRepository.save(user).getId();
    }

    /**
     * Delete a user by their token.
     *
     * @param token the user's token
     */
    @Override
    @Transactional
    public void delete(String token) {
        User user = getUserFromToken(token);

        userRepository.deleteById(user.getId());
    }

    /**
     * Upload the profile picture of the current user
     *
     * @param token the token of the current user
     * @param file the file to upload as the profile picture
     */
    @Override
    @Transactional
    public void uploadProfilePicture(String token, MultipartFile file) throws IOException {
        User user = getUserFromToken(token);

        byte[] imageData = file.getBytes();
        String type = file.getContentType();
        user.setProfilePicture(imageData);
        user.setProfilePictureType(type);
        userRepository.save(user);
    }

    /**
     * Get profile picture of user of id
     *
     * @param id the user id
     */
    @Override
    public Map<String, Object> getProfilePicture(String id) {
        Optional<User> user = userRepository.findById(Long.parseLong(id));
        if (user.isEmpty()) {
            throw new NotFoundException("The user is not found");
        }
        Map<String, Object> profilePicture = new HashMap<>();
        profilePicture.put("contentType", user.get().getProfilePictureType());
        profilePicture.put("imageData", user.get().getProfilePicture());
        return profilePicture;
    }
}
