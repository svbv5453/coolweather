package com.coolweather.app.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.utils.HttpCallBackListener;
import com.coolweather.app.utils.HttpUtil;
import com.coolweather.app.utils.Utility;


public class ChooseAreaActivity extends Activity {
	
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;
	private int currentLevel;
	
	private TextView titleText = null;
	private ListView listView = null;
	
	private CoolWeatherDB coolWeatherDB;
	
	private List<String> dataList = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	
	private Province selectedProvince;
	private City selectedCity;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		titleText = (TextView)findViewById(R.id.textView);
		listView = (ListView)findViewById(R.id.listView);
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, dataList);
		
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(currentLevel == LEVEL_PROVINCE){
					List<Province> provinceList = new ArrayList<Province>();
					selectedProvince = provinceList.get(position);
					queryCity();
					
				}else if(currentLevel == LEVEL_CITY){
					List<City> cityList = new ArrayList<City>();
					selectedCity = cityList.get(position);
					queryCounty();
					
				}
				
			}

			

			
		});
		
		queryProvince();
	}

	private void queryProvince() {
	
		List<Province> provinceList = coolWeatherDB.loadProvince();
		System.out.println(provinceList);
	    if(provinceList.size()>0){
	    	dataList.clear();
	    	for (Province province : provinceList) {
				
				dataList.add(province.getProvinceName());
			}
	    	adapter.notifyDataSetChanged();
	    	listView.setSelection(0);
	    	titleText.setText("中国");
	    	currentLevel = LEVEL_PROVINCE;
	    }else{
	    	queryFromServer(null, "province");
	    }
		
		
		
	}
	private void queryCity() {
		List<City> cityList = coolWeatherDB.loadCity(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
		
	}
	

	private void queryCounty() {
		List<County> countyList = coolWeatherDB.loadCounty(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	private void queryFromServer(String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code +
					".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result =false;
				if("province".equals(type)){
					result = Utility.handlerProvinceResponse(coolWeatherDB, response);
				}else if("city".equals(type)){
					result = Utility.handlerCityResponse(coolWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handlerCountyResponse(coolWeatherDB, response, selectedCity.getId());
				}
				if(result){
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
							}else if("city".equals(type)){
								queryCity();
							}else if("county".equals(type)){
								queryCounty();
							}
							
						}
						
					});
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
						
					}

					
				});
				
			}
		});
		
	}

	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("请稍后，正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
			
		}
		progressDialog.show();
			
		}
	

	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
		
	}
	@Override
	public void onBackPressed() {
		if(currentLevel == LEVEL_COUNTY){
			queryCity();
		}else if(currentLevel == LEVEL_CITY){
			queryProvince();
		}else {
			finish();
		}
	}
		
	}
	


