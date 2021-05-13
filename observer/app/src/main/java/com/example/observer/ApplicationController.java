package com.example.observer;


import android.app.Application;
import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by KJH on 2017-06-06.
 */
public class ApplicationController extends Application {
    public final static String TAG = "KJH";
    private static ApplicationController instance;
    public static ApplicationController getInstance(){return instance;}

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationController.instance = this;
    }

    private NetworkService networkService;
    public NetworkService getNetworkService() {return networkService;}

    private String baseUrl;

    public void buildNetworkService(String protocol, String ip, int port){
        synchronized (ApplicationController.class){
            if (networkService == null){
                baseUrl = protocol + String.format("://%s:%d/", ip, port);
                Log.i(TAG, baseUrl);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                networkService = retrofit.create(NetworkService.class);
            }
        }
    }

    public void buildNetworkService(String protocol, String ip){
        synchronized (ApplicationController.class){
            if (networkService == null){
                baseUrl = protocol + String.format("://%s/", ip);
                Log.i(TAG, baseUrl);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                networkService = retrofit.create(NetworkService.class);
            }
        }
    }
}