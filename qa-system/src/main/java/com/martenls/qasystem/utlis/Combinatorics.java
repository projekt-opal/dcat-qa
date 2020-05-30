package com.martenls.qasystem.utlis;

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Combinatorics {

    /**
     * Generates a list of all possible k element sublists of the given list and all their permutations.
     * For example for list=[1,2,3] and k=2 -> [[1, 2], [2, 1], [1, 3], [3, 1], [2, 3], [3, 2]]
     * @param list to get sublists of
     * @param k the number of elements in sublists
     * @param <T> type of list objects
     * @return a list of all possible k element sublists of the given list and all their permutations.
     */
    public static <T> List<List<T>> getAllCombsAndPermsOfKListElements(List<T> list, int k) {
        // first get list of all combinations of indices and transform into list of list of elements with according indices
        List<List<T>> results = getCombinationsKOutOfN(list.size(), k)
                .stream()
                .map(x -> Arrays.stream(x).mapToObj(list::get).collect(Collectors.toList()))
                .collect(Collectors.toList());
        // then add all permutations of all sublists and return
        return results.stream()
                .map(x ->  StreamSupport.stream(Spliterators.spliteratorUnknownSize(new PermutationIterator<>(x), Spliterator.ORDERED), false)
                        .collect(Collectors.toUnmodifiableList()))
                .flatMap(List::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<int[]> getCombinationsKOutOfN(int n, int k) {
        List<int[]> combinations =  new ArrayList<>();
        Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(n, k);
        while (iterator.hasNext()) {
            combinations.add(iterator.next());
        }
        return combinations;
    }
}
