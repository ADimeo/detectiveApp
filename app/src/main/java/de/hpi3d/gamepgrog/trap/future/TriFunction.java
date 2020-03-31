package de.hpi3d.gamepgrog.trap.future;

/**
 * Brings <a href="https://developer.android.com/reference/java/util/function/TriFunction">TriFunction</a> to Api level < 24
 */
public interface TriFunction<T, U, V, R> {

    R apply(T t, U u, V v);

    default <W> TriFunction<T, U, V, W> andThen(Function<? super R, ? extends W> after) {
        return (t, u, v) -> after.apply(apply(t, u, v));
    }
}
