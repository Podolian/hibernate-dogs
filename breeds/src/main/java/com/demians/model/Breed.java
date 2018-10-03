package com.demians.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "name")
@Entity
@Table(name = "breed")
public class Breed {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "average_weight", nullable = false)
    private Integer averageWeight;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "recomended_nickname")
    private String recomendedNickname;

    @ManyToMany(mappedBy = "breeds", fetch = FetchType.LAZY)
    @Setter(AccessLevel.PRIVATE)
    private Set<Vocation> vocations = new HashSet<>();

}
