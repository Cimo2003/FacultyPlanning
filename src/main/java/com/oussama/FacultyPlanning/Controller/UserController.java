package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Dto.ChangePasswordRequest;
import com.oussama.FacultyPlanning.Event.RegistrationCompleteEvent;
import com.oussama.FacultyPlanning.Model.User;
import com.oussama.FacultyPlanning.Dto.UserDto;
import com.oussama.FacultyPlanning.Mapper.UserMapper;
import com.oussama.FacultyPlanning.Service.UserService;
import com.oussama.FacultyPlanning.Utility.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto, final HttpServletRequest request) {
        User user = userMapper.userDtoToUser(userDto);
        System.out.println("DTO:"+userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User newUser = userService.addNewUser(user);
        publisher.publishEvent(new RegistrationCompleteEvent(user, emailService.applicationUrl(request)));
        return ResponseEntity.ok(userMapper.userToUserDto(newUser));
    }

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
