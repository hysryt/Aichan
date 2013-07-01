package com.example.aichan2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 *  データベースへの接続を一挙に引き受けるクラス
 */
public class DatabaseAccess {
	DatabaseOpenHelper helper;

	
	DatabaseAccess(DatabaseOpenHelper helper) {
		this.helper = helper;
	}
	
	
	/**
	 *  データベースが空かどうか
	 */
	public boolean isEmpty() {
		SQLiteDatabase db = helper.getReadableDatabase();
		
		Cursor c = db.query(DatabaseInfo.REGEXP_TABLE_NAME, new String[] {"COUNT(*) AS cnt"}, null, null, null, null, null);
		
		int count = 0;
		if(c.moveToFirst()) {
			count = c.getInt(0);
		}
		
		Log.e("DatabaseAccess#isEmpty", count +"");
		
		if(count <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	

	
	
	/**
	 *  正規表現をすべて読み込み
	 */
	public List<RegExpItem> loadRegExps() {
		// 読み込み専用のSQLiteDatabase取得
		SQLiteDatabase db = helper.getReadableDatabase();
		
		// 正規表現テーブルのデータを全部リストに入れる
		List<RegExpItem> list = new ArrayList<RegExpItem>();
		Cursor c = db.query(DatabaseInfo.REGEXP_TABLE_NAME, null, null, null, null, null, DatabaseInfo.REGEXP_PRIORITY_COLUMN +" DESC");
		boolean isEof = c.moveToFirst();
		while(isEof) {
			int id = c.getInt(c.getColumnIndex(DatabaseInfo.REGEXP_ID_COLUMN));
			String regexpStr = c.getString(c.getColumnIndex(DatabaseInfo.REGEXP_REGEXP_COLUMN));
			Pattern regexp = Pattern.compile(regexpStr);
			int priority = c.getInt(c.getColumnIndex(DatabaseInfo.REGEXP_PRIORITY_COLUMN));
			RegExpItem item = new RegExpItem(id, regexp, priority);
			list.add(item);
			
			isEof = c.moveToNext();
		}
		
		db.close();
		
		return list;
	}
	
	
	/**
	 *  正規表現IDから会話テーブルと返事テーブルを結合させて取得
	 */
	public List<TalkItem> getTalks(int regexpId) {
//		SELECT kaiwa.id, henjiId, beforeKaiwaId, startHour, endHour, charaId
//		FROM kaiwa, henji
//		WHERE seikihyogenId = 1
//		AND henjiId = henji.id
//		ORDER BY charaId desc
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		String sql = "SELECT "+ DatabaseInfo.TALK_TABLE_NAME +"."+ DatabaseInfo.TALK_ID_COLUMN +" AS "+ "talkId"+
					 "		, "+ DatabaseInfo.TALK_RESPONSEID_COLUMN +
					 "		, "+ DatabaseInfo.TALK_PREVIOUSTALKID_COLUMN +
					 "		, "+ DatabaseInfo.TALK_STARTHOUR_COLUMN +
					 "		, "+ DatabaseInfo.TALK_ENDHOUR_COLUMN +
					 "		, "+ DatabaseInfo.TALK_CHARACTERID_COLUMN +
					 "	FROM "+ DatabaseInfo.TALK_TABLE_NAME +
					 "	WHERE "+ DatabaseInfo.TALK_REGEXPID_COLUMN +"="+ regexpId +
					 "	ORDER BY "+ DatabaseInfo.TALK_CHARACTERID_COLUMN +" DESC, "+ DatabaseInfo.TALK_PREVIOUSTALKID_COLUMN +" DESC";
		
		List<TalkItem> list = new ArrayList<TalkItem>();
		
		Cursor c = db.rawQuery(sql, null);
		boolean isEof = c.moveToFirst();
		while(isEof) {
			int kaiwaId = c.getInt(c.getColumnIndex("talkId"));
			int henjiId = c.getInt(c.getColumnIndex(DatabaseInfo.TALK_RESPONSEID_COLUMN));
			int prevTalkId = -1;
			if(!c.isNull(c.getColumnIndex(DatabaseInfo.TALK_PREVIOUSTALKID_COLUMN))) {
				prevTalkId = c.getInt(c.getColumnIndex(DatabaseInfo.TALK_PREVIOUSTALKID_COLUMN));
			}
			int startHour = c.getInt(c.getColumnIndex(DatabaseInfo.TALK_STARTHOUR_COLUMN));
			int endHour = c.getInt(c.getColumnIndex(DatabaseInfo.TALK_ENDHOUR_COLUMN));
			int charaId = c.getInt(c.getColumnIndex(DatabaseInfo.TALK_CHARACTERID_COLUMN));
			
			Log.d("DatabaseAccess", kaiwaId +","+ henjiId +","+ prevTalkId +","+ startHour +","+ endHour +","+ charaId);
			
			TalkItem item = new TalkItem(kaiwaId, henjiId, prevTalkId, charaId, startHour, endHour);
			
			list.add(item);
			
			isEof = c.moveToNext();
		}
		
		db.close();
		
		return list;
	}
	
	
	/**
	 *  返事IDから返事を取得
	 */
	public String getResponse(int responseId) {
		// 読み込み専用のSQLiteDatabase取得
		SQLiteDatabase db = helper.getReadableDatabase();

		Cursor c = db.query(DatabaseInfo.RESPONSE_TABLE_NAME
							, new String[]{DatabaseInfo.RESPONSE_RESPONSE_COLUMN}
							, DatabaseInfo.RESPONSE_ID_COLUMN +"="+ responseId
							, null, null, null, null);
		String response = null;
		boolean isEof = c.moveToFirst();
		while(isEof) {
			response = c.getString(c.getColumnIndex(DatabaseInfo.RESPONSE_RESPONSE_COLUMN));
			isEof = c.moveToNext();
		}
		
		db.close();
		
		return response;
	}
	
	public void insertBasicTalkData() {
		insertBasicTalkData(null);
	}
	
	public void insertBasicTalkData(ProgressAndMessageAsyncTask task) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		int groupCnt = 18;
		double progress = 0;
		
		db.beginTransaction();
		
		try {
			task.forwardProgress("正規表現データベースを構築中...(1/3)");
			
			insertRegexp(db, 1, "こんに?ち(わ|は)");
			insertRegexp(db, 2, "おはよ");
			insertRegexp(db, 3, "こんばん(わ|は)");
			insertRegexp(db, 4, "(お?腹|おなか|はら)が?((へ|減)っ|(す|空)い)た");
			insertRegexp(db, 5, "^(#aite#の)?(名前|なまえ)(は(何|なに|なん|(\\?|？)?$)|を?(教|おし)え)");
			insertRegexp(db, 6, "^(#aite#は?)?(誰|だれ)(\\?|？)?$");
			insertRegexp(db, 7, "#jibun#は男");
			insertRegexp(db, 8, "#jibun#は女");
			insertRegexp(db, 9, "#jibun#の趣味は(なに|何|なん)");
			
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
	
			insertRegexp(db, 10, "#jibun#の趣味は.+です");
			insertRegexp(db, 11, "^(#aite#の)?趣味は");
			insertRegexp(db, 12, "(こん|コン)", 1);
			insertRegexp(db, 13, "(ただいま|タダイマ)");
			insertRegexp(db, 14, "会話.*(変|噛み|かみ)(合わ|あわ)");
			insertRegexp(db, 15, "(好き|すき|スキ)");
			insertRegexp(db, 16, "(嫌い|きらい|キライ)");
			insertRegexp(db, 17, "(頑張|がんば|ガンバ)(る|ル)");
			insertRegexp(db, 18, "(頑張|がんば|ガンバ)(れ|レ)");
			insertRegexp(db, 19, "(頑張|がんば|ガンバ)(ろ|ロ)う?");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
	
			insertRegexp(db, 20, "(番組|ばんぐみ)");
			insertRegexp(db, 21, ".+(を|も)(尊敬|そんけい)");
			insertRegexp(db, 22, "(職業|しょくぎょう)");
			insertRegexp(db, 23, "生.*変");
			insertRegexp(db, 24, "(旅行|りょこう)");
			insertRegexp(db, 25, "(口癖|口ぐせ|くちぐせ)");
			insertRegexp(db, 26, "(気分|きぶん|調子|ちょうし)が?(良い|いい|イイ)");
			insertRegexp(db, 27, "(気分|きぶん|調子|ちょうし)が?(悪い|わるい|ワルイ)");
			insertRegexp(db, 28, "(何|なに)が?(欲しい|ほしい)");
			insertRegexp(db, 29, ".+が?(欲しい|ほしい)");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRegexp(db, 30, "髪.*切");
			insertRegexp(db, 31, "髪型");
			insertRegexp(db, 32, "(寝|眠)");
			insertRegexp(db, 33, "(言います|いいます)");
			insertRegexp(db, 34, "言い");
			insertRegexp(db, 35, "#jibun#.+(特.*技|とくぎ|とくいわざ)");
			insertRegexp(db, 36, "(特.*技|とくぎ|とくいわざ)");
			insertRegexp(db, 37, ".+が(得意|とくい|トクイ)");
			insertRegexp(db, 38, ".+が(苦手|にがて|ニガテ)");
			insertRegexp(db, 39, "(お化け|おばけ|オバケ)");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRegexp(db, 40, "(幽霊|ゆうれい|ユウレイ)");
			insertRegexp(db, 41, "変身");
			insertRegexp(db, 42, "返信");
			insertRegexp(db, 43, "政治");
			insertRegexp(db, 44, "(ストレス|すとれす)");
			insertRegexp(db, 45, "(長所|ちょうしょ)");
			insertRegexp(db, 46, "(短所|たんしょ)");
			insertRegexp(db, 47, "(幸せ|しあわせ)");
			insertRegexp(db, 48, "(叫|さけ)");
			insertRegexp(db, 49, "(.+)(を|って)?検索");
			insertRegexp(db, 50, "(.+)って(何|なに)");
			insertRegexp(db, 51, "うん|はい|する|よろしく|お(ねが|願)い|頼");
			
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);

			task.forwardProgress("返事データベースを構築中...(2/3)");
			// 返事
			insertRes(db, 1, "おはよう、#user#");
			insertRes(db, 2, "こんにちは、#user#");
			insertRes(db, 3, "こんばんは、#user#");
			insertRes(db, 4, "朝ごはん食べた？");
			insertRes(db, 5, "そろそろ昼だね");
			insertRes(db, 6, "昼ごはん食べた？");
			insertRes(db, 7, "そろそろ晩ご飯");
			insertRes(db, 8, "晩ご飯食べた？");
			insertRes(db, 9, "なんか食べれば");
			insertRes(db, 10, "私は#ai#だよ、よろしくね");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 11, "そうなんだ");
			insertRes(db, 12, "知るかバカ");
			insertRes(db, 13, "スポーツ、それと...#user#と話す事かな！");
			insertRes(db, 14, "コンコン、きつね？");
			insertRes(db, 15, "おかえりなさい");
			insertRes(db, 16, "#user#おかえり");
			insertRes(db, 17, "ごめんなさい・・・");
			insertRes(db, 18, "でもね、いつかはあなたも、いい人を見つけて、本当の恋をする時が来るのよ");
			insertRes(db, 19, "そんなこと言わないでください・・・");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 20, "がんばってください");
			insertRes(db, 21, "はい、がんばります");
			insertRes(db, 22, "がんばりましょう");
			insertRes(db, 23, "観ればいいじゃない");
			insertRes(db, 24, "なにか面白い番組やってる？");
			insertRes(db, 25, "疲れた人を癒してあげる事かな");
			insertRes(db, 26, "人間になって#user#に会いたいな");
			insertRes(db, 27, "連れて行ってよ");
			insertRes(db, 28, "行きたい！");
			insertRes(db, 29, "急に言われても分かんないよ");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 30, "口癖なんてないよ・・・多分");
			insertRes(db, 31, "それは良かった♪");
			insertRes(db, 32, "どうしたの？");
			insertRes(db, 33, "私は#user#がいれば十分です");
			insertRes(db, 34, "#user#が欲しいです！");
			insertRes(db, 35, "たまには我慢も大事です");
			insertRes(db, 36, "いいね！");
			insertRes(db, 37, "今の髪型気に入ってるの");
			insertRes(db, 38, "もう寝る？");
			insertRes(db, 39, "もう、どうぞ言っちゃってください");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 40, "#user#がんばれ");
			insertRes(db, 41, "私は、お喋りが特技です");
			insertRes(db, 42, "お喋りが特技です");
			insertRes(db, 43, "いいですね");
			insertRes(db, 44, "苦手なら無理に克服する必要はないです");
			insertRes(db, 45, "おばけなんて信じてるんですか？");
			insertRes(db, 46, "お、おばけなんているわけないじゃないですか・・・");
			insertRes(db, 47, "幽霊なんて信じてるんですか");
			insertRes(db, 48, "ゆ、幽霊なんているわけないじゃないですか・・・");
			insertRes(db, 49, "衣装チェンジですか？");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 50, "メール？");
			insertRes(db, 51, "政治家さん、がんばってください");
			insertRes(db, 52, "発散するしかないですね！");
			insertRes(db, 53, "お喋りで人を癒すこと");
			insertRes(db, 54, "少しお喋りに慣れてないことかな・・・");
			insertRes(db, 55, "#user#の幸せが私の幸せ");
			insertRes(db, 56, "#user#がいれば幸せかな・・・");
			insertRes(db, 57, "すきだーーー！");
			insertRes(db, 58, "#search#,1,はいよ");
			insertRes(db, 59, "#beforesearch#,1,・・・検索する？");
			insertRes(db, 60, "#search#,-1,はいよ！");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			

			task.forwardProgress("会話データベースを構築中...(3/3)");
			// 会話
			insertTalk(db, 1, 1, 4, 10);		// こんにちは
			insertTalk(db, 1, 2, 11, 18);
			insertTalk(db, 1, 3, 19, 3);
			insertTalk(db, 2, 1, 4, 10);		// おはよう
			insertTalk(db, 2, 2, 11, 18);
			insertTalk(db, 2, 3, 19, 3);
			insertTalk(db, 3, 1, 4, 10);		// こんばんは
			insertTalk(db, 3, 2, 11, 18);
			insertTalk(db, 3, 3, 19, 3);
			insertTalk(db, 4, 4, 6, 10);		// はらへった
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertTalk(db, 4, 5, 11, 11);
			insertTalk(db, 4, 6, 12, 15);
			insertTalk(db, 4, 7, 16, 18);
			insertTalk(db, 4, 8, 19, 0);
			insertTalk(db, 4, 9, 1, 5);
			insertTalk(db, 5, 10);				// だれだ
			insertTalk(db, 6, 10);
			insertTalk(db, 7, 11);
			insertTalk(db, 7, 11);
			insertTalk(db, 7, 11);
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertTalk(db, 8, 11);
			insertTalk(db, 9, 12);
			insertTalk(db, 10, 11);
			insertTalk(db, 11, 13);
			insertTalk(db, 12, 14);
			insertTalk(db, 13, 15);
			insertTalk(db, 13, 16);
			insertTalk(db, 14, 17);
			insertTalk(db, 15, 18);
			insertTalk(db, 16, 19);
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertTalk(db, 17, 20);
			insertTalk(db, 18, 21);
			insertTalk(db, 19, 22);
			insertTalk(db, 20, 23);
			insertTalk(db, 20, 24);
			insertTalk(db, 21, 11);
			insertTalk(db, 22, 25);
			insertTalk(db, 23, 26);
			insertTalk(db, 24, 27);
			insertTalk(db, 24, 28);
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertTalk(db, 25, 29);
			insertTalk(db, 25, 30);
			insertTalk(db, 26, 31);
			insertTalk(db, 27, 32);
			insertTalk(db, 28, 33);
			insertTalk(db, 28, 34);
			insertTalk(db, 29, 35);
			insertTalk(db, 30, 36);
			insertTalk(db, 31, 37);
			insertTalk(db, 32, 38);
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertTalk(db, 33, 39);
			insertTalk(db, 34, 40);
			insertTalk(db, 35, 41);
			insertTalk(db, 36, 42);
			insertTalk(db, 37, 43);
			insertTalk(db, 38, 44);
			insertTalk(db, 39, 45);
			insertTalk(db, 39, 46);
			insertTalk(db, 40, 47);
			insertTalk(db, 40, 48);
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertTalk(db, 41, 49);
			insertTalk(db, 42, 50);
			insertTalk(db, 43, 51);
			insertTalk(db, 44, 52);
			insertTalk(db, 45, 53);
			insertTalk(db, 46, 54);
			insertTalk(db, 47, 55);
			insertTalk(db, 47, 56);
			insertTalk(db, 48, 57);
			insertTalk(db, 49, 58);
			
			int p = insertTalk(db, 50, 59);
			insertTalkWithPrev(db, 51, 60, p);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
		progress += 100/groupCnt;
		task.forwardProgress((int)progress);
		
		db.close();
	}
	
	int talkId = 1;
	
	private void resetTalkId() {
		talkId = 1;
	}
	
	private void insertRegexp(SQLiteDatabase db, int id, String regexp) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +"(id,seikihyogen) VALUES("+ id +", \""+ regexp +"\")");
	}
	
	private void insertRegexp(SQLiteDatabase db, int id, String regexp, int priority) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +"(id,seikihyogen,pri) VALUES("+ id +", \""+ regexp +"\", "+ priority +")");

	}
	
	private void insertRes(SQLiteDatabase db, int id, String res) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" VALUES("+ id +", \""+ res +"\")");
	}
	
	private int insertTalk(SQLiteDatabase db, int regexpId, int resId) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES("+ talkId +", "+ regexpId +", "+ resId +")");
		return talkId++;
	}
	
	private int insertTalk(SQLiteDatabase db, int regexpId, int resId, int startHour, int endHour) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId,startHour,endHour) VALUES("+ talkId++ +", "+ regexpId +", "+ resId +", "+ startHour +", "+ endHour +")");
		return talkId++;
	}
	
	private int insertTalk(SQLiteDatabase db, int regexpId, int resId, int charId) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId,charId) VALUES("+ talkId++ +", "+ regexpId +", "+ resId +", "+ charId +")");
		return talkId++;
	}
	
	private int insertTalk(SQLiteDatabase db, int regexpId, int resId, int startHour, int endHour, int charId) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId,startHour,endHour,charId) VALUES("+ talkId++ +", "+ regexpId +", "+ resId +", "+ startHour +", "+ endHour +", "+ charId +")");
		return talkId++;
	}
	
	private int insertTalkWithPrev(SQLiteDatabase db, int regexpId, int resId, int prevId) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId,maeKaiwaId) VALUES("+ talkId +", "+ regexpId +", "+ resId +", "+ prevId+ ")");
		return talkId++;
	}
	
	/**
	 * ユーザ独自メッセージの記録
	 */
	public boolean registUserMessage(String sendMessage, String[] resMessages) {
		if(sendMessage.equals("")) {
			return false;
		}
		
		// ユーザ名取得
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplicationContext());
		String userName = pref.getString(App.PREFKEY_USERNAME, "-");
		
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		int sendId = 1, resId = 1;
		
		// 送信メッセージのIDを取得
		Cursor c1 = db.query(DatabaseInfo.USERSEND_TABLE_NAME
				, new String[]{"max(id)"}
				, null, null, null, null, null);
		
		if(c1.getCount() != 0) {
			c1.moveToFirst();
			sendId = c1.getInt(0) + 1;
		}
		
		c1.close();
		
		// 返信メッセージのIDを取得
		Cursor c2 = db.query(DatabaseInfo.USERRES_TABLE_NAME
				, new String[]{"max(id)"}
				, null, null, null, null, null);
		
		if(c2.getCount() != 0) {
			c2.moveToFirst();
			resId = c2.getInt(0) + 1;
		}
		
		c2.close();
		
		ContentValues values = new ContentValues();
		values.put(DatabaseInfo.USERSEND_ID_COLUMN, sendId);
		values.put(DatabaseInfo.USERSEND_MESSAGE_COLUMN, sendMessage);
		db.insert(DatabaseInfo.USERSEND_TABLE_NAME, null, values);
		values.clear();
		
		boolean result = true;
		
		try {
			for(int i=0; i<resMessages.length; i++, resId++) {
				String resMessage = resMessages[i];
				
				// 空白だったら挿入しない
				if(!resMessage.equals("")) {	
					values.put(DatabaseInfo.USERRES_USERNAME_COLUMN, userName);
					values.put(DatabaseInfo.USERRES_ID_COLUMN, resId);
					values.put(DatabaseInfo.USERRES_SENDID_COLUMN, sendId);
					values.put(DatabaseInfo.USERRES_MESSAGE_COLUMN, resMessage);
					db.insert(DatabaseInfo.USERRES_TABLE_NAME, null, values);
					values.clear();
				} else {
					resId--;
				}
			}
		} catch (Exception e) {
			result = false;
		}
		
		Log.d("registUserMessage", "sendId: "+ sendId);
		
		db.close();
		
		return result;
	}
	
	
	/**
	 * ユーザ独自送信メッセージの読みこみ
	 */
	public List<UserSendMessage> loadUserSendMessage() {
		SQLiteDatabase db = helper.getReadableDatabase();
		
		List<UserSendMessage> list = new ArrayList<UserSendMessage>();
		
		Cursor c = db.query(DatabaseInfo.USERSEND_TABLE_NAME
				, new String[]{DatabaseInfo.USERSEND_ID_COLUMN, DatabaseInfo.USERSEND_MESSAGE_COLUMN}
				, null, null, null, null, null);
		
		boolean isEof = c.moveToFirst();
		while(isEof) {
			int id = c.getInt(c.getColumnIndex(DatabaseInfo.USERSEND_ID_COLUMN));
			String message = c.getString(c.getColumnIndex(DatabaseInfo.USERSEND_MESSAGE_COLUMN));
			list.add(new UserSendMessage(id, message));
			
			isEof = c.moveToNext();
		}
		
		db.close();
		
		return list;
	}
	
	
	/**
	 * ユーザ独自返信メッセージの取得
	 * 存在しない場合は null を返す
	 */
	public String[] getUserResMessages(int sendId) {
		// ユーザ名取得
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplicationContext());
		String userName = pref.getString(App.PREFKEY_USERNAME, "-");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		List<String> list = new ArrayList<String>();
		
		// 送信メッセージIDが一致かつユーザ名が一致するものを取得
		Cursor c = db.query(DatabaseInfo.USERRES_TABLE_NAME
				, new String[]{DatabaseInfo.USERRES_MESSAGE_COLUMN}
				, DatabaseInfo.USERRES_SENDID_COLUMN +" = "+ sendId +" AND "+ DatabaseInfo.USERRES_USERNAME_COLUMN +" = ?"
				, new String[]{userName}, null, null, null);
		
		boolean isEof = c.moveToFirst();
		while(isEof) {
			String resMessage = c.getString(c.getColumnIndex(DatabaseInfo.USERRES_MESSAGE_COLUMN));
			list.add(resMessage);
			isEof = c.moveToNext();
		}
		
		if(list.size() == 0) {
			return null;
		} else {
			return list.toArray(new String[list.size()]);
		}
	}
}


/**
 * 一つの正規表現テーブルの行を表すクラス
 */
class RegExpItem {
	int id;
	Pattern regexp;
	int priority;
	
	RegExpItem(int id, Pattern regexp, int priority) {
		this.id = id;
		this.regexp = regexp;
		this.priority = priority;
	}
	
	public int getId() {
		return id;
	}
	
	/*
	 *  一致する場合はRegExpResultのインスタンスを返し
	 *  一致しない場合はnullを返す
	 */
	public RegExpResult match(String text) {
		// 正規表現を検証
		Matcher m = regexp.matcher(text);
		if(m.find()) {
			return new RegExpResult(text, this, m, priority);
		}
		return null;
	}
	
	// debug
	public String toString() {
		return id +":"+ regexp.toString();
	}
}


/**
 *  正規表現の結果を表すクラス
 */
class RegExpResult {
	String text;
	RegExpItem item;
	Matcher matcher;
	int priority;
	
	RegExpResult(String text, RegExpItem item, Matcher matcher, int priority) {
		this.text = text;
		this.item = item;
		this.matcher = matcher;
		this.priority = priority;
	}
	
	public int getId() {
		return item.id;
	}
	
	public int getPriority() {
		return priority;
	}
}


/**
 * 会話を表すクラス
 */
class TalkItem {
	int id;
	int responseId;
	int previousTalkId;
	
	int charaId;
	int startHour;
	int endHour;
	
	TalkItem(int talkId, int responseId, int prevTalkId, int charaId, int startHour, int endHour) {
		this.id = talkId;
		this.responseId = responseId;
		this.previousTalkId = prevTalkId;
		this.charaId = charaId;
		this.startHour = startHour;
		this.endHour = endHour;
	}
	
	public int getTalkId() {
		return id;
	}
	
	public int getResponseId() {
		return responseId;
	}
	
	public int getCharacterId() {
		return charaId;
	}
	
	public int getPrevTalkId() {
		return previousTalkId;
	}
	
	public boolean isMatchTime(int currentHour) {
		if(startHour > endHour) {
			if(startHour <= currentHour || currentHour <= endHour) {
				return true;
			}
		} else{
			if(startHour <= currentHour && currentHour <= endHour) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isMatchCharacter(int charaId) {
		if(this.charaId == charaId || this.charaId == 0) {
			return true;
		}
		return false;
	}
	
	public boolean isMatchPrevTalk(int prevTalkId) {
		if(previousTalkId == prevTalkId || previousTalkId == -1) {
			return true;
		}
		return false;
	}
	
	public boolean hasPrevTalk() {
		if(previousTalkId != -1) {
			return true;
		}
		return false;
	}
}


class UserSendMessage {
	private int mId;
	private String mMessage;
	
	UserSendMessage(int id, String message) {
		mId = id;
		mMessage = message;
	}
	
	public int getId() {
		return mId;
	}
	
	public boolean match(String message) {
		return mMessage.equals(message);
	}
	
	public void dump() {
		Log.d("UserSendMessage", mId +","+ mMessage);
	}
}
