package de.hpi3d.gamepgrog.trap.api;

import android.app.Application;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Builds a {@link ApiCall}
 * @param <R> the return type
 * @see <a href="https://android.jlelse.eu/building-your-own-retrofit-call-adapter-b198169bab69">
 *     Tutorial for CallAdapters in Retrofit</a>
 */
public class ApiCallAdapter<R> implements CallAdapter<R, ApiCall<R>> {

    private Type responseType;
    private Application app;
    private boolean uploadsData;

    public ApiCallAdapter(Type responseType, Application app, boolean uploadsData) {
        this.responseType = responseType;
        this.app = app;
        this.uploadsData = uploadsData;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public ApiCall<R> adapt(Call<R> call) {
        return new ApiCall<>(call, app, uploadsData);
    }

    /**
     * Factory for {@link CallAdapter}
     */
    public static class Factory extends CallAdapter.Factory {

        private Application app;

        private Factory(Application app) {
            this.app = app;
        }

        /**
         * Called by Retrofit on initialization for each return type
         */
        @Override
        public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
            try {
                ParameterizedType type = (ParameterizedType) returnType;
                if (type.getRawType() != ApiCall.class)
                    return null;

                boolean uploadsData = includesUploadsData(annotations);

                return new ApiCallAdapter<>(type.getActualTypeArguments()[0], app, uploadsData);
            } catch (ClassCastException e) {
                return null;
            }
        }

        private static boolean includesUploadsData(Annotation[] annotations) {
            for (Annotation a : annotations) {
                if (a instanceof UploadsData)
                    return true;
            }
            return false;
        }

        public static Factory create(Application app) {
            return new ApiCallAdapter.Factory(app);
        }
    }
}
