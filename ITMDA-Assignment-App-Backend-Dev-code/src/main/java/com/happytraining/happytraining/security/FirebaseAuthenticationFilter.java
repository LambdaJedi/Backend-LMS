package com.happytraining.happytraining.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decodedToken.getUid();
                String email = decodedToken.getEmail();

                // Create authorities based on user claims or email
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

                // Add ADMIN role for specific users
                if (isAdminUser(email, decodedToken)) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    System.out.println("Admin user authenticated: " + email);
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                decodedToken,
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (FirebaseAuthException e) {
                System.err.println("Firebase token verification failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid Firebase token: " + e.getMessage() + "\"}");
                return;
            }
        } else {
            // No token for protected endpoint
            System.err.println("No Authorization token provided for protected endpoint: " + path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authorization token required\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path, String method) {
        // Auth endpoints are public
        if (path.startsWith("/api/auth/")) {
            return true;
        }

        // Debug endpoints are public
        if (path.startsWith("/api/debug/")) {
            return true;
        }

        // Course endpoints are public (all methods)
        if (path.startsWith("/api/courses/")) {
            return true;
        }
        if (path.equals("/api/courses")) {
            return true;
        }

        // User endpoints are public (all methods) - ADD THIS SECTION
        if (path.startsWith("/api/users/")) {
            return true;
        }
        if (path.equals("/api/users")) {
            return true;
        }

        // Error and static resources are public
        if (path.equals("/error") ||
                path.equals("/") ||
                path.startsWith("/static/") ||
                path.equals("/favicon.ico")) {
            return true;
        }

        return false;
    }

    // Customize this method to determine who gets ADMIN role
    private boolean isAdminUser(String email, FirebaseToken token) {
        // Option 1: Check specific email addresses
        if (email != null && (
                email.equals("admin@happytraining.com") ||
                        email.equals("administrator@happytraining.com") ||
                        email.endsWith("@admin.happytraining.com")
        )) {
            return true;
        }

        // Option 2: Check custom claims from Firebase
        if (token.getClaims().containsKey("admin") &&
                Boolean.TRUE.equals(token.getClaims().get("admin"))) {
            return true;
        }

        // Option 3: Check for admin role in claims
        if (token.getClaims().containsKey("role") && "admin".equals(token.getClaims().get("role"))) {
            return true;
        }

        // Option 4: Check for roles array in claims
        if (token.getClaims().containsKey("roles")) {
            Object roles = token.getClaims().get("roles");
            if (roles instanceof List && ((List<?>) roles).contains("admin")) {
                return true;
            }
        }

        return false;
    }
}