package canape.benjamin.runflutterrun.controller;

import canape.benjamin.runflutterrun.dto.UserDto;
import canape.benjamin.runflutterrun.model.User;
import canape.benjamin.runflutterrun.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/user/register", consumes = "application/json")
    public Integer create(@RequestBody UserDto userDto) {
        return userCrudService.create(convertToEntity(userDto));
    }

    @PutMapping(value = "/private/user/editPassword", consumes = "application/json")
    public Integer editPassword(@RequestBody UserDto userDto) {
        return userCrudService.editPassword(convertToEntity(userDto));
    }

    @DeleteMapping(value = "/private/user")
    public String delete(@RequestParam(value = "id") Long id) {
        userCrudService.delete(id);
        return "Done";
    }

    private User convertToEntity(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        return user;
    }

    private UserDto convertToDTO(User user
    ) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return userDto;
    }
}