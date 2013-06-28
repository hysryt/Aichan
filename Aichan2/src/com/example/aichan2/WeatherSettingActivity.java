package com.example.aichan2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class WeatherSettingActivity extends Activity {
	Spinner todofukenSpinner;
	Spinner areaSpinner;
	TypedArray areaCodeArray;

	String[] areaValueArray;
	String areaCode = "";
	
	int[]  areaStringArrayIdArray = {
	        R.array.hokkaido_area_string,
	        R.array.aomori_area_string,
	        R.array.akita_area_string,
	        R.array.iwate_area_string,
	        R.array.yamagata_area_string,
	        R.array.miyagi_area_string,
	        R.array.fukusima_area_string,
	        R.array.ibaraki_area_string,
	        R.array.totigi_area_string,
	        R.array.gunma_area_string,
	        R.array.saitama_area_string,
	        R.array.tokyo_area_string,
	        R.array.kanagawa_area_string,
	        R.array.tiba_area_string,
	        R.array.sizuoka_area_string,
	        R.array.yamanasi_area_string,
	        R.array.nigata_area_string,
	        R.array.nagano_area_string,
	        R.array.toyama_area_string,
	        R.array.isikawa_area_string,
	        R.array.fukui_area_string,
	        R.array.gifu_area_string,
	        R.array.aiti_area_string,
	        R.array.mie_area_string,
	        R.array.siga_area_string,
	        R.array.kyoto_area_string,
	        R.array.osaka_area_string,
	        R.array.nara_area_string,
	        R.array.wakayama_area_string,
	        R.array.hyogo_area_string,
	        R.array.okayama_area_string,
	        R.array.tottori_area_string,
	        R.array.hirosima_area_string,
	        R.array.simane_area_string,
	        R.array.yamaguti_area_string,
	        R.array.kagawa_area_string,
	        R.array.tokusima_area_string,
	        R.array.ehime_area_string,
	        R.array.kouti_area_string,
	        R.array.fukuoka_area_string,
	        R.array.saga_area_string,
	        R.array.nagasaki_area_string,
	        R.array.oita_area_string,
	        R.array.kumamoto_area_string,
	        R.array.miyazaki_area_string,
	        R.array.kagosima_area_string,
	        R.array.okinawa_area_string,
	};
	
	
	int[]  areaValueArrayIdArray = {
	        R.array.hokkaido_area_value,
	        R.array.aomori_area_value,
	        R.array.akita_area_value,
	        R.array.iwate_area_value,
	        R.array.yamagata_area_value,
	        R.array.miyagi_area_value,
	        R.array.fukusima_area_value,
	        R.array.ibaraki_area_value,
	        R.array.totigi_area_value,
	        R.array.gunma_area_value,
	        R.array.saitama_area_value,
	        R.array.tokyo_area_value,
	        R.array.kanagawa_area_value,
	        R.array.tiba_area_value,
	        R.array.sizuoka_area_value,
	        R.array.yamanasi_area_value,
	        R.array.nigata_area_value,
	        R.array.nagano_area_value,
	        R.array.toyama_area_value,
	        R.array.isikawa_area_value,
	        R.array.fukui_area_value,
	        R.array.gifu_area_value,
	        R.array.aiti_area_value,
	        R.array.mie_area_value,
	        R.array.siga_area_value,
	        R.array.kyoto_area_value,
	        R.array.osaka_area_value,
	        R.array.nara_area_value,
	        R.array.wakayama_area_value,
	        R.array.hyogo_area_value,
	        R.array.okayama_area_value,
	        R.array.tottori_area_value,
	        R.array.hirosima_area_value,
	        R.array.simane_area_value,
	        R.array.yamaguti_area_value,
	        R.array.kagawa_area_value,
	        R.array.tokusima_area_value,
	        R.array.ehime_area_value,
	        R.array.kouti_area_value,
	        R.array.fukuoka_area_value,
	        R.array.saga_area_value,
	        R.array.nagasaki_area_value,
	        R.array.oita_area_value,
	        R.array.kumamoto_area_value,
	        R.array.miyazaki_area_value,
	        R.array.kagosima_area_value,
	        R.array.okinawa_area_value,
	};
	
	
	int areaPos = 0;
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_weather_setting);
		
		todofukenSpinner = (Spinner)findViewById(R.id.todofuken_spinner);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.todofuken_string)) {
    		@Override
    		public View getView (int position, View convertView, ViewGroup parent) {
    			View view = super.getView(position, convertView, parent);
    			Util.setFont(view);
    			return view;
    		}
    		
    		@Override
    		public View getDropDownView (int position, View convertView, ViewGroup parent) {
    			View view = super.getDropDownView(position, convertView, parent);
    			Util.setFont(view);
    			return view;
    		}
		};
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		todofukenSpinner.setAdapter(adapter);
		
		todofukenSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// 地域スピナーをセット
				setAreaSpinnerEntities();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		areaSpinner = (Spinner)findViewById(R.id.area_spinner);
		areaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//地域コードを取得
				areaCode = getAreaCode();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		areaCodeArray = getResources().obtainTypedArray(R.array.todofuken_value);

		// 現在設定されているデータを取得し、スピナーの選択位置を合わせる
		loadSetting();
	}
	
	private void setAreaSpinnerEntities() {
		int todofukenCode = todofukenSpinner.getSelectedItemPosition();
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(areaStringArrayIdArray[todofukenCode])){
    		@Override
    		public View getView (int position, View convertView, ViewGroup parent) {
    			View view = super.getView(position, convertView, parent);
    			Util.setFont(view);
    			return view;
    		}
    		
    		@Override
    		public View getDropDownView (int position, View convertView, ViewGroup parent) {
    			View view = super.getDropDownView(position, convertView, parent);
    			Util.setFont(view);
    			return view;
    		}
		};
		
		areaValueArray = getResources().getStringArray(areaValueArrayIdArray[todofukenCode]);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		areaSpinner.setAdapter(adapter);
		
		// 起動時用
		areaSpinner.setSelection(areaPos);
		areaPos = 0;
	}
	
	private void loadSetting() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		int todofukenPos = pref.getInt("todofukenPos", 0);

		areaPos = pref.getInt("areaPos", 0);
		todofukenSpinner.setSelection(todofukenPos);
		
		// ここでareaSpinner.setSelectionをしても
		// todofukenSpinner.onItemSelected　が遅れて実行された場合、
		// その中の setAreaSpinnerEntities　で　areaSpinner　のセレクションが初期化してしまうので
		// それを防ぐためその処理は setAreaSpinnerEntities の中にいれた
	}
	
	private String getAreaCode() {
		String areaCodeString = areaValueArray[areaSpinner.getSelectedItemPosition()];
		return areaCodeString;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// 選択された都道府県と地域をセーブ
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putInt("todofukenPos", todofukenSpinner.getSelectedItemPosition());
		editor.putInt("areaPos", areaSpinner.getSelectedItemPosition());
		editor.putString("weatherAreaCode", areaCode);
		editor.commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		areaCodeArray.recycle();
	}
}
