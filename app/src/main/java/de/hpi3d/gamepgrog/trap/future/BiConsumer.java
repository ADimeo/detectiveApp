package de.hpi3d.gamepgrog.trap.future;

/**
 * Brings <a href="https://developer.android.com/reference/java/util/function/BiConsumer">BiConsumer</a> to Api level < 24
 */
public interface BiConsumer<T, U> {

    void accept(T t, U u);

    default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) {
        return (t, u) -> {
            accept(t, u);
            after.accept(t, u);
        };
    }
}
