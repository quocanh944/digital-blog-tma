package com.tma.digital_blog.service;

import com.tma.digital_blog.dto.UserDTO;
import com.tma.digital_blog.util.ReferencedWarning;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    public Page<UserDTO> findAll(Integer page, Integer size, String search);
    public UserDTO get(final Long id);
    public UserDTO get(final String username);
    public Long create(final UserDTO userDTO);
    public void activate(final Long id);
    public void deactivate(final Long id);
    public void delete(final Long id);
    public ReferencedWarning getReferencedWarning(final Long id);
    public boolean usernameExists(final String username);
}
