package com.tma.digital_blog.service.impl;

import com.tma.digital_blog.dto.AuthResponseDTO;
import com.tma.digital_blog.dto.ChangePasswordDTO;
import com.tma.digital_blog.dto.LoginDTO;
import com.tma.digital_blog.dto.RefreshTokenRequestDTO;
import com.tma.digital_blog.model.User;
import com.tma.digital_blog.repository.UserRepository;
import com.tma.digital_blog.security.JWTTokenProvider;
import com.tma.digital_blog.service.AuthService;
import com.tma.digital_blog.service.RedisService;
import com.tma.digital_blog.util.BadInputException;
import com.tma.digital_blog.util.NotFoundException;
import com.tma.digital_blog.util.UserDeactivateException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private RedisService redisService;
    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        User user = userRepository.findUserByUsername(
                loginDTO.getUsername()
        ).orElseThrow(() -> new UsernameNotFoundException("Not found this user."));

        if (!user.isActivated()) {
            throw new UserDeactivateException("This user is deactivate");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(), loginDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername());

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    @Override
    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        boolean isValid = jwtTokenProvider.validateRefreshToken(
                refreshTokenRequestDTO.getRefreshToken()
        );
        String username = jwtTokenProvider.getUsernameFromRefreshToken(
                refreshTokenRequestDTO.getRefreshToken()
        );

        String token = redisService.getJWTFromUsername(username);

        if (!token.equals(refreshTokenRequestDTO.getRefreshToken())) {
            redisService.removeJTWRedis(username);
            throw new NotFoundException("Token in Redis not match.");
        }

        User user = userRepository.findUserByUsername(
                username
        ).orElseThrow(() -> new UsernameNotFoundException("Not found this user."));

        if (!user.isActivated()) {
            throw new UserDeactivateException("This user is deactivate");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null,
                AuthorityUtils.createAuthorityList(user.getRole().name()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername());

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO, String username) throws UsernameNotFoundException, BadInputException {
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new BadInputException("New Password and Confirm Password not match");
        }

        User u = userRepository
                .findUserByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Not found user")
                );

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!(encoder.matches(changePasswordDTO.getCurrentPassword(), u.getPassword()))
        ) {
            throw new BadInputException("Wrong password");
        }

        u.setPassword(encoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(u);
    }
}
