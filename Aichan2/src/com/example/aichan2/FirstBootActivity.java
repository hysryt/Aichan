package com.example.aichan2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FirstBootActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firstboot);
		
		Button btn = (Button)findViewById(R.id.nextButton);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// まだユーザの名前が決まってないことを通知して画面遷移
				Intent intent = new Intent(v.getContext(), MainActivity.class);
				intent.putExtra(MainActivity.EXTRA_USERNAME_IS_NOT_SET, true);
				Log.d("FirstBootActivity", intent.toString());
				startActivity(intent);
				
				// 次回からは初回起動画面を省略
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
				Editor editor = pref.edit();
				editor.putBoolean(App.FIRST_BOOT, false);
				editor.commit();
			}
		});
	}
}
