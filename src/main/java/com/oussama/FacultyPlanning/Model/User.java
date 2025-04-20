package com.oussama.FacultyPlanning.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oussama.FacultyPlanning.Enum.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Builder @Data @AllArgsConstructor @NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    @NaturalId(mutable = true)
    private String email;
    private String phone;
    @JsonIgnoreProperties(allowSetters = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private Faculty adminFaculty;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    @JsonIgnoreProperties(allowSetters = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Faculty faculty;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.ADMIN;
    @Builder.Default
    private boolean isEnabled = false;

    public String getFullName() {
        return this.firstName+" "+this.lastName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
