package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class CoolWeatherDB {
	private static final int version = 1;
	private static CoolWeatherDB coolWeatherDB;
	private static final String DB_NAME = "cool_weather";
	private static SQLiteDatabase db;
	
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, "DB_NAME", null, version);
		db = dbHelper.getWritableDatabase();
		
		
	}
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
			return coolWeatherDB;
		}else{
			return coolWeatherDB;
		}
	}
	public void saveProvince(Province province){
		if(province != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			db.insert("Province", null, contentValues);
		}
	}
	public List<Province> loadProvince(){
		List<Province> provinceList = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String provinceName = cursor.getString(cursor.getColumnIndex("province_name"));
				String provinceCode = cursor.getString(cursor.getColumnIndex("province_code"));
				
				Province province = new Province(id, provinceName, provinceCode);
				provinceList.add(province);
			}while(cursor.moveToNext());
			
		}
		return provinceList;
	}
	public void saveCity(City city){
		if(city != null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId());
			db.insert("City", null, contentValues);
		}
		
	}
	public List<City> loadCity(){
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = db.query("City", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				cityList.add(city);
			}while(cursor.moveToNext());
		}
		return cityList;
	}
	public void saveCounty(County county){
		ContentValues contentValues = new ContentValues();
		contentValues.put("county_name", county.getCountyName());
		contentValues.put("county_code", county.getCountyCode());
		contentValues.put("city_id", county.getCityId());
		db.insert("County", null, contentValues);
	}
	public List<County> loadCounty(){
		List<County> countyList = new ArrayList<County>();
		Cursor cursor = db.query("County", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				countyList.add(county);
			}while(cursor.moveToNext());
		}
		return countyList;
	}

}
