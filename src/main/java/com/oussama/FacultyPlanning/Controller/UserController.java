package com.oussama.FacultyPlanning.Controller;

import com.oussama.FacultyPlanning.Dto.ChangePasswordRequest;
import com.oussama.FacultyPlanning.Event.RegistrationCompleteEvent;
import com.oussama.FacultyPlanning.Model.Room;
import com.oussama.FacultyPlanning.Model.User;
import com.oussama.FacultyPlanning.Dto.UserDto;
import com.oussama.FacultyPlanning.Mapper.UserMapper;
import com.oussama.FacultyPlanning.Service.ExcelImportService;
import com.oussama.FacultyPlanning.Service.UserService;
import com.oussama.FacultyPlanning.Utility.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final ExcelImportService excelImportService;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto, final HttpServletRequest request) {
        User user = userMapper.userDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User newUser = userService.addNewUser(user);
        publisher.publishEvent(new RegistrationCompleteEvent(user, emailService.applicationUrl(request)));
        return ResponseEntity.ok(userMapper.userToUserDto(newUser));
    }

    @PostMapping("/teachers/import")
    public ResponseEntity<String> importTeachers(@RequestParam("file") MultipartFile file, @RequestParam("facultyId") Long facultyId) {
        try {
            List<User> teachers = excelImportService.importTeachersFromExcel(file, facultyId);
            return ResponseEntity.ok("Successfully imported " + teachers.size() + " teachers");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }

    @PatchMapping
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
        User user = userService.findUserById(userDto.getId());
        if (userDto.getFirstName()!=null) user.setFirstName(userDto.getFirstName());
        if (userDto.getLastName()!=null) user.setLastName(userDto.getLastName());
        if (userDto.getPhone()!=null) user.setPhone(userDto.getPhone());
        return ResponseEntity.ok(userMapper.userToUserDto(userService.updateUser(user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

}
