package canape.benjamin.runflutterrun.services.impl;

import canape.benjamin.runflutterrun.dto.EditPasswordDto;
import canape.benjamin.runflutterrun.dto.EditProfileDto;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.repositories.UserRepository;
import canape.benjamin.runflutterrun.security.jwt.JwtUtils;
import canape.benjamin.runflutterrun.services.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private JwtUtils jwtUtils;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private Environment env;

    /**
     * Create a new user.
     *
     * @param user the user to create
     * @return the ID of the created user
     * @throws RuntimeException if an account already exists with the provided email
     */
    @Override
    public Long create(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
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
    @Override
    public List<User> search(String token, String searchText) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.search(searchText, username);
    }

    /**
     * Find a user by their username.
     *
     * @param username the username
     * @return the user object
     */
    @Override
    public User findByUsername(String username) {
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
    public Long editPassword(String token, EditPasswordDto dto) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

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
    public Long editProfile(String token, EditProfileDto dto) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);

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
    public void delete(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        userRepository.deleteById(user.getId());
    }

    /**
     * Upload the profile picture of the current user
     *
     * @param token the token of the current user
     * @param file the file to upload as the profile picture
     */
    @Override
    public void uploadProfilePicture(String token, MultipartFile file) throws IOException {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username);
        byte[] imageData = file.getBytes();
        user.setProfilePicture(imageData);
        userRepository.save(user);
    }

    /**
     * Get profile picture of user of id
     *
     * @param id the user id
     */
    @Override
    public byte[] getProfilePicture(String id) {
        Optional<User> user = userRepository.findUserById(Long.parseLong(id));
        if (user.isEmpty()) {
            throw new NotFoundException("The user is not found");
        }
        return user.get().getProfilePicture();
    }
}
