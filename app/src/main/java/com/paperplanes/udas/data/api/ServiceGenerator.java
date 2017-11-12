package com.paperplanes.udas.data.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by abdularis on 31/10/17.
 */

public class ServiceGenerator {

    private static final String BASE_API_URL = "http://192.168.0.102:5000/api/";

    private static OkHttpClient sHttpClient = new OkHttpClient.Builder().build();

    private static Retrofit sRetrofit =
            new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(sHttpClient)
                    .build();

    public static <S>
    S createService(Class<S> serviceClass) {
        return sRetrofit.create(serviceClass);
    }

}
