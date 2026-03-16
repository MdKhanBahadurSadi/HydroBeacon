package com.sadi.hydrobeacon.data.remote;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
        @Query("lat") double lat,
        @Query("lon") double lon,
        @Query("appid") String apiKey
    );

    class WeatherResponse {
        @SerializedName("rain")
        public Rain rain;

        public static class Rain {
            @SerializedName("1h")
            public double oneHour;
        }
    }
}
