package com.oussama.FacultyPlanning.Mapper;

import com.oussama.FacultyPlanning.Dto.UserDto;
import com.oussama.FacultyPlanning.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public User userDtoToUser(UserDto userDto) {
        if (userDto == null)
            return null;
        else {
            User user = new User();
            user.setId(userDto.getId());
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setPhone(userDto.getPhone());
            user.setFaculty(userDto.getFaculty());
            if (userDto.getRole()!=null) {
                user.setRole(userDto.getRole());
            }
            return user;
        }
    }

    public UserDto userToUserDto(User user) {
        if (user == null)
            return null;
        else {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setFullName(user.getFullName());
            userDto.setEmail(user.getEmail());
            userDto.setPhone(user.getPhone());
            userDto.setRole(user.getRole());
            return userDto;
        }
    }
}
