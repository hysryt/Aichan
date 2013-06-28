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
		
		// ����N�����ǂ������m�F
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean mFirstBoot = pref.getBoolean(App.FIRST_BOOT, true);
		
		// ����N�����ǂ����ɂ���ĉ�ʑJ�ڂ𕪊�
		if(mFirstBoot) {
			// ����N�����p��ʂ�
			Intent intent = new Intent(this, FirstBootActivity.class);
			startActivity(intent);
		} else {
			// ���C����ʂ�
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
}
