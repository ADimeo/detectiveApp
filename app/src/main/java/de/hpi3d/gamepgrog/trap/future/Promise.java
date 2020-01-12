package de.hpi3d.gamepgrog.trap.future;

/**
 * A Promise us used to make async returns.
 * <br>
 * <h3>Call a method returning a promise with:</h3>
 * <code>asyncMethod(yourParams).then((result) -> {});</code><br>
 * <code>then</code> will get executed once when the asyncMethod finishes.
 * There is no guaranty that it will ever get executed.
 * <h3>A method can return a promise like this:</h3>
 * <pre>
 * {@code
 * public Promise<String> asyncMethod(yourParams) {<br>
 *     Promise<String> p = Promise.create();<br>
 *     asyncCall((value) -> {p.resolve("Finished")});<br>
 *     return p;<br>
 * }
 * }
 * </pre>
 * @param <T> The result type
 */
public class Promise<T> {

    private T result;
    private boolean executed = false;
    private Consumer<T> consumer = null;

    private Promise() {}

    /**
     * call this once to return a value to the caller.
     * Further calls will be ignored
     * @param result the result to pass to the caller
     */
    public void resolve(T result) {
        if (executed) return;

        executed = true;
        this.result = result;

        if (consumer != null) {
            consumer.accept(result);
        }
    }

    /**
     * The given consumer will get called when the called method finishes with its return value
     * @param consumer Handle the return value
     */
    public void then(Consumer<T> consumer) {
        this.consumer = consumer;
        if (executed) {
            consumer.accept(result);
        }
    }

    /**
     * Creates a new Promise
     * @param <T> The return values type
     * @return A new Promise
     */
    public static <T> Promise<T> create() {
        return new Promise<>();
    }

    /**
     * Creates a new Promise ans resolves it
     * @param result The result of the method
     * @param <T> The type of the result
     * @return A new Promise
     */
    public static <T> Promise<T> createResolved(T result) {
        Promise<T> p = create();
        p.resolve(result);
        return p;
    }
}
