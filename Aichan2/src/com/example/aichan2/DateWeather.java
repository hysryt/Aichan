package com.example.aichan2;

class DateWeather {
	private int year;
	private int month;
	private int date;
	
	private int weekDay;
	
	private String strDate;
	private String wday;
	
	private String weather;
	
	private String max;
	private String min;
	
	private String prob;
	private String p0006;
	private String p0612;
	private String p1218;
	private String p1824;
	
	boolean detailProbFlg = false;
	
	
	String[] jweek = {"ì˙", "åé", "âŒ", "êÖ", "ñÿ", "ã‡", "ìy"};
	
	
	DateWeather(String strDate, String wday, String weather, String max, String min, String p0006, String p0612, String p1218, String p1824) {
		this.strDate = strDate;
		this.wday = wday;
		this.weather = weather;
		this.max = max;
		this.min = min;
		this.p0006 = p0006;
		this.p0612 = p0612;
		this.p1218 = p1218;
		this.p1824 = p1824;
		
		detailProbFlg = true;
		
		int[] pd = Util.parseDate(strDate);
		if(pd.length == 2) {
			month = pd[0];
			date = pd[1];
		} else if(pd.length == 3) {
			year = pd[0];
			month = pd[1];
			date = pd[2];
		}
		
		weekDay = Util.parseIntWeekDay(wday);
		
	}
	
	DateWeather(String strDate, String wday, String weather, String max, String min, String prob) {
		this.strDate = strDate;
		this.wday = wday;
		this.weather = weather;
		this.max = max;
		this.min = min;
		this.prob = prob;
		
		detailProbFlg = false;
		
		int[] pd = Util.parseDate(strDate);
		if(pd.length == 2) {
			month = pd[0];
			date = pd[1];
		} else if(pd.length == 3) {
			year = pd[0];
			month = pd[1];
			date = pd[2];
		}
		
		weekDay = Util.parseIntWeekDay(wday);
	}
	
	public boolean isDetailProb() {
		return detailProbFlg;
	}

	public int getMonth() {
		return month;
	}
	public int getDate() {
		return date;
	}
	
	public String getWeekDay() {
		return wday;
	}
	
	public String getWeather() {
		return weather;
	}
	
	public String getMax() {
		return max;
	}
	public String getMin() {
		return min;
	}
	
	public String getProb() {
		if(prob == null) return "--";
		return prob;
	}
	public String getProb0006() {
		if(p0006 == null) return "--";
		return p0006;
	}
	public String getProb0612() {
		if(p0612 == null) return "--";
		return p0612;
	}
	public String getProb1218() {
		if(p1218 == null) return "--";
		return p1218;
	}
	public String getProb1824() {
		if(p1824 == null) return "--";
		return p1824;
	}
	
	public String getJapanWeek() {
		if(weekDay == -1) return "ÅH";
		return jweek[weekDay];
	}
	
	
	public String toString() {
		if(isDetailProb()) {
			return month +"-"+ date +", "+ getJapanWeek() +", "+ weather +", "+ max +", "+ min +", "+ p0006 +", "+ p0612 +", "+ p1218 +", "+ p1824;
		} else {
			return month +"-"+ date +", "+ getJapanWeek() +", "+ weather +", "+ max +", "+ min +", "+ prob;
		}
	}
}
