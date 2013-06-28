package com.example.aichan2;

import java.util.Calendar;
import java.util.regex.Matcher;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

class ResponseBehavior {
	protected String resMsg;
	
	public static ResponseBehavior createInstance(Activity activity, Matcher m, String responseMessage) {
		// ���O�ɂ���Ƃ��u��
		responseMessage = preReplace(responseMessage, m);
		
		if(responseMessage.startsWith("#search#")) {			// ����
			return new SearchResponse(activity, m, responseMessage);
		} else if(responseMessage.startsWith("#beforesearch#")) {
			return new BeforeSearchResponse(activity, m, responseMessage);
		} else if(responseMessage.startsWith("#setting#")) {	// �ݒ�
			return new SettingResponse(activity, responseMessage);
		} else {
			return new NormalResponse(responseMessage, m);
		}
	}
	
	private static String preReplace(String oldResMsg, Matcher m) {
		
		//���K�\���̈ꕔ���g�p�����������b�Z�[�W(#num#��u��)
		int grpCnt = m.groupCount();
		for(int i=0; i<grpCnt; i++) {
			String repWord = "#"+ i +"#";
			oldResMsg = oldResMsg.replaceAll(repWord, m.group(i));
		}
		
		return oldResMsg;
	}
	
	public String getResponseMessage() {
		return resMsg;
	}
	
	protected void execute() {
		// ��ʓI�Ȓu��
		Calendar now = Calendar.getInstance();
		String m = Integer.toString(now.get(Calendar.MONTH) + 1);
		String n = Integer.toString(now.get(Calendar.DATE));
		
		resMsg = resMsg.replaceAll("#month#", m);
		resMsg = resMsg.replaceAll("#date#", n);

		resMsg = resMsg.replaceAll("#user#", App.getInstance().getUserName());
		resMsg = resMsg.replaceAll("#ai#", MainActivity.AI_NAME);

		setCharacterExpression();
	}
	
	/**
	 * �\���ݒ�
	 */
	protected void setCharacterExpression() {
		CharacterManager cm = CharacterManager.getInstance();
		cm.setCharacterExpression(CharacterManager.EXPRESSION_NORMAL);
	}
}

/**
 *  �ʏ�̉���
 */
class NormalResponse extends ResponseBehavior {
	NormalResponse(String responseMessage, Matcher m) {
		resMsg = responseMessage;
		
		execute();
	}
}


class NewsResponse extends ResponseBehavior {
	NewsResponse(Activity srcActivity) {
		execute(srcActivity);
	}
	
	protected void execute(Activity srcActivity) {
		super.execute();
		
		Intent intent = new Intent(srcActivity, NewsActivity.class);
		srcActivity.startActivity(intent);
	}
}


class WeatherResponse extends ResponseBehavior {
	WeatherResponse(Activity srcActivity) {
		execute(srcActivity);
	}
	
	protected void execute(Activity srcActivity) {
		super.execute();
		
		Intent intent = new Intent(srcActivity, WeatherActivity.class);
		srcActivity.startActivity(intent);
	}
}


/**
 *  �����̈�O
 */
class BeforeSearchResponse extends ResponseBehavior {
	BeforeSearchResponse(Activity activity, Matcher m, String responseMessage) {
		resMsg = responseMessage;
		
		// �����P��̒��o
		int searchWordHolder = Integer.parseInt(resMsg.split(",")[1]);
		String searchWord = m.group(searchWordHolder);
		
		// �����P��̕ۑ�
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		Editor editor = pref.edit();
		editor.putString("searchword", searchWord);
		editor.commit();
		
		DebugViewer.d("�P��ۑ�(searchword:"+ searchWord +")");
		
		// �������b�Z�[�W�̒��o
		resMsg = resMsg.split(",")[2];
		
		// �������b�Z�[�W���̓��ꕶ����u��
		resMsg = resMsg.replaceAll("#word", searchWord);
		
		execute();
	}
	
	protected void setCharacterExpression() {
		CharacterManager cm = CharacterManager.getInstance();
		cm.setCharacterExpression(CharacterManager.EXPRESSION_THINKING);
	}
}



/**
 *  �����p�̉���
 */
class SearchResponse extends ResponseBehavior {	
	SearchResponse(Activity activity, Matcher m, String responseMessage) {
		resMsg = responseMessage;
		
		// �����P��̒��o
		int searchWordHolder = Integer.parseInt(resMsg.split(",")[1]);
		String searchWord;
		if(searchWordHolder != -1) {
			searchWord = m.group(searchWordHolder);
		} else {
			// -1 �̏ꍇ�� SharedPreferences����Ƃ��Ă���
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
			searchWord = pref.getString("searchword", "");
		}
		
		// �������b�Z�[�W�̒��o
		resMsg = resMsg.split(",")[2];
		
		// �������b�Z�[�W���̓��ꕶ����u��
		resMsg = resMsg.replaceAll("#word#", searchWord);
		
		execute(activity, searchWord);
	}
	
	
	public void execute(Activity srcActivity, String searchWord) {
		super.execute();
		
		// ������ʂւ̈ړ�
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.jp/search?q="+ searchWord));
		srcActivity.startActivity(intent);

		srcActivity = null;
	}
}


/**
 *  �ݒ��ʂ̂̑J�ڂ���p�̉���
 */
class SettingResponse extends ResponseBehavior {
	SettingResponse(Activity activity, String responseMessage) {
		resMsg = responseMessage;
		
		// �������b�Z�[�W�̒��o
		resMsg = resMsg.split(",")[1];
		
		execute(activity);
	}
	
	public void execute(Activity srcActivity) {
		super.execute();
		
		// �ݒ��ʂւ̈ړ�
		Intent intent = new Intent(srcActivity, MainPreferenceActivity.class);
		srcActivity.startActivity(intent);
		
		srcActivity = null;
	}
}