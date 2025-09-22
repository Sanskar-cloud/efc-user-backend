package com.example.efc_user.security;


import com.example.efc_user.config.SecurityConfig;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Bypass public endpoints
        String servletPath = request.getServletPath();
        boolean isPublic = Arrays.stream(SecurityConfig.PUBLIC_URLS)
                .anyMatch(publicUrl -> servletPath.matches(publicUrl.replace("**", ".*")));
        if (isPublic) {
            System.out.println("Skipping JWT filter for public path: " + servletPath);
            filterChain.doFilter(request, response);
            return;
        }

        // Get the token from the Authorization header
        String requestToken = request.getHeader("Authorization");

        String username = null;
        String token = null;

        // Validate token format
        if (requestToken != null && requestToken.startsWith("Bearer ")) {
            token = requestToken.substring(7); // Remove "Bearer " prefix

            try {
                username = this.jwtTokenHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get Jwt token: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                System.out.println("Jwt token has expired: " + e.getMessage());
            } catch (MalformedJwtException e) {
                System.out.println("Invalid Jwt token: " + e.getMessage());
            }
        } else {
            System.out.println("Jwt token does not begin with Bearer or is null");
        }

        // Validate token and authenticate user
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (this.jwtTokenHelper.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Invalid Jwt token");
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean isExcluded = Arrays.stream(SecurityConfig.PUBLIC_URLS)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
        System.out.println("Request Path: " + path + " Excluded: " + isExcluded);
        return isExcluded;
    }

}
