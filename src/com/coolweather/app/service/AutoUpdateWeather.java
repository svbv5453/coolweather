package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdateWeatherReciever;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class AutoUpdateWeather extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(){
			@Override
			public void run() {
				updateWeather();
			}

			
		}.start();
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		int hours = 8*60*60*1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + hours;
		Intent updateWeatherIntent = new Intent(AutoUpdateWeather.this, AutoUpdateWeatherReciever.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, updateWeatherIntent, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	private void updateWeather() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = sp.getString("weatherCode", null);
		String address = "http://www.weather.com.cn/data/cityinfo/" +
				weatherCode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if(!TextUtils.isEmpty(response)){
					Utility.handlerWeatherResponse(AutoUpdateWeather.this, response);
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				
			}
		});
		
		
	}

}
