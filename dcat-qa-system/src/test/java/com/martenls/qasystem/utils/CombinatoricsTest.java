package com.martenls.qasystem.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CombinatoricsTest {



    @Test
    void getAllCombsAndPermsOf1ListElements() {
        List<Integer> collection = Lists.newArrayList(1,2,3);
        List<List<Integer>> result = Combinatorics.getAllCombsAndPermsOfKListElements(collection, 1);
        assertThat(result, hasSize(3));
        Integer[][] array = {{1}, {2}, {3}};
        assertThat(result, equalTo(Arrays.stream(array).map(Arrays::asList).collect(Collectors.toList())));
    }

    @Test
    void getAllCombsAndPermsOf2ListElements() {
        List<Integer> collection = Lists.newArrayList(1,2,3);
        List<List<Integer>> result = Combinatorics.getAllCombsAndPermsOfKListElements(collection, 2);
        assertThat(result, hasSize(6));
        Integer[][] array = {{1, 2}, {2, 1}, {1, 3}, {3, 1}, {2, 3}, {3, 2}};
        assertThat(result, equalTo(Arrays.stream(array).map(Arrays::asList).collect(Collectors.toList())));
    }

    @Test
    void getAllCombsAndPermsOf3ListElements() {
        List<Integer> collection = Lists.newArrayList(1,2,3);
        List<List<Integer>> result = Combinatorics.getAllCombsAndPermsOfKListElements(collection, 3);
        assertThat(result, hasSize(6));
        Integer[][] array = {{1, 2, 3}, {1, 3, 2}, {3, 1, 2}, {3, 2, 1}, {2, 3, 1}, {2, 1, 3}};
        assertThat(result, equalTo(Arrays.stream(array).map(Arrays::asList).collect(Collectors.toList())));
    }

    @Test
    void getAllCombsAndPermsOf4ListElements() {
        List<Integer> collection = Lists.newArrayList(1,2,3);
        assertThrows(NumberIsTooLargeException.class, () -> Combinatorics.getAllCombsAndPermsOfKListElements(collection, 4));
    }

    @Test
    void getCombinations3OutOf2() {
        List<List<Integer>> result = Combinatorics.getCombinationsKOutOfN(3,2).stream().map(ArrayUtils::toObject).map(Arrays::asList).collect(Collectors.toList());
        assertThat(result, hasSize(3));
        Integer[][] array = {{0, 1}, {0, 2}, {1, 2}};
        assertThat(result, equalTo(Arrays.stream(array).map(Arrays::asList).collect(Collectors.toList())));
    }

    @Test
    void getCombinations5OutOf5() {
        List<List<Integer>> result = Combinatorics.getCombinationsKOutOfN(5,5).stream().map(ArrayUtils::toObject).map(Arrays::asList).collect(Collectors.toList());
        assertThat(result, hasSize(1));
        Integer[][] array = {{0, 1, 2, 3, 4}};
        assertThat(result, equalTo(Arrays.stream(array).map(Arrays::asList).collect(Collectors.toList())));
    }

    @Test
    void getCombinations4OutOf5() {
        assertThrows(NumberIsTooLargeException.class, () -> Combinatorics.getCombinationsKOutOfN(4,5));
    }
}