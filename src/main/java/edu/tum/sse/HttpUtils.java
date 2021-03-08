package edu.tum.sse;


import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Utility functions to set up {@link Retrofit} and {@link OkHttpClient}.
 */
public class HttpUtils {


    /**
     * Controls whether {@link OkHttpClient}s built with this class will validate SSL certificates.
     */
    private static boolean shouldValidateSsl = false;

    /**
     * @see #shouldValidateSsl
     */
    public static void setShouldValidateSsl(boolean shouldValidateSsl) {
        HttpUtils.shouldValidateSsl = shouldValidateSsl;
    }

    /**
     * Creates a new {@link Retrofit} with proper defaults. The instance can be customized with the given action.
     */
    public static Retrofit createRetrofit(Consumer<Retrofit.Builder> retrofitBuilderAction) {
        return createRetrofit(retrofitBuilderAction, okHttpBuilder -> {
            // nothing to do
        });
    }

    /**
     * Creates a new {@link Retrofit} with proper defaults. The instance and the corresponding {@link OkHttpClient} can
     * be customized with the given action.
     */
    public static Retrofit createRetrofit(Consumer<Retrofit.Builder> retrofitBuilderAction,
                                          Consumer<OkHttpClient.Builder> okHttpBuilderAction) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        setDefaults(httpClientBuilder);
        okHttpBuilderAction.accept(httpClientBuilder);

        Retrofit.Builder builder = new Retrofit.Builder().client(httpClientBuilder.build());
        retrofitBuilderAction.accept(builder);
        return builder.build();
    }

    /**
     * Sets sensible defaults for the {@link OkHttpClient}.
     */
    private static void setDefaults(OkHttpClient.Builder builder) {
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);
    }

    /**
     * Returns the error body of the given response or a replacement string in case it is null.
     */
    public static <T> String getErrorBodyStringSafe(Response<T> response) throws IOException {
        ResponseBody errorBody = response.errorBody();
        if (errorBody == null) {
            return "<no response body provided>";
        }
        return errorBody.string();
    }

}
