package com.example.aichan2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;

public class Mike {
	/*
	 *  音声認識用インテントの発行
	 */
	public void start(Activity caller, int requestCode) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "なに？");
		
		caller.startActivityForResult(intent, requestCode);
	}
	
	/*
	 *  onActivityResultが受け取ったdataから文字列のArrayListを取り出す
	 */
	public ArrayList<String> getStringArrayList(Intent data) {
		return data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	}
	
	/*
	 *  onActivityResultが受け取ったdataから一番最初の文字列を取り出す
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