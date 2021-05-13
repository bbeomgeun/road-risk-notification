package com.example.observer;

import com.example.observer.model.Image;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NetworkService {
    @POST("/observer/image/")
    Call<Image> post_image(@Body Image image);

}