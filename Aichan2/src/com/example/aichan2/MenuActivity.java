package com.example.aichan2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		Button weatherButton = (Button)findViewById(R.id.tenkiButton);
		weatherButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), WeatherActivity.class);
				startActivity(intent);
			}
		});
		
		Button newsButton = (Button)findViewById(R.id.newsButton);
		newsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), NewsActivity.class);
				startActivity(intent);
			}
		});
		
		Button settingButton = (Button)findViewById(R.id.setteiButton);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), WeatherSettingActivity.class);
				startActivity(intent);
			}
		});
		
		Button addButton = (Button)findViewById(R.id.tuikaButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), NewWordActivity.class);
				startActivity(intent);
			}
		});
		
		Util.setFont(findViewById(R.id.root));
	}
}
