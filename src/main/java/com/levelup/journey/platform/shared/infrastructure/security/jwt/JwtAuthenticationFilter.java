package com.levelup.journey.platform.shared.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter
 * Intercepts requests to validate JWT tokens
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        // Check if Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token from header
            final String jwt = authHeader.substring(BEARER_PREFIX.length());
            
            // Validate token
            if (!jwtService.isTokenValid(jwt)) {
                logger.warn("Invalid JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            // Extract username, userId and roles from token
            final String username = jwtService.extractUsername(jwt);
            final String userId = jwtService.extractUserId(jwt);
            final List<String> roles = jwtService.extractRoles(jwt);

            // If user is not already authenticated, set authentication in context
            String principal = (userId != null && !userId.isBlank()) ? userId : username;

            if (principal != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Convert roles to Spring Security authorities
                List<SimpleGrantedAuthority> authorities = roles == null ? List.of() :
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities
                );

                // Set authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                logger.debug("User '{}' authenticated with roles: {}", principal, roles);
            }

        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
