package de.hpi3d.gamepgrog.trap.future;

/**
 * A Promise us used to make async returns.
 * <br>
 * <h3>Call a method returning a promise with:</h3>
 * <code>asyncMethod(yourParams).then((result) -> {});</code><br>
 * <code>then</code> will getOrDefault executed once when the asyncMethod finishes.
 * There is no guaranty that it will ever getOrDefault executed.
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
    private int errorCode;
    private boolean executed = false;
    private boolean hadError = false;
    private Consumer<T> consumer = null;
    private Consumer<Integer> errorConsumer = null;

    protected Promise() {}

    /**
     * call this once to return a value to the caller.
     * Further calls will be ignored
     * @param result the result to pass to the caller
     */
    public Promise<T> resolve(T result) {
        if (executed || hadError) return this;

        executed = true;
        this.result = result;

        if (consumer != null) {
            consumer.accept(result);
        }

        return this;
    }

    public Promise<T> throwError(int errorCode) {
        if (executed || hadError) return this;

        executed = true;
        hadError = true;
        this.errorCode = errorCode;

        if (errorConsumer != null) {
            errorConsumer.accept(errorCode);
        }

        return this;
    }

    /**
     * The given consumer will getOrDefault called when the called method finishes with its return value
     * @param consumer Handle the return value
     */
    public Promise<T> then(Consumer<T> consumer) {
        this.consumer = consumer;
        if (executed && !hadError) {
            consumer.accept(result);
        }
        return this;
    }

    public Promise<T> error(Consumer<Integer> errorConsumer) {
        this.errorConsumer = errorConsumer;
        if (hadError) {
            errorConsumer.accept(errorCode);
        }
        return this;
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
