package com.study.ticketservice.common.security;

import com.study.ticketservice.domain.auth.entity.UserRoleMap;
import com.study.ticketservice.domain.auth.repository.RolePrivilegeMapRepository;
import com.study.ticketservice.domain.auth.repository.UserRepository;
import com.study.ticketservice.domain.auth.repository.UserRoleMapRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RbacAuthoritiesFilter extends OncePerRequestFilter {

    private final UserRoleMapRepository userRoleMapRepository;
    private final RolePrivilegeMapRepository rolePrivilegeMapRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long userId)) {
            filterChain.doFilter(request, response);
            return;
        }

        Set<String> authorities = getAuthorities(userId); // Role & Privilege 모두 추가

        List<SimpleGrantedAuthority> granted = authorities.stream().map(SimpleGrantedAuthority::new).toList();
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                userId,
                authentication.getCredentials(),
                granted
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return uri.equals("/api/v1/auth/login")
        || uri.equals("/api/v1/auth/logout")
        || uri.equals("/api/v1/auth/refresh");
    }

    private Set<String> getAuthorities(Long userId) {
        List<String> roleNames = userRoleMapRepository.findRoleNamesByUserId(userId);
        Set<String> authorities = new HashSet<>(roleNames);

        if (!roleNames.isEmpty()) {
            List<UserRoleMap> userRoles = userRoleMapRepository.findAllByUserUserId(userId);
            List<Long> roleIds = userRoles.stream()
                    .map(urm -> urm.getRole().getRoleId())
                    .toList();
            if (!roleIds.isEmpty()) {
                List<String> privilegeNames = rolePrivilegeMapRepository.findPrivilegeNamesByRoleIds(roleIds);
                authorities.addAll(privilegeNames);
            }
        }
        return authorities;
    }
}


