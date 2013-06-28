package com.example.aichan2;

import java.util.List;

public class WeatherObject {
	String prefecture;
	String region;
	String city;
	
	List<DateWeather> dateList;
	List<DateWeather> weekList;
	
	WeatherObject(String prefecture, String region, String city, List<DateWeather> dateList, List<DateWeather> weekList) {
		this.prefecture = prefecture;
		this.region = region;
		this.city = city;
		this.dateList = dateList;
		this.weekList = weekList;
	}
	
	public String getPrefecture() {
		return prefecture;
	}
	
	public String getRegion() {
		return region;
	}
	
	public String getCity() {
		return city;
	}
	
	public List<DateWeather> getDateList() {
		return dateList;
	}
	
	public List<DateWeather> getWeekList() {
		return weekList;
	}
}
