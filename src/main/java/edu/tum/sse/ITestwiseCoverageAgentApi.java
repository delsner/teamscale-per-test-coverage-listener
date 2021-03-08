package edu.tum.sse;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ITestwiseCoverageAgentApi {

    /**
     * Generates a {@link Retrofit} instance for this service.
     */
    static ITestwiseCoverageAgentApi createService(HttpUrl baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        return retrofit.create(ITestwiseCoverageAgentApi.class);
    }

    /**
     * Test start.
     */
    @POST("test/start/{testUniformPath}")
    Call<ResponseBody> testStarted(@Path("testUniformPath") String testUniformPath);

    /**
     * Test finished.
     */
    @POST("test/end/{testUniformPath}")
    Call<ResponseBody> testFinished(
            @Path("testUniformPath") String testUniformPath
    );

    /**
     * Test finished.
     */
    @POST("test/end/{testUniformPath}")
    Call<ResponseBody> testFinished(
            @Path("testUniformPath") String testUniformPath,
            @Body TestExecution testExecution
    );
}
