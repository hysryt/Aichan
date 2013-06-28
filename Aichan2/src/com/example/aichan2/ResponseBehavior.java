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
		// 事前にやっとく置換
		responseMessage = preReplace(responseMessage, m);
		
		if(responseMessage.startsWith("#search#")) {			// 検索
			return new SearchResponse(activity, m, responseMessage);
		} else if(responseMessage.startsWith("#beforesearch#")) {
			return new BeforeSearchResponse(activity, m, responseMessage);
		} else if(responseMessage.startsWith("#setting#")) {	// 設定
			return new SettingResponse(activity, responseMessage);
		} else {
			return new NormalResponse(responseMessage, m);
		}
	}
	
	private static String preReplace(String oldResMsg, Matcher m) {
		
		//正規表現の一部を使用した応答メッセージ(#num#を置換)
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
		// 一般的な置換
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
	 * 表情を設定
	 */
	protected void setCharacterExpression() {
		CharacterManager cm = CharacterManager.getInstance();
		cm.setCharacterExpression(CharacterManager.EXPRESSION_NORMAL);
	}
}

/**
 *  通常の応答
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
 *  検索の一つ前
 */
class BeforeSearchResponse extends ResponseBehavior {
	BeforeSearchResponse(Activity activity, Matcher m, String responseMessage) {
		resMsg = responseMessage;
		
		// 検索単語の抽出
		int searchWordHolder = Integer.parseInt(resMsg.split(",")[1]);
		String searchWord = m.group(searchWordHolder);
		
		// 検索単語の保存
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		Editor editor = pref.edit();
		editor.putString("searchword", searchWord);
		editor.commit();
		
		DebugViewer.d("単語保存(searchword:"+ searchWord +")");
		
		// 応答メッセージの抽出
		resMsg = resMsg.split(",")[2];
		
		// 応答メッセージ内の特殊文字を置換
		resMsg = resMsg.replaceAll("#word", searchWord);
		
		execute();
	}
	
	protected void setCharacterExpression() {
		CharacterManager cm = CharacterManager.getInstance();
		cm.setCharacterExpression(CharacterManager.EXPRESSION_THINKING);
	}
}



/**
 *  検索用の応答
 */
class SearchResponse extends ResponseBehavior {	
	SearchResponse(Activity activity, Matcher m, String responseMessage) {
		resMsg = responseMessage;
		
		// 検索単語の抽出
		int searchWordHolder = Integer.parseInt(resMsg.split(",")[1]);
		String searchWord;
		if(searchWordHolder != -1) {
			searchWord = m.group(searchWordHolder);
		} else {
			// -1 の場合は SharedPreferencesからとってくる
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
			searchWord = pref.getString("searchword", "");
		}
		
		// 応答メッセージの抽出
		resMsg = resMsg.split(",")[2];
		
		// 応答メッセージ内の特殊文字を置換
		resMsg = resMsg.replaceAll("#word#", searchWord);
		
		execute(activity, searchWord);
	}
	
	
	public void execute(Activity srcActivity, String searchWord) {
		super.execute();
		
		// 検索画面への移動
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.jp/search?q="+ searchWord));
		srcActivity.startActivity(intent);

		srcActivity = null;
	}
}


/**
 *  設定画面のの遷移する用の応答
 */
class SettingResponse extends ResponseBehavior {
	SettingResponse(Activity activity, String responseMessage) {
		resMsg = responseMessage;
		
		// 応答メッセージの抽出
		resMsg = resMsg.split(",")[1];
		
		execute(activity);
	}
	
	public void execute(Activity srcActivity) {
		super.execute();
		
		// 設定画面への移動
		Intent intent = new Intent(srcActivity, MainPreferenceActivity.class);
		srcActivity.startActivity(intent);
		
		srcActivity = null;
	}
}