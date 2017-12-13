package com.paperplanes.unma.data.network.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by abdularis on 31/10/17.
 */

public class WebServiceGenerator {

    private static final String BASE_API_URL = "http://abdularis.pythonanywhere.com/api/";

    private Retrofit mRetrofit;

    public WebServiceGenerator(OkHttpClient httpClient) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build();
    }

    public <Type>
    Type createService(Class<Type> serviceClass) {
        return mRetrofit.create(serviceClass);
    }

}
