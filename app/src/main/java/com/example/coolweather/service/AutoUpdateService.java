package com.example.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = 8*60*60*1000;
        long triggerTime = SystemClock.elapsedRealtime()+time;
        Intent intent1 = new Intent(this,AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = share.getString("weather",null);
        if(weatherString != null){
            Weather weather = Utility.handWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String url = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    Weather weather1 = Utility.handWeatherResponse(responseBody);
                    if(weather1 != null && "ok".equals(weather1.status)){
                        SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        sharedPreferences.putString("weather",responseBody);
                        sharedPreferences.apply();
                    }
                }
            });
        }
    }

    private void updateBingPic(){
        String url = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPicImg = response.body().string();
                SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                sharedPreferences.putString("bing_pic",bingPicImg);
                sharedPreferences.apply();
            }
        });
    }
}
