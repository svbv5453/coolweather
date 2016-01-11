package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
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
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 鐪佸垪琛�
	 */
	private List<Province> provinceList;
	/**
	 * 甯傚垪琛�
	 */
	private List<City> cityList;
	/**
	 * 鍘垮垪琛�
	 */
	private List<County> countyList;
	/**
	 * 閫変腑鐨勭渷浠�
	 */
	private Province selectedProvince;
	/**
	 * 閫変腑鐨勫煄甯�
	 */
	private City selectedCity;
	/**
	 * 褰撳墠閫変腑鐨勭骇鍒�
	 */
	private int currentLevel;
	/**
	 * 鏄惁浠嶹eatherActivity涓烦杞繃鏉ャ�
	 */
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}*/
		
		Boolean isFromShowWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		if(sp.getBoolean("citySelected", false) && !isFromShowWeatherActivity){
			Intent intent = new Intent (ChooseAreaActivity.this, ShowWeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				} else if(currentLevel == LEVEL_COUNTY){
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, ShowWeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
				
			}
		});
		queryProvinces();  // 鍔犺浇鐪佺骇鏁版嵁
	}

	/**
	 * 鏌ヨ鍏ㄥ浗鎵�湁鐨勭渷锛屼紭鍏堜粠鏁版嵁搴撴煡璇紝濡傛灉娌℃湁鏌ヨ鍒板啀鍘绘湇鍔″櫒涓婃煡璇€�
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 鏌ヨ閫変腑鐪佸唴鎵�湁鐨勫競锛屼紭鍏堜粠鏁版嵁搴撴煡璇紝濡傛灉娌℃湁鏌ヨ鍒板啀鍘绘湇鍔″櫒涓婃煡璇€�
	 */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}
	
	/**
	 * 鏌ヨ閫変腑甯傚唴鎵�湁鐨勫幙锛屼紭鍏堜粠鏁版嵁搴撴煡璇紝濡傛灉娌℃湁鏌ヨ鍒板啀鍘绘湇鍔″櫒涓婃煡璇€�
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	
	/**
	 * 鏍规嵁浼犲叆鐨勪唬鍙峰拰绫诲瀷浠庢湇鍔″櫒涓婃煡璇㈢渷甯傚幙鏁版嵁銆�
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// 閫氳繃runOnUiThread()鏂规硶鍥炲埌涓荤嚎绋嬪鐞嗛�杈�
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 閫氳繃runOnUiThread()鏂规硶鍥炲埌涓荤嚎绋嬪鐞嗛�杈�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
										"加载失败...", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * 鏄剧ず杩涘害瀵硅瘽妗�
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("请稍后，正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 鍏抽棴杩涘害瀵硅瘽妗�
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 鎹曡幏Back鎸夐敭锛屾牴鎹綋鍓嶇殑绾у埆鏉ュ垽鏂紝姝ゆ椂搴旇杩斿洖甯傚垪琛ㄣ�鐪佸垪琛ㄣ�杩樻槸鐩存帴閫�嚭銆�
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else{
			finish();
		}
		/*else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}*/
			
		}
	}

