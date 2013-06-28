package com.example.aichan2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;

public class Mike {
	/*
	 *  �����F���p�C���e���g�̔��s
	 */
	public void start(Activity caller, int requestCode) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "�ȂɁH");
		
		caller.startActivityForResult(intent, requestCode);
	}
	
	/*
	 *  onActivityResult���󂯎����data���當�����ArrayList�����o��
	 */
	public ArrayList<String> getStringArrayList(Intent data) {
		return data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	}
	
	/*
	 *  onActivityResult���󂯎����data�����ԍŏ��̕���������o��
	 */
	public String getString(Intent data) {
		ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		for(int i=0; i<result.size(); i++) {
			Log.i("Mike", result.get(i));
		}
		if(result != null && result.size() != 0) {
			return result.get(0);
		}
		return null;
	}
}