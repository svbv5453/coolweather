package com.coolweather.app.debug;

import java.util.List;

import junit.framework.TestCase;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.Province;

public class Test extends TestCase{
	CoolWeatherDB coolWeatherDB;
	
	public void test()throws Exception{
		List<Province> provinceList = coolWeatherDB.loadProvince();
		System.out.println(provinceList);
	}

}
