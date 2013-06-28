package com.example.aichan2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class BootBranchActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 初回起動かどうかを確認
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean mFirstBoot = pref.getBoolean(App.FIRST_BOOT, true);
		
		// 初回起動かどうかによって画面遷移を分岐
		if(mFirstBoot) {
			// 初回起動時用画面へ
			Intent intent = new Intent(this, FirstBootActivity.class);
			startActivity(intent);
		} else {
			// メイン画面へ
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
}
