package de.hpi3d.gamepgrog.trap.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

import org.parceler.Parcels;

import java.util.function.BiConsumer;
import java.util.prefs.BackingStoreException;

import retrofit2.Response;

public class BackendIntent {

    private Intent intent;

    @Nullable
    private Context context;

    public BackendIntent(Intent intent) {
        this.intent = intent;
    }

    private BackendIntent(Intent intent, Context context) {
        this.intent = intent;
        this.context = context;
    }

    public <T> T getExtra(String key) {
        return Parcels.unwrap(intent.getParcelableExtra(key));
    }

    public String getManagerName() {
        return getExtra(ApiService.KEY_MANAGER);
    }

    public <T> BackendIntent sendBack(Response<T> response) {
        ResultReceiver receiver = intent.getParcelableExtra(ApiService.KEY_RECEIVER);
        if (receiver != null) {
            Bundle b = new Bundle();

            if (response.isSuccessful())
                b.putParcelable(ApiService.KEY_RESULT, Parcels.wrap(response.body()));

            receiver.send(response.code(), b);
        }
        return this;
    }

    public <T> BackendIntent put(String key, T value) {
        intent.putExtra(key, Parcels.wrap(value));
        return this;
    }

    public BackendIntent setManager(String manager) {
        return put(ApiService.KEY_MANAGER, manager);
    }

    public BackendIntent putReceiver(BiConsumer<Integer, Bundle> receiver) {
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

    public BackendIntent putReceiver(Runnable callback) {
        return putReceiver((code, bundle) -> callback.run());
    }

    public void start() {
        if (context != null)
            context.startService(intent);
    }

    public static BackendIntent build(Context context) {
        return new BackendIntent(new Intent(context, ApiService.class), context);
    }
}
