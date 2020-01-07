package de.hpi3d.gamepgrog.trap.future;

public interface Consumer<T> {

    void accept(T t);

    default Consumer<T> andThen(Consumer<? super T> after) {
        return t -> {
            accept(t);
            after.accept(t);
        };
    }
}
