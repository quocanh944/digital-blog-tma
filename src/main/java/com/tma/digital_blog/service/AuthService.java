package com.tma.digital_blog.service;

import com.tma.digital_blog.dto.AuthResponseDTO;
import com.tma.digital_blog.dto.ChangePasswordDTO;
import com.tma.digital_blog.dto.LoginDTO;
import com.tma.digital_blog.dto.RefreshTokenRequestDTO;
import com.tma.digital_blog.util.BadInputException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthService {
    public AuthResponseDTO login(LoginDTO loginDTO);
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO);
    public void changePassword(ChangePasswordDTO changePasswordDTO, String username) throws UsernameNotFoundException, BadInputException;
}
