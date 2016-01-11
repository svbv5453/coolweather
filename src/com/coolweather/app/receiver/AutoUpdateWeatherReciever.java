package com.coolweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

import com.coolweather.app.service.AutoUpdateWeather;

public class AutoUpdateWeatherReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AutoUpdateWeather.class);
		context.startService(i);
		

	}

}
