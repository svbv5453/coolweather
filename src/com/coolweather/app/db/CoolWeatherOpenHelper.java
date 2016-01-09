package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	private static final String CREATER_PROVINCE = "create table Privince ("
			+ "id integer primary key autoincrement, "
			+ "province_name text, "
			+ "province_code text)";
	private static final String CREATE_CITY = "create table City ("
			+ "id interger primary key autoincrement, "
			+ "city_name text, "
			+ "city_code text, "
			+ "province_id integer)";
	private static final String CREATER_COUNTY = "create table County ("
			+ "id integer primary key autoincrement, "
			+ "county_name text, "
			+ "county_code text, "
			+ "city_id integer)";

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATER_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATER_COUNTY);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
