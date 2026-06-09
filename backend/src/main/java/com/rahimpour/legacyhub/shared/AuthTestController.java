package com.rahimpour.legacyhub.shared;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/me")
public class AuthTestController {

    @GetMapping
    public Map<String, Object> me(Authentication authentication) { // authentication is injected by Spring Security
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Map.of(
                "name", authentication.getName(),
                "authenticated", authentication.isAuthenticated(),
                "authorities", authorities
        );
    }
}
