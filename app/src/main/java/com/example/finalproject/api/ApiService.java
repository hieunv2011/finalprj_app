package com.example.finalproject.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://fprj-backend.vercel.app/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @GET("users/me")
    Call<UserResponse> getUserProfile(@Header("Authorization") String token);

    @GET("users/user-devices/{userId}")
    Call<UserDevicesResponse> getUserDevices(@Header("Authorization") String token, @Path("userId") String userId );

    @PUT("users/{userId}")
    Call<UserResponse> updateUserProfile(@Header("Authorization") String token,
                                         @Path("userId") String userId,
                                         @Body UserProfileRequest request);

    @POST("users/register")
    Call<UserResponse> register(@Body RegisterRequest request);
    @POST("users/verify-otp")
    Call<VerifyOtpResponse> verifyOtp(@Body VerifyOtpRequest otpRequest);

}
