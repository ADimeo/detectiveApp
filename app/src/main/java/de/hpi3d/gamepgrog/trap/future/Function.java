package de.hpi3d.gamepgrog.trap.future;

/**
 * Brings <a href="https://developer.android.com/reference/java/util/function/Function">Function</a> to Api level < 24
 */
public interface Function<T, R> {

    R apply(T t);

    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        return t -> after.apply(apply(t));
    }

    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        return v -> apply(before.apply(v));
    }

    static <T> Function<T, T> identity() {
        return t -> t;
    }
}
