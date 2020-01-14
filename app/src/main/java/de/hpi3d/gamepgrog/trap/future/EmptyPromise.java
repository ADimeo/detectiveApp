package de.hpi3d.gamepgrog.trap.future;

/**
 * A Promise us used to make async returns.
 * <br>
 * <h3>Call a method returning a promise with:</h3>
 * <code>asyncMethod(yourParams).then(() -> {});</code><br>
 * <code>then</code> will getOrDefault executed once when the asyncMethod finishes.
 * There is no guaranty that it will ever getOrDefault executed.
 * <h3>A method can return a promise like this:</h3>
 * <pre>
 * {@code
 * public EmptyPromise asyncMethod(yourParams) {<br>
 *     EmptyPromise p = EmptyPromise.create();<br>
 *     asyncCall((value) -> {p.resolve()});<br>
 *     return p;<br>
 * }
 * }
 * </pre>
 */
public class EmptyPromise {

    private boolean executed = false;
    private Runnable callback = null;

    private EmptyPromise() {}

    /**
     * call this once to return to the caller.
     * Further calls will be ignored
     */
    public void resolve() {
        if (executed) return;

        executed = true;

        if (callback != null) {
            callback.run();
        }
    }

    /**
     * The given consumer will getOrDefault called when the called method finishes with its return value
     * @param callback Called when finished
     */
    public void then(Runnable callback) {
        this.callback = callback;
        if (executed) {
            callback.run();
        }
    }

    /**
     * Creates a new Promise
     * @return A new EmptyPromise
     */
    public static EmptyPromise create() {
        return new EmptyPromise();
    }
}
