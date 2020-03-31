package de.hpi3d.gamepgrog.trap.future;

/**
 * Brings <a href="https://developer.android.com/reference/java/util/function/Consumer">Consumer</a> to Api level < 24
 */
public interface Consumer<T> {

    void accept(T t);

    default Consumer<T> andThen(Consumer<? super T> after) {
        return t -> {
            accept(t);
            after.accept(t);
        };
    }
}
