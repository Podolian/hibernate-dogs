package com.demians.util;

import com.demians.model.Breed;
import com.demians.model.Vocation;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DogsDataGenerator {

    public static Breed createRandomBreed() {
        Breed breed = new Breed();
        breed.setName(RandomStringUtils.randomAlphabetic(20));
        breed.setAverageWeight(RandomUtils.nextInt());
        breed.setOrigin(RandomStringUtils.randomAlphabetic(8));
        breed.setRecomendedNickname(RandomStringUtils.randomAlphabetic(4));
        return breed;
    }

    public static List<Breed> createListOfRandomBreeds(int size){
        return Stream.generate(DogsDataGenerator::createRandomBreed).limit(size).collect(Collectors.toList());
    }


    public static Vocation createRandomVocation() {
        Vocation vocation = new Vocation();
        vocation.setMission(RandomStringUtils.randomAlphabetic(20));
        return vocation;
    }

    public static List<Vocation> createRandomListOfVocations(int size){
        return Stream.generate(DogsDataGenerator::createRandomVocation).limit(size).collect(Collectors.toList());
    }
}
