package com.demians.dao;

import com.demians.model.Breed;
import com.demians.model.Vocation;
import com.demians.util.EntityManagerUtil;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

public class DogsDaoImpl implements DogsDao {
    private static final String FIND_BY_NAME_QRY = "select b from Breed b where b.name = ?1";
    private static final String FIND_BREED_SUITABLE_TO_SQL = "select v from Vocation v join fetch v.breeds where v.mission = ?1";
    private EntityManagerFactory entityManagerFactory;
    private EntityManagerUtil emUtil;

    public DogsDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.emUtil = new EntityManagerUtil(entityManagerFactory);
    }


    @Override
    public void save(Breed breed) {
        emUtil.performWithinTx(entityManager -> entityManager.persist(breed));
    }

    @Override
    public Breed findById(Long id) {
        return emUtil.performReturningWithinTx(entityManager -> entityManager.find(Breed.class, id));
    }

    @Override
    public Set<Breed> findBreedsSuitableTo(String mission) {
        return emUtil.performReturningWithinTx(entityManager -> {
            TypedQuery<Vocation> findSuitablebreedQuery = entityManager.createQuery(FIND_BREED_SUITABLE_TO_SQL, Vocation.class);
            findSuitablebreedQuery.setParameter(1, mission);
            return findSuitablebreedQuery.getSingleResult().getBreeds();
        });
    }

    @Override
    public Breed findByName(String name) {
        return emUtil.performReturningWithinTx(entityManager -> {
            TypedQuery<Breed> findByNameQuery = entityManager.createQuery(FIND_BY_NAME_QRY, Breed.class);
            findByNameQuery.setParameter(1, name);
            return findByNameQuery.getSingleResult();
        });
    }

    @Override
    public List<Breed> findAll() {
        return emUtil.performReturningWithinTx(entityManager -> entityManager
                .createQuery("select b from Breed b", Breed.class)
                .getResultList()
        );
    }

    @Override
    public void update(Breed breed) {
        emUtil.performWithinTx(entityManager -> entityManager.merge(breed));
    }

    @Override
    public void remove(Breed breed) {
        emUtil.performWithinTx(entityManager -> {
            Breed removeBreed = entityManager.merge(breed);
            entityManager.remove(removeBreed);
        });
    }
}
