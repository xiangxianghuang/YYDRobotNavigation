package com.yongyida.robot.http;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 */
public class MediaRetrofit {

    private static final String TAG = MediaRetrofit.class.getSimpleName() ;


    private final static long DEFAULT_TIMEOUT = 5;
    private static ApiService mApiService;
    private Context mContext;

    public static ApiService getApiService(Context context) {
        if (mApiService == null) {
            synchronized (MediaRetrofit.class) {
                if (mApiService == null) {
                    new MediaRetrofit(context);
                }
            }
        }
        return mApiService;
    }

    private MediaRetrofit(Context context) {
        this.mContext = context;
        Retrofit retrofit = new Retrofit.Builder()
                .client(getOkHttpClient())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(getServerHost())
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    private OkHttpClient getOkHttpClient() {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String s) {
                Log.e("HttpLoggingInterceptor",s);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addNetworkInterceptor(logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY));
        return httpClientBuilder.build();
    }

    private String url = "10.7.5.88:8080" ;
    private String getServerHost() {

        return url ;
    }


}
