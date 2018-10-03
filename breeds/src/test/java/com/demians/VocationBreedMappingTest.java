package com.demians;

import com.demians.model.Breed;
import com.demians.model.Vocation;
import com.demians.util.DogsDataGenerator;
import com.demians.util.EntityManagerUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import javax.persistence.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

public class VocationBreedMappingTest {
    private static EntityManagerUtil emUtil;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("DogBreed");
        emUtil = new EntityManagerUtil(entityManagerFactory);
    }

    @AfterAll
    static void destroy() {
        entityManagerFactory.close();
    }


    @Test
    public void testSaveBreedOnly() {
        Breed breed = DogsDataGenerator.createRandomBreed();

        emUtil.performWithinTx(entityManager -> entityManager.persist(breed));

        assertThat(breed.getId(), notNullValue());
    }

    @Test
    public void testSaveBreedWithoutName() {
        Breed breed = DogsDataGenerator.createRandomBreed();
        breed.setName(null);
        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(breed));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(PersistenceException.class));
        }
    }

    @Test
    public void testSaveBreedWithDuplicateName() {
        Breed breed = DogsDataGenerator.createRandomBreed();
        emUtil.performWithinTx(entityManager -> entityManager.persist(breed));
        Breed breedWithDuplicateName = DogsDataGenerator.createRandomBreed();
        breedWithDuplicateName.setName(breed.getName());

        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(breedWithDuplicateName));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(RollbackException.class));
        }
    }

    @Test
    public void testSaveVocationOnly() {
        Vocation vocation = DogsDataGenerator.createRandomVocation();

        emUtil.performWithinTx(entityManager -> entityManager.persist(vocation));

        assertThat(vocation.getId(), notNullValue());
    }

    @Test
    public void testSaveVocationWithoutMission() {
        Vocation vocationWithNullFirstName = DogsDataGenerator.createRandomVocation();
        vocationWithNullFirstName.setMission(null);
        try {
            emUtil.performWithinTx(entityManager -> entityManager.persist(vocationWithNullFirstName));
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e.getClass(), equalTo(PersistenceException.class));
        }
    }

    @Test
    public void testAddNewBreedForExistingVocation() {
        Vocation vocation = DogsDataGenerator.createRandomVocation();
        emUtil.performWithinTx(entityManager -> entityManager.persist(vocation));

        Breed breed = DogsDataGenerator.createRandomBreed();
        emUtil.performWithinTx(entityManager -> {
            Vocation managedVocation = entityManager.find(Vocation.class, vocation.getId());
            managedVocation.addBreed(breed);
        });

        assertThat(breed.getId(), notNullValue());
        emUtil.performWithinTx(entityManager -> {
            Breed managedBreed = entityManager.find(Breed.class, breed.getId());
            assertThat(managedBreed.getVocations(), hasItem(vocation));
        });
    }


    @Test
    public void testAddVocationToExistingBreed() {
        Breed breed = DogsDataGenerator.createRandomBreed();
        emUtil.performWithinTx(entityManager -> entityManager.persist(breed));

        Vocation vocation = DogsDataGenerator.createRandomVocation();
        emUtil.performWithinTx(entityManager -> {
            Breed managedBreed = entityManager.merge(breed);
            vocation.addBreed(managedBreed);
            entityManager.persist(vocation);
        });

        assertThat(vocation.getId(), notNullValue());
        emUtil.performWithinTx(entityManager -> {
            Vocation managedVocation = entityManager.find(Vocation.class, vocation.getId());
            assertThat(managedVocation.getBreeds(), hasItem(breed));
        });
    }

    @Test
    public void testSaveNewVocationWithCoupleNewBreeds() {
        Vocation vocation = DogsDataGenerator.createRandomVocation();
        List<Breed> breedList = DogsDataGenerator.createListOfRandomBreeds(3);
        breedList.forEach(vocation::addBreed);

        emUtil.performWithinTx(entityManager -> entityManager.persist(vocation));

        assertThat(vocation.getId(), notNullValue());
        assertThat(vocation.getBreeds(), containsInAnyOrder(breedList.toArray()));
        breedList.forEach(breed -> assertThat(breed.getVocations(),hasItem(vocation)));
        emUtil.performWithinTx(entityManager -> {
            Vocation managedVocation = entityManager.find(Vocation.class, vocation.getId());
            assertThat(managedVocation.getBreeds(), containsInAnyOrder(breedList.toArray()));
        });
    }

    @Test
    public void testRemoveBreedFromVocation() {
        Vocation vocation = DogsDataGenerator.createRandomVocation();
        Breed breed = DogsDataGenerator.createRandomBreed();
        vocation.addBreed(breed);
        emUtil.performWithinTx(entityManager -> entityManager.persist(vocation));

        emUtil.performWithinTx(entityManager -> {
            Vocation managedVocation = entityManager.find(Vocation.class, vocation.getId());
            managedVocation.removeBreed(breed);
        });

        assertThat(breed.getVocations(), not(hasItem(vocation)));
        emUtil.performWithinTx(entityManager -> {
            Breed managedBreed = entityManager.find(Breed.class, breed.getId());
            assertThat(managedBreed.getVocations(), not(hasItem(vocation)));
        });
    }

    @Test
    public void testRemoveVocation() {
        Vocation vocation = DogsDataGenerator.createRandomVocation();
        Breed breed = DogsDataGenerator.createRandomBreed();
        vocation.addBreed(breed);
        emUtil.performWithinTx(entityManager -> entityManager.persist(vocation));

        emUtil.performWithinTx(entityManager -> {
            Vocation managedVocation = entityManager.merge(vocation);
            entityManager.remove(managedVocation);
        });

        emUtil.performWithinTx(entityManager -> {
            Vocation foundAccount = entityManager.find(Vocation.class, vocation.getId());
            assertThat(foundAccount, nullValue());

            Breed managedBreed = entityManager.find(Breed.class, breed.getId());
            assertThat(managedBreed.getVocations(), not(hasItem(vocation)));
        });
    }

    @Test
    public void testRemoveBreed() {
        Vocation vocation = DogsDataGenerator.createRandomVocation();
        Breed breed = DogsDataGenerator.createRandomBreed();
        vocation.addBreed(breed);
        emUtil.performWithinTx(entityManager -> entityManager.persist(vocation));

        emUtil.performWithinTx(entityManager -> {
            Breed managedBreed = entityManager.merge(breed);
            managedBreed.getVocations().forEach(a -> a.removeBreed(managedBreed));
            entityManager.remove(managedBreed);
        });

        emUtil.performWithinTx(entityManager -> {
            Breed foundBreed= entityManager.find(Breed.class, breed.getId());
            assertThat(foundBreed, nullValue());

            Vocation managedVocation = entityManager.find(Vocation.class, vocation.getId());
            assertThat(managedVocation.getBreeds(), not(hasItem(breed)));
        });
    }

    @Test
    public void testBreedSetVocationsIsPrivate() throws NoSuchMethodException {
        assertThat(Breed.class.getDeclaredMethod("setVocations", Set.class).getModifiers(), equalTo(Modifier.PRIVATE));
    }

    @Test
    public void testVocationSetBreedsIsPrivate() throws NoSuchMethodException {
        assertThat(Vocation.class.getDeclaredMethod("setBreeds", Set.class).getModifiers(), equalTo(Modifier.PRIVATE));
    }

    @Test
    public void testVocationBreedLinkTableHasCorrectName() throws NoSuchFieldException {
        Field breedsField = Vocation.class.getDeclaredField("breeds");
        JoinTable joinTable = breedsField.getAnnotation(JoinTable.class);

        assertThat(joinTable.name(), equalTo("vocation_breed"));
    }

    @Test
    public void testLinkTableHasCorrectForeignKeyColumnNameToVocation() throws NoSuchFieldException {
        Field breedsField = Vocation.class.getDeclaredField("breeds");
        JoinTable joinTable = breedsField.getAnnotation(JoinTable.class);

        assertThat(joinTable.joinColumns()[0].name(), equalTo("vocation_id"));
    }

    @Test
    public void testLinkTableHasCorrectForeignKeyColumnNameToBreed() throws NoSuchFieldException {
        Field breedsField = Vocation.class.getDeclaredField("breeds");
        JoinTable joinTable = breedsField.getAnnotation(JoinTable.class);

        assertThat(joinTable.inverseJoinColumns()[0].name(), equalTo("breed_id"));
    }

}
