package com.tma.digital_blog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tma.digital_blog.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisService redisService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String tokenFromRedis = redisService.getJWTFromUsername(username);

                if (!token.equals(tokenFromRedis)) {
                    redisService.removeJTWRedis(username);
                    throw new BadCredentialsException("Token not match with Redis");
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationCredentialsNotFoundException e) {
            logger.debug(e.getMessage());
            // Set content type to application/json
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Set status code to SC_UNAUTHORIZED
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // Create a Map to represent the JSON payload
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("error", "Unauthorized");
            jsonResponse.put("message", e.getMessage());

            // Use ObjectMapper to convert the Map to JSON and write it to the response
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, jsonResponse);
            out.flush();
        }
    }
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
