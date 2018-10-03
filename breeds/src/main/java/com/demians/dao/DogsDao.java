package com.demians.dao;

import com.demians.model.Breed;

import java.util.List;
import java.util.Set;

public interface DogsDao {
    /**
     * Receives a new instance of {@link Breed} and stores it into database. Sets a generated id to breed.
     *
     * @param breed new instance of breed
     */
    void save(Breed breed);

    /**
     * Returns an {@link Breed} instance by its id
     *
     * @param id breed id in the database
     * @return breed instance
     */
    Breed findById(Long id);

    /**
     * Returns all breeds, that fit to  mission of the vocation.
     *
     * @param mission stored breed instance
     * @return breed list
     */
    Set<Breed> findBreedsSuitableTo(String mission);

    /**
     * Returns {@link Breed} instance by its name
     *
     * @param name breed name
     * @return breed instance
     */
    Breed findByName(String name);

    /**
     * Returns all breeds stored in the database.
     *
     * @return breed list
     */
    List<Breed> findAll();

    /**
     * Receives stored {@link Breed} instance and updates it in the database
     *
     * @param breed stored breed with updated fields
     */
    void update(Breed breed);

    /**
     * Removes the stored breed from the database.
     *
     * @param breed stored breed instance
     */
    void remove(Breed breed);

}
