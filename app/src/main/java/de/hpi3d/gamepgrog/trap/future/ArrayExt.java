package de.hpi3d.gamepgrog.trap.future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Brings some stream methods to Api < 24
 */
public class ArrayExt {

    public static List<Integer> toIntList(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int value : array) {
            list.add(value);
        }
        return list;
    }

    /**
     * This is needed because Parcels only accepts ArrayList, not Arrays#ArrayList
     */
    public static <T> List<T> toArrayList(T ... original) {
        List<T> list = new ArrayList<>();
        for (T value : original) {
            list.add(value);
        }
        return list;
    }

    public static <T> boolean allMatch(T[] list, Predicate<? super T> predicate) {
        return allMatch(Arrays.asList(list), predicate);
    }

    public static <T> boolean allMatch(List<T> list, Predicate<? super T> predicate) {
        for (T elem : list) {
            if (!predicate.test(elem)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean anyMatch(T[] list, Predicate<? super T> predicate) {
        return anyMatch(Arrays.asList(list), predicate);
    }

    public static <T> boolean anyMatch(List<T> list, Predicate<? super T> predicate) {
        for (T elem : list) {
            if (predicate.test(elem)) {
                return true;
            }
        }
        return false;
    }

    public static <T> void forEvery(T[] list, Consumer<? super T> consumer) {
        forEvery(Arrays.asList(list), consumer);
    }

    public static <T> void forEvery(List<T> list, Consumer<? super T> consumer) {
        for (T elem : list) {
            consumer.accept(elem);
        }
    }

    public static <T> T find(List<T> values, Predicate<? super T> finder) {
        for (T elem : values) {
            if (finder.test(elem)) {
                return elem;
            }
        }
        return null;
    }

    public static <T, V> List<V> map(List<T> list, Function<T, V> converter) {
        List<V> converted = new ArrayList<>();
        for (T value : list) {
            converted.add(converter.apply(value));
        }
        return converted;
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> matches = new ArrayList<>();
        for (T value : list) {
            if (predicate.test(value)) {
                matches.add(value);
            }
        }
        return matches;
    }
}
