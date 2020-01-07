package de.hpi3d.gamepgrog.trap.future;

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
