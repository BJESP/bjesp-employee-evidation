package com.example.demo.security;

import com.example.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private  TokenUtils tokenUtils;
    @Autowired
    private  CustomUserDetailsService userDetailsService;

    public TokenAuthenticationFilter() {

    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        String userEmail;
        String authToken = tokenUtils.getToken(request);
        System.out.println(authToken+"izgenerisan");
        if (authToken != null) {
            // Get username from the token
            userEmail = tokenUtils.getUsernameFromToken(authToken);
            System.out.println(userEmail+"ahhahahahhahahahhha");
            if (userEmail != null) {
                System.out.println("dosaooooooooo");
                // Get user with the username
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                System.out.println(userDetails.getUsername()+userDetails.getPassword()+"blalalalal");
                // Check if the token is valid
                if (tokenUtils.validateToken(authToken, userDetails)) {
                    // Create authentication
                    TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                    authentication.setToken(authToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Forward the request to the next filter
        chain.doFilter(request, response);
    }
}
