package de.hpi3d.gamepgrog.trap.future;

/**
 * Brings <a href="https://developer.android.com/reference/java/util/function/Supplier">Supplier</a> to Api level < 24
 */
public interface Supplier<T> {
    T get();
}
