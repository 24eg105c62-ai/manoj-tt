package com.pocketskilltrack.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pocketskilltrack.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String studentId;
    private final String email;
    private final String fullName;

    @JsonIgnore
    private final String password;

    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String studentId, String email, String fullName, String password, boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.studentId = studentId;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getStudentId(),
                user.getEmail(),
                user.getFullName(),
                user.getPasswordHash(),
                user.isEnabled(),
                authorities
        );
    }

    public Long getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }

    @Override
    public String getUsername() { return email; }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}
