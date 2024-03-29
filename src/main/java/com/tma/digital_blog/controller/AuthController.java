package com.tma.digital_blog.controller;

import com.tma.digital_blog.dto.*;
import com.tma.digital_blog.security.JWTTokenProvider;
import com.tma.digital_blog.service.AuthService;
import com.tma.digital_blog.service.RedisService;
import com.tma.digital_blog.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private AuthService authService;
    private UserService userService;
    private RedisService redisService;
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    @SecurityRequirements()
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        AuthResponseDTO authResponseDTO = authService.login(loginDTO);
        redisService.addJWTRedis(authResponseDTO.getRefreshToken());
        return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        redisService.removeJTWRedis(authentication.getName());
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO
            ) {

        AuthResponseDTO authResponseDTO = authService.refreshToken(refreshTokenRequestDTO);
        redisService.addJWTRedis(authResponseDTO.getRefreshToken());

        return new ResponseEntity<>(
                authResponseDTO,
                HttpStatus.OK
        );
    }

    @GetMapping("/profile")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UserDTO> readProfile(Authentication authentication) {
        return new ResponseEntity<>(userService.get(authentication.getName()), HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody @Valid final ChangePasswordDTO changePasswordDTO,
            Authentication authentication) {
        authService.changePassword(changePasswordDTO, authentication.getName());

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
