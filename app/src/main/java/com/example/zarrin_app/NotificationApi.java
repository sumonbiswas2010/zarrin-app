package com.example.zarrin_app;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NotificationApi {
    @POST("/v1/generate")
    Call<ApiResponse> sendNotifications(@Body List<NotificationEntity> notifications);
}
