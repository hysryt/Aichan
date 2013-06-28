package com.example.aichan2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.ProgressBar;


public class Talk {
	// データベースアクセス用
	DatabaseOpenHelper helper;
	DatabaseAccess dbAccess;
	
	List<UserSendMessage> userSendMessageList;
	List<RegExpItem> regexpList;
	
	int prevTalkId = -1;
	int charId = 1;
	
	/*
	String[] strPats = {
		"こんに?ち[はわ]"
		,"おはよう?"
		,"こんばん[はわ]"
		,"(お?腹|おなか|はら)が?((へ|減)っ|(す|空)い)た"
		,"(眠|ねむ)た?い"
		,"(疲|つか)れた"
		,"何日"
		,"(.+)を検索(して)?$"
		,"(じゅげむ|寿限無)"
		,"設定"
		,"(.+)が?((食|く)い|(食|た)べ)たい"
	};
	
	String[] rets = {
		"こんにちは、#user#"
		,"おはよう"
		,"こんばんは"
		,"ご飯食べれば"
		,"寝れば"
		,"おつかれ"
		,"今日は#month#月#date#日だよ"
		,"#search#,1,#word#を検索します"
		,"寿限無寿限無五劫の擦り切れ海砂利水魚の水行末 雲来末 風来末食う寝る処に住む処藪ら柑子の藪柑子パイポパイポ パイポのシューリンガンシューリンガンのグーリンダイグーリンダイのポンポコピーのポンポコナーの長久命の長助"
		,"#setting#,設定画面を開きます"
		,"#1#！"
	};
	*/
	
	String[] non = {
		"なに？"
		,"へ～・・・"
		,"ふ～ん"
		,"だよね"
	};
	
	//Pattern[] pats = new Pattern[strPats.length];
	
    /**
     * @param context データベース接続に使用
     */
	Talk() {
	}
	
	public void openDB(Context context) {
		helper = new DatabaseOpenHelper(context);
		dbAccess = new DatabaseAccess(helper);
		
		// 正規表現の読込
		userSendMessageList = dbAccess.loadUserSendMessage();
		regexpList = dbAccess.loadRegExps();
	}
	
	public void closeDB() {
		helper.close();
	}
	
	public void reloadUserMessage() {
		userSendMessageList = dbAccess.loadUserSendMessage();
	}
	
	
	/**
	 *  メッセージを与えて応答メッセージを返す
	 *  検索機能など、必要であれば他のアプリの起動も行う
	 */
	public String sendMessage(Activity activity, String message) {
		DebugViewer.d("----------");
		
		DebugViewer.d("prevTalkId:"+ prevTalkId);
		
		// ユーザ独自テーブルから返事を検索
		String[] userResMessages = getUserResMessage(message);
		// TODO:見つかったらそれを返す
		if(userResMessages != null) {
			Random rnda = new Random();
			int ra = rnda.nextInt(userResMessages.length);
			return userResMessages[ra];
		}
		
		
		// 与えられたメッセージから一致する正規表現を探して結果を返す
		RegExpResult regexpResult = getRegExpResult(message);
		
		// 一致する正規表現が無かった場合
		if(regexpResult == null) {
			// 表情を通常に
			CharacterManager cm = CharacterManager.getInstance();
			cm.setCharacterExpression(CharacterManager.EXPRESSION_NORMAL);
			// 分からなかった時用のメッセージを返す
			prevTalkId = -1;
			return getNonMessage();
		}
		
		// 正規表現IDから適切な会話を取得
		List<TalkItem> talklist = getFilteredTalks(regexpResult.getId());
		
		// 会話が複数ある場合は一つ選ぶ
		// 会話が無かった場合は分からなかった用のメッセージから一つ選ぶ
		int index = 0;
		if(talklist.size() > 1) {
			// 複数ある場合はランダムで一つ選ぶ
			int size = talklist.size();
			Random rnd = new Random();
			int r = rnd.nextInt(size);
			index = r;
		} else if(talklist.size() == 0) {
			// 表情を通常に
			CharacterManager cm = CharacterManager.getInstance();
			cm.setCharacterExpression(CharacterManager.EXPRESSION_NORMAL);
			// 分からなかった時用のメッセージを返す
			prevTalkId = -1;
			return getNonMessage();
		}
		
		// 会話ID、返事ID確定
		TalkItem talkitem = talklist.get(index);
		int talkId = talkitem.getTalkId();
		int resId = talkitem.getResponseId();
		DebugViewer.d("返事ID : "+ resId);
		
		// 返事IDから返事を取得
		String responseMsg = dbAccess.getResponse(resId);
		DebugViewer.d("返事 : "+ responseMsg);
		
		// 返事のResponseBehaviorを取得
		ResponseBehavior responseBehavior = ResponseBehavior.createInstance(activity, regexpResult.matcher, responseMsg);
		
		// 最終的な返事の取得
		String resMsg = responseBehavior.getResponseMessage();
		
		responseBehavior = null;
		
		prevTalkId = talkId;
		
		return resMsg;
	}
	
	/**
	 * ユーザ独自テーブルから返事を探す
	 */
	private String[] getUserResMessage(String message) {
		for(int i=0; i<userSendMessageList.size(); i++) {
			UserSendMessage item = userSendMessageList.get(i);
			if(item.match(message)) {
				int id = item.getId();
				
				return dbAccess.getUserResMessages(id);
			}
		}
		
		return null;
	}
	
	/**
	 *  メッセージから一致する正規表現を探して結果を返す
	 */
	private RegExpResult getRegExpResult(String message) {
		String receivedMsg = message;

		String msg;
		// 一人称を#jibun#に変換
		msg = replaceFirstPerson(message);
		// 二人称を#aite#に変換
		msg = replaceSecondPerson(msg);
		RegExpResult regexpResult = matchRegExp(msg);
		
		// priority0 は変換された1人称、2人称に反応しない
		if(regexpResult == null || regexpResult.getPriority() == 0) {
			if(regexpResult != null && regexpResult.getPriority() == 0) {
				DebugViewer.d("priority:0のため無視");
			}
			// 変換前のメッセージで正規表現を比較し結果を求める	
			regexpResult = matchRegExp(message);
		}
		
		return regexpResult;
	}
	
	/**
	 *  正規表現IDからフィルタを通した最適な会話を取得
	 */
	private List<TalkItem> getFilteredTalks(int regexpId) {
		// 正規表現IDから会話を求める
		List<TalkItem> talklist = dbAccess.getTalks(regexpId);

		// 適切な会話を抽出
		Calendar calendar = Calendar.getInstance();
		int curHour = calendar.get(Calendar.HOUR_OF_DAY);
		talklist = filterTalkList(talklist, curHour, prevTalkId, charId);
		
		return talklist;
	}
	
	
	/**
	 *  時間によるフィルタ
	 */
	List<TalkItem> filterTalkList(List<TalkItem> talklist, int currentHour, int prevTalkId, int charId) {
		DebugViewer.d("  beforeFilter : "+ talklist.size());
		return filterByTime(filterByPrevTalk(filterByCharacter(talklist, charId), prevTalkId), currentHour);
	}
	
	List<TalkItem> filterByTime(List<TalkItem> talklist,int currentHour) {
		List<TalkItem> result = new ArrayList<TalkItem>();
		
		for(int i=0; i<talklist.size(); i++) {
			TalkItem item = talklist.get(i);
			if(item.isMatchTime(currentHour)) {
				result.add(item);
			}
		}

		DebugViewer.d("    timefilter : "+ result.size());
		
		return result;
	}
	
	
	/**
	 *  前会話IDによるフィルタ
	 */
	List<TalkItem> filterByPrevTalk(List<TalkItem> talklist,int prevTalkId) {
		List<TalkItem> result = new ArrayList<TalkItem>();
		boolean existPrevTalk = false;
		
		for(int i=0; i<talklist.size(); i++) {
			TalkItem item = talklist.get(i);
			if(item.isMatchPrevTalk(prevTalkId)) {
				if(result.size() == 0) {	// 最初のやつの前会話IDがある場合はNULLのものは取得しない
					if(item.getPrevTalkId() == -1) {
						existPrevTalk = false;
					} else {
						existPrevTalk = true;
					}
					result.add(item);
				} else {
					if((item.getPrevTalkId() == -1 && !existPrevTalk)
							|| (item.getPrevTalkId() != -1 && existPrevTalk)) {
						result.add(item);
					}
				}
			}
		}

		DebugViewer.d("    prevTalkfilter : "+ result.size());
		
		return result;
	}
	
	/**
	 *  キャラクタIDによるフィルタ
	 */
	List<TalkItem> filterByCharacter(List<TalkItem> talklist,int charId) {
		List<TalkItem> result = new ArrayList<TalkItem>();
		boolean existCharacterId = false;
		
		for(int i=0; i<talklist.size(); i++) {
			TalkItem item = talklist.get(i);
			if(item.isMatchCharacter(charId)) {
				if(result.size() == 0) {
					if(item.getCharacterId() == 0) {
						existCharacterId = false;
					} else {
						existCharacterId = true;
					}
					result.add(item);
				} else {
					if(item.getCharacterId() == 0 && !existCharacterId
							|| item.getCharacterId() != 0 && existCharacterId) {
						result.add(item);
					}
				}
			}
		}

		
		DebugViewer.d("    characterfilter : "+ result.size());
		
		return result;
	}
	
	
	private String getNonMessage() {
        Random rnd = new Random();
        int r = rnd.nextInt(non.length);
		return non[r];
	}
	
	
	/**
	 *  全ての正規表現と比較して一番最初に一致した結果を返す
	 */
	private RegExpResult matchRegExp(String text) {
		DebugViewer.d(text);
		
		for(int i=0; i<regexpList.size(); i++) {
			RegExpItem item = regexpList.get(i);
			RegExpResult result = item.match(text);
			if(result != null) {
				
				DebugViewer.d("正規表現ID : "+ result.getId());
				DebugViewer.d("正規表現 : "+ result.item.regexp.toString());
				DebugViewer.d("priority : "+ result.getPriority());
				
				return result;
			}
		}
		
		DebugViewer.d("該当なし");
		return null;
	}
	
	
	/**
	 *  一人称を #jibun# に変換
	 */
	private String replaceFirstPerson(String msg) {
		String userName = App.getInstance().getUserName();
		String result = msg.replaceAll(userName +"|俺|おれ|オレ|僕|ぼく|ボク|私|わたし|ワタシ", "#jibun#");
		return result;
	}
	
	/**
	 *  二人称を #aite# に変換
	 */
	private String replaceSecondPerson(String msg) {
		String result = msg.replaceAll(MainActivity.AI_NAME +"|あなた|アナタ|君|きみ|キミ|お前|おまえ|オマエ|貴様|きさま|キサマ|あんた", "#aite#");
		return result;
	}
}


class AAA extends ProgressBar {

	public AAA(Context context) {
		super(context);
		// TODO 自動生成されたコンストラクター・スタブ
	}
}
