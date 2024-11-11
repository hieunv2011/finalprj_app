package com.example.finalproject.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    @GET("data/2.5/weather")  // Sử dụng API mới
    Call<WeatherResponse> getWeather(
            @Query("lat") double lat,  // Vĩ độ
            @Query("lon") double lon,  // Kinh độ
            @Query("appid") String apiKey  // API Key từ OpenWeatherMap
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")  // URL của OpenWeatherMap API
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
