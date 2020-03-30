package de.hpi3d.gamepgrog.trap.api;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import de.hpi3d.gamepgrog.trap.future.BiConsumer;
import de.hpi3d.gamepgrog.trap.future.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Wrapper for {@link Call}, build be {@link ApiCallAdapter}.
 * Executes calls asynchronously.
 * Endpoints annotated with {@link UploadsData} will not be called if safety-mode is on
 */
public class ApiCall<R> {

    private static final String TAG = "ApiCall";

    public static final int SUCCESS = 200;
    public static final int FAILURE = -1;
    public static final int ERROR_SAFETY_MODE = -2;

    private Call<R> call;
    private Application app;
    private boolean uploadsData;

    ApiCall(Call<R> call, Application app, boolean uploadsData) {
        this.call = call;
        this.app = app;
        this.uploadsData = uploadsData;
    }

    public void call() {
        call((data, code) -> {});
    }

    public void call(Runnable result) {
        call((data, code) -> result.run());
    }

    public void call(Consumer<R> result) {
        call((data, code) -> result.accept(data));
    }

    /**
     * Executes the call.<br>
     * @param result will get called with the return object and
     *               return code (HTTP-Code or FAILURE || ERROR_SAFETY_MODE).
     *               If code != {@link #SUCCESS}, value might be <code>null</code>
     */
    public void call(BiConsumer<R, Integer> result) {

        // Check safety mode
        boolean safetyMode = StorageManager.with(app).safetyMode.get();
        if (safetyMode && uploadsData) {
            Log.d(TAG, "Safety Mode is on. Blocked Data Upload");
            result.accept(null, ERROR_SAFETY_MODE);
            return;
        }

        call.enqueue(new Callback<R>() {
            @Override
            public void onResponse(Call<R> call, Response<R> response) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    result.accept(response.body(), response.code());
                });
            }

            @Override
            public void onFailure(Call<R> call, Throwable t) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    result.accept(null, FAILURE);
                });
            }
        });
    }
}
