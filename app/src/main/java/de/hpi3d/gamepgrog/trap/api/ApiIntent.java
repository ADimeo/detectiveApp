package de.hpi3d.gamepgrog.trap.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

import org.parceler.Parcels;

import java.util.function.BiConsumer;

import retrofit2.Response;


/**
 * Used to send data and start a {@link ApiService}.<br>
 * Supports method chaining.<br>
 * Uses {@link Parcels} Lib to transfer objects
 */
public class ApiIntent {

    private Intent intent;

    @Nullable
    private Context context;

    ApiIntent(Intent intent) {
        this.intent = intent;
    }

    private ApiIntent(Intent intent, Context context) {
        this.intent = intent;
        this.context = context;
    }

    // ------------ METHODS FOR THE SERVICE ------------

    <T> T getExtra(String key) {
        return Parcels.unwrap(intent.getParcelableExtra(key));
    }

    String getManagerName() {
        return getExtra(ApiService.KEY_CALL);
    }

    ApiIntent sendBack(int code) {
        ResultReceiver receiver = intent.getParcelableExtra(ApiService.KEY_RECEIVER);
        if (receiver != null) {
            receiver.send(code, new Bundle());
        }
        return this;
    }

    <T> ApiIntent sendBack(Response<T> response) {
        ResultReceiver receiver = intent.getParcelableExtra(ApiService.KEY_RECEIVER);
        if (receiver != null) {
            Bundle b = new Bundle();

            if (response.isSuccessful())
                b.putParcelable(ApiService.KEY_RESULT, Parcels.wrap(response.body()));

            receiver.send(response.code(), b);
        }
        return this;
    }

    // ------------ METHODS FOR THE ACTIVITY ------------

    public <T> ApiIntent put(String key, T value) {
        intent.putExtra(key, Parcels.wrap(value));
        return this;
    }

    public ApiIntent setCall(String call) {
        return put(ApiService.KEY_CALL, call);
    }

    public ApiIntent putReceiver(BiConsumer<Integer, Bundle> receiver) {
        if (intent != null) {
            intent.putExtra(ApiService.KEY_RECEIVER, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    receiver.accept(resultCode, resultData);
                }
            });
        }
        return this;
    }

    public void start() {
        if (context != null)
            context.startService(intent);
    }

    public static ApiIntent build(Context context) {
        return new ApiIntent(new Intent(context, ApiService.class), context);
    }

    public static <T> T getResult(Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(ApiService.KEY_RESULT));
    }
}
