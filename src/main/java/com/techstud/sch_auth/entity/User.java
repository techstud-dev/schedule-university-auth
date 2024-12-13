package com.techstud.sch_auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @OneToMany(mappedBy = "user", targetEntity = Role.class, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Role> roles;

    @Embedded
    private RefreshToken refreshToken;

    @Override
    public List<Role> getAuthorities() {
        return roles;
    }

}
