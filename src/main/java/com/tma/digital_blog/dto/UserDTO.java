package com.tma.digital_blog.dto;

import com.tma.digital_blog.model.enumType.Role;
import com.tma.digital_blog.model.annotations.UserUsernameUnique;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private Long id;

    @Size(max = 255)
    @UserUsernameUnique
    private String username;

    @Size(max = 255)
    private String password;

    @NotNull
    private Role role;

    private boolean isActivated;

}
