package de.hpi3d.gamepgrog.trap.future;

/**
 * Brings <a href="https://developer.android.com/reference/java/util/function/TriConsumer">TriConsumer</a> to Api level < 24
 */
public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);

    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        return (t, u, v) -> {
            accept(t, u, v);
            after.accept(t, u, v);
        };
    }
}
