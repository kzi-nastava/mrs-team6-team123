
package com.example.mobile_application.service;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    //private static final String ADDRESS = "http://10.0.2.2:8080/";
    private static final String ADDRESS = "http://192.168.0.34:8080/"; // Ana
    private static TokenManager tokenManager;

    // Call this once in MainActivity.onCreate()
    public static void init(Context context) {
        tokenManager = new TokenManager(context);
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();
                        if (tokenManager != null && tokenManager.getToken() != null) {
                            builder.addHeader("Authorization",
                                    "Bearer " + tokenManager.getToken());
                        }
                        return chain.proceed(builder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(ADDRESS)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static TokenManager getTokenManager() { return tokenManager; }

    public static void resetClient() { retrofit = null; }
}