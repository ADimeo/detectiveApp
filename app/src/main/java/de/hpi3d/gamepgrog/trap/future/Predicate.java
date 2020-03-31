package de.hpi3d.gamepgrog.trap.future;

import java.util.Objects;

/**
 * Brings <a href="https://developer.android.com/reference/java/util/function/Predicate">Predicate</a> to Api level < 24
 */
public interface Predicate<T> {

    boolean test(T t);

    default Predicate<T> and(Predicate<? super T> other) {
        return t -> test(t) && other.test(t);
    }

    default Predicate<T> or(Predicate<? super T> other) {
        return t -> test(t) || other.test(t);
    }

    default Predicate<T> negate() {
        return t -> !test(t);
    }

    static <T> Predicate<T> isEqual(Object targetRef) {
        return t -> Objects.equals(targetRef, t);
    }
}
