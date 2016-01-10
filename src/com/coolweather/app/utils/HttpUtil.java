package com.coolweather.app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.PendingIntent.OnFinished;

public class HttpUtil {
	
	
	public static void sendHttpRequest(final String address, final HttpCallBackListener listener){
		new Thread(){
			@Override
			public void run() {
				HttpURLConnection conn = null;
				try {
					URL trueUrl = new URL(address);
					conn = (HttpURLConnection) trueUrl.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(8000);
					conn.setConnectTimeout(8000);
					InputStream inputStream = conn.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder response = new StringBuilder();
					String line = "";
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					if(listener != null){
						listener.onFinish(response.toString());
					}
				
				} catch (Exception e) {
					if(listener != null){
						listener.onError(e);
					}
				}finally{
					if(conn != null){
						conn.disconnect();
					}
				}
			}
		}.start();
	}

}
