package com.example.aichan2;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	String nameSpace = "http://www.weathermap.co.jp/rss/ns/forecast.dtd";

	String url = "http://feeds.feedburner.com/hitokuchi_";
	String areaCode = "1100";
	
	LinearLayout dateLayout;
	LinearLayout weatherLayout;
	
	LinearLayout progressLayout;
	LinearLayout mainLayout;
	LinearLayout errorLayout;
	TextView errorMessageView;
	Button retryButton;
	
	Weather weather;
	
	LayoutInflater inflater;
	LinearLayout dayLayout;
	LinearLayout weekdayLayout;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		inflater = getLayoutInflater();
		dayLayout = (LinearLayout)findViewById(R.id.dayLayout);
		weekdayLayout = (LinearLayout)findViewById(R.id.weekdayLayout);
		
		setLayout();
		
		retryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				visibleProgressLayout();
				getWeather();
			}
		});
		
		visibleProgressLayout();
		getWeather();
	}
	
	private void getWeather() {
		if(weather == null) {
			weather = new Weather();
			
	        Weather.Callback callback = new Weather.Callback() {
	        	@Override
	        	public void onError(WeatherError error) {
	        		setErrorData(error);
	        		
	        		visibleErrorLayout();
	        	}
	        	
	        	@Override
	        	public void onFinish(WeatherObject wObject) {
	        		setWeatherData(wObject);
	        		
	        		visibleWeatherLayout();
	        	}
	        };
			
	        weather.setCallback(callback);
		}

		weather.getWeather();
	}
	
	private void setErrorData(WeatherError error) {
		errorMessageView.setText(error.getMessage());
	}
	
	private void setWeatherData(WeatherObject wObject) {
		List<DateWeather> dList = wObject.getDateList();
		List<DateWeather> wList = wObject.getWeekList();
		
		WeatherIconManager iconManager = new WeatherIconManager(this);
		
		// レイアウトのリセット
		dayLayout.removeAllViews();
		weekdayLayout.removeAllViews();
		
		// 地域表示
		((TextView)findViewById(R.id.placeView))
			.setText(wObject.getPrefecture() +" "+ wObject.getRegion() +"（"+ wObject.getCity() +"）");
		
		// 直近2日間の詳細予報の表示
		for(int i=0; i<dList.size(); i++) {
			// 天気情報取得
			DateWeather d = dList.get(i);
			
			// xmlファイルからViewを生成
			View view = inflater.inflate(R.layout.weather_day, null);
			
			// 情報をセット
			((TextView)view.findViewById(R.id.date)).setText(d.getMonth() +"月"+ d.getDate() +"日（"+ d.getJapanWeek() +"曜日）");
			((ImageView)view.findViewById(R.id.icon)).setImageDrawable(iconManager.getIcon(d.getWeather()));
			((TextView)view.findViewById(R.id.weather)).setText(d.getWeather());
			((TextView)view.findViewById(R.id.temp)).setText("高"+ d.getMax() +"℃/低"+ d.getMin() +"℃");
			((TextView)view.findViewById(R.id.rain0006)).setText(d.getProb0006() +"%");
			((TextView)view.findViewById(R.id.rain0612)).setText(d.getProb0612() +"%");
			((TextView)view.findViewById(R.id.rain1218)).setText(d.getProb1218() +"%");
			((TextView)view.findViewById(R.id.rain1824)).setText(d.getProb1824() +"%");
			
			// レイアウトに追加
			dayLayout.addView(view);
		}
		
		
		// 詳細表示した日の予報を週刊予報から削除
		for(int i=0; i<dList.size(); i++) {
			wList.remove(0);
		}
		
		
		// 週間予報の表示
		for(int i=0; i<wList.size(); i++) {
			// 天気情報取得
			DateWeather d = wList.get(i);

			// xmlファイルからViewを生成
			View view = inflater.inflate(R.layout.weather_weekday, null);
			
			//　情報をセット
			((TextView)view.findViewById(R.id.date)).setText(d.getMonth() +"/"+ d.getDate() +"("+ d.getJapanWeek() +")");
			((ImageView)view.findViewById(R.id.icon)).setImageDrawable(iconManager.getIcon(d.getWeather()));
			((TextView)view.findViewById(R.id.weather)).setText(d.getWeather());
			((TextView)view.findViewById(R.id.temp)).setText("高"+ d.getMax() +"℃/低"+ d.getMin() +"℃");
			((TextView)view.findViewById(R.id.rain)).setText(d.getProb() +"%");
			
			// レイアウトに追加
			weekdayLayout.addView(view);
		}
		
		// フォント変更
		setFont(findViewById(R.id.root));
	}
	
	private void visibleWeatherLayout() {
		progressLayout.setVisibility(View.GONE);
		errorLayout.setVisibility(View.GONE);
		mainLayout.setVisibility(View.VISIBLE);
	}
	
	private void visibleProgressLayout() {
		errorLayout.setVisibility(View.GONE);
		mainLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.VISIBLE);
	}
	
	private void visibleErrorLayout() {
		progressLayout.setVisibility(View.GONE);
		mainLayout.setVisibility(View.GONE);
		errorLayout.setVisibility(View.VISIBLE);
	}
	
    
    private void setLayout() {    	
		progressLayout = (LinearLayout)findViewById(R.id.progressLayout);
		mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
		
		errorLayout = (LinearLayout)findViewById(R.id.errorLayout);
		errorMessageView = (TextView)findViewById(R.id.errorMessageView);
		retryButton = (Button)findViewById(R.id.retryButton);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.action_settings);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getApplicationContext(), WeatherSettingActivity.class);
				startActivityForResult(intent, 100);
				return false;
			}
		});
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		resetLayout();
		
		visibleProgressLayout();
		getWeather();
	}
	
	private void resetLayout() {
//		dateLayout.removeAllViews();
//		weatherLayout.removeAllViews();
//		weekTempLayout.removeAllViews();
//		weekRainfallLayout.removeAllViews();
	}
	
	private void setFont(View view) {
		if(view instanceof ViewGroup) { 
			ViewGroup vg = (ViewGroup)view;
			int cnt = vg.getChildCount();
			for(int i=0; i<cnt; i++) {
				setFont(vg.getChildAt(i));
			}
		} else if(view instanceof TextView) {
			((TextView)view).setTypeface(App.getInstance().getFont());
		}
	}
}

class DateWeatherView {
	TextView dateView;
	TextView weatherView;
	TextView tempView;
	TextView[] rainView = new TextView[4];
	
	public DateWeatherView setDateView(TextView dateView) {
		this.dateView = dateView;
		return this;
	}
	
	public DateWeatherView setWeatherView(TextView weatherView) {
		this.weatherView = weatherView;
		return this;
	}
	
	public DateWeatherView setTempView(TextView tempView) {
		this.tempView = tempView;
		return this;
	}
	
	public DateWeatherView setRainView(TextView a, TextView b, TextView c, TextView d) {
		rainView[0] = a;
		rainView[1] = b;
		rainView[2] = c;
		rainView[3] = d;
		return this;
	}
	
	public DateWeatherView setRainView(TextView a) {
		rainView[0] = a;
		return this;
	}
	
	public DateWeatherView setDateText(String t) {
		if(dateView != null) dateView.setText(t);
		return this;
	}
	
	public DateWeatherView setWeatherText(String t) {
		if(weatherView != null) weatherView.setText(t);
		return this;
	}
	
	public DateWeatherView setTempText(String t) {
		if(tempView != null) tempView.setText(t);
		return this;
	}
	
	public DateWeatherView setRainText(String a, String b, String c, String d) {
		rainView[0].setText(a);
		rainView[1].setText(b);
		rainView[2].setText(c);
		rainView[3].setText(d);
		return this;
	}
	
	public DateWeatherView setRainText(String a) {
		rainView[0].setText(a);
		return this;
	}
}