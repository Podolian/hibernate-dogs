package com.demians.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "vocation")
public class Vocation {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "mission", nullable = false, unique = true)
    private String mission;

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "vocation_breed",
            joinColumns = @JoinColumn(name = "vocation_id"),
            inverseJoinColumns = @JoinColumn(name = "breed_id")
    )
    private Set<Breed> breeds = new HashSet<>();

    public void addBreed(Breed breed){
        breeds.add(breed);
        breed.getVocations().add(this);
    }

    public void removeBreed(Breed breed){
        breeds.remove(breed);
        breed.getVocations().remove(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vocation)) return false;
        Vocation objVocation = (Vocation) obj;
        return Objects.equals(id, objVocation.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
