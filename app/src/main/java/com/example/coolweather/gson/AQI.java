package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {

    public AQIcity city;
    public class AQIcity{
        @SerializedName("aqi")
        public String aqi;

        @SerializedName("pm25")
        public String pm25;
    }

}
