package com.tma.digital_blog.dto.mapper;

import com.tma.digital_blog.dto.UserDTO;
import com.tma.digital_blog.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setActivated(user.isActivated());
        return userDTO;
    }

    public static User mapToEntity(final UserDTO userDTO, final User user) {
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setActivated(user.isActivated());
        return user;
    }
}
