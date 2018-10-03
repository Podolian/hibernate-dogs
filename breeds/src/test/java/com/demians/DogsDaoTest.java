package com.demians;

import com.demians.dao.DogsDaoImpl;
import com.demians.model.Breed;
import com.demians.model.Vocation;
import com.demians.util.DogsDataGenerator;
import com.demians.util.EntityManagerUtil;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DogsDaoTest {
    private static EntityManagerFactory emf;
    private static DogsDaoImpl dogsDao;
    private static EntityManagerUtil emUtil;

    @BeforeEach
    public void setup() {
        emf = Persistence.createEntityManagerFactory("DogBreed");
        dogsDao = new DogsDaoImpl(emf);
        emUtil = new EntityManagerUtil(emf);
    }

    @AfterEach
    public void destroy() {
        emf.close();
    }

    @Test
    public void testSaveBread() {
        Breed breed = DogsDataGenerator.createRandomBreed();

        dogsDao.save(breed);

        Breed foundBreed = emUtil.performReturningWithinTx(entityManager -> entityManager.find(Breed.class, breed.getId()));
        assertThat(foundBreed, equalTo(breed));
    }

    @Test
    public void testFindBreedById() {
        Breed breed = DogsDataGenerator.createRandomBreed();
        emUtil.performWithinTx(entityManager -> entityManager.persist(breed));

        Breed foundBreed = dogsDao.findById(breed.getId());

        assertThat(foundBreed, equalTo(breed));
    }

    @Test
    public void testFindBreedSuitableTo() {
        Vocation theVocation = DogsDataGenerator.createRandomVocation();
        Breed theBreed = DogsDataGenerator.createRandomBreed();
        theVocation.addBreed(theBreed);

        emUtil.performWithinTx(entityManager -> entityManager.persist(theVocation));
        System.out.println(theBreed);

        String theMission = theVocation.getMission();

        assertThat(dogsDao.findBreedsSuitableTo(theMission), contains(theBreed));
    }

    @Test
    public void testFindBreedByName(){
        Breed breed = DogsDataGenerator.createRandomBreed();
        emUtil.performWithinTx(entityManager -> entityManager.persist(breed));

        Breed foundBreed = dogsDao.findByName(breed.getName());

        assertThat(foundBreed, equalTo(breed));
    }

    @Test
    public void testFindAllBreeds() {
        List<Breed> listOfRandomBreeds = DogsDataGenerator.createListOfRandomBreeds(5);
        emUtil.performWithinTx(entityManager -> listOfRandomBreeds.forEach(entityManager::persist));

        List<Breed> foundBreeds = dogsDao.findAll();

        foundBreeds.stream().map(Breed::getId).forEach(System.out::println);
        System.out.println("---");
        listOfRandomBreeds.stream().map(Breed::getId).forEach(System.out::println);

        assertThat(foundBreeds, containsInAnyOrder(listOfRandomBreeds.toArray()));
    }

    @Test
    public void testUpdateBreed(){
        Breed breed = DogsDataGenerator.createRandomBreed();
        emUtil.performWithinTx(entityManager -> entityManager.persist(breed));
        String newName = "Xoloitzcuintli";
        breed.setName(newName);

        dogsDao.update(breed);
        Breed newBreed = dogsDao.findByName(newName);

        assertThat(breed, equalTo(newBreed));
    }

    @Test
    public void testRemoveBreed() {
        Breed breed = DogsDataGenerator.createRandomBreed();
        emUtil.performWithinTx(entityManager -> entityManager.persist(breed));

        dogsDao.remove(breed);

        Breed removedBreed = emUtil.performReturningWithinTx(entityManager -> entityManager.find(Breed.class, breed.getId()));
        assertThat(removedBreed, nullValue());
    }


}
