package com.techstud.sch_auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "universities")
public class University {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "university_id_seq")
    @SequenceGenerator(name = "university_id_seq", sequenceName = "university_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    public University(String university) {
        this.name = university;
    }
}
