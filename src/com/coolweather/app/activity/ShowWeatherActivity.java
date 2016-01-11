package com.coolweather.app.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateWeather;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ShowWeatherActivity extends Activity implements OnClickListener {
	
	private Button switchCity;
	private Button refreshWeather;
	private TextView weatherDesp;
	private TextView temp1;
	private TextView temp2;
	private TextView cityName;
	private TextView publishTime;
	private TextView currentTime;
	
	private LinearLayout weatherInfoLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		Intent intent = getIntent();
		String countyCode = intent.getStringExtra("county_code");
		queryWeatherCode(countyCode);
		
		weatherDesp = (TextView)findViewById(R.id.weather_desp);
		temp1 = (TextView)findViewById(R.id.temp1);
		temp2 = (TextView)findViewById(R.id.temp2);
		cityName = (TextView)findViewById(R.id.city_name);
		publishTime = (TextView)findViewById(R.id.publish_text);
		currentTime = (TextView)findViewById(R.id.current_date);
		weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
		switchCity = (Button)findViewById(R.id.switch_city);
		refreshWeather = (Button)findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
		
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishTime.setText("同步中..");
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = sp.getString("weatherCode", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
		
	}
	private void queryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city" +
				countyCode + ".xml";
		queryFromServer(address, "countyCode");
		
	}
	
	private void queryFromServer(String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						String weatherCode = array[1];
						queryWeatherInfo(weatherCode);
					}
				} else if("weatherCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						Utility.handlerWeatherResponse(ShowWeatherActivity.this, response);
						runOnUiThread(new Runnable(){

							@Override
							public void run() {
								showWeather();
								
							}

							
							
						});
					}
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						publishTime.setText("同步失败");
						
					}
					
				});
				
			}
		});
		
		
	}
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" +
				weatherCode + ".html";
		queryFromServer(address, "weatherCode");
		
	}
	private void showWeather() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String cityName1 = sp.getString("cityName", "");
		String weatherDesp1 = sp.getString("weatherDesp", null);
		String temp11 = sp.getString("temp1", null);
		String temp21 = sp.getString("temp2", null);
		String publishTime1 = sp.getString("ptime", null);
		String currentTime1 = sp.getString("currentDate", null);
		cityName.setText(cityName1);
		weatherDesp.setText("今天的天气是" + weatherDesp1);
		temp1.setText(temp11);
		temp2.setText(temp21);
		publishTime.setText("发布时间是" + publishTime1);
		currentTime.setText("当前时间是" + currentTime1);
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityName.setVisibility(View.VISIBLE);
		Intent intent = new Intent(ShowWeatherActivity.this, AutoUpdateWeather.class);
		startService(intent);
		
	}

}
