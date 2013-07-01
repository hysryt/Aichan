package com.example.aichan2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public DatabaseOpenHelper(Context context) {
		super(context, DatabaseInfo.DB_NAME, null, DatabaseInfo.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 正規表現テーブル
		db.execSQL(
			"CREATE TABLE "+ DatabaseInfo.REGEXP_TABLE_NAME +"(" +
			"	"+ DatabaseInfo.REGEXP_ID_COLUMN +" INT NOT NULL PRIMARY KEY" +
			"	,"+ DatabaseInfo.REGEXP_REGEXP_COLUMN +" text"+
			"	,"+ DatabaseInfo.REGEXP_PRIORITY_COLUMN +" INT DEFAULT 5"+
			");"
		);
		
		// 返事テーブル
		db.execSQL(
			"CREATE TABLE "+ DatabaseInfo.RESPONSE_TABLE_NAME +"(" +
			"	"+ DatabaseInfo.RESPONSE_ID_COLUMN +" int NOT NULL PRIMARY KEY" +
			"	,"+ DatabaseInfo.RESPONSE_RESPONSE_COLUMN +" TEXT"+
			");"
		);
		
		// 会話テーブル
		db.execSQL(
			"CREATE TABLE "+ DatabaseInfo.TALK_TABLE_NAME +"(" +
			"	"+ DatabaseInfo.TALK_ID_COLUMN +" INT NOT NULL PRIMARY KEY" +
			"	,"+ DatabaseInfo.TALK_REGEXPID_COLUMN +" INT"+
			"	,"+ DatabaseInfo.TALK_RESPONSEID_COLUMN +" INT"+
			"	,"+ DatabaseInfo.TALK_PREVIOUSTALKID_COLUMN +" INT DEFAULT NULL"+
			"	,"+ DatabaseInfo.TALK_CHARACTERID_COLUMN +" INT DEFAULT 0"+
			"	,"+ DatabaseInfo.TALK_STARTHOUR_COLUMN +" INT DEFAULT 0"+
			"	,"+ DatabaseInfo.TALK_ENDHOUR_COLUMN +" INT DEFAULT 24"+
			"	,FOREIGN KEY("+ DatabaseInfo.TALK_REGEXPID_COLUMN +") REFERENCES seikihyogen("+ DatabaseInfo.REGEXP_ID_COLUMN +")"+
			"	,FOREIGN KEY("+ DatabaseInfo.TALK_RESPONSEID_COLUMN +") REFERENCES henji("+ DatabaseInfo.RESPONSE_ID_COLUMN +")"+
			"	,FOREIGN KEY("+ DatabaseInfo.TALK_PREVIOUSTALKID_COLUMN +") REFERENCES kaiwa("+ DatabaseInfo.TALK_ID_COLUMN +")"+
			");"
		);
		
		// ユーザ送信メッセージテーブル
		db.execSQL(
			"CREATE TABLE "+ DatabaseInfo.USERSEND_TABLE_NAME +"("+
			"	"+ DatabaseInfo.USERSEND_ID_COLUMN +" INT NOT NULL PRIMARY KEY"+
			"	,"+ DatabaseInfo.USERSEND_MESSAGE_COLUMN +" TEXT"+
			");"
		);
		
		// ユーザ返信メッセージテーブル
		db.execSQL(
			"CREATE TABLE "+ DatabaseInfo.USERRES_TABLE_NAME +"("+
			"	"+ DatabaseInfo.USERRES_ID_COLUMN +" INT NOT NULL"+
			"	,"+ DatabaseInfo.USERRES_USERNAME_COLUMN +" TEXT NOT NULL"+ 
			"	,"+ DatabaseInfo.USERRES_SENDID_COLUMN +" INT NOT NULL"+
			"	,"+ DatabaseInfo.USERRES_MESSAGE_COLUMN +" TEXT"+
			");"
		);
		
		
		DatabaseInfo.REPLICATED = false;
		
		/*
		// サーバ上のデータベースと同期
		// 出来なかった場合は基本データを挿入
		Replicate r = new Replicate();
//		if(!r.replicate(db)) {
		if(true) {
			
			// 基本データのトランザクション開始
			db.beginTransaction();
			try {
				insertBasicData(db);
				db.setTransactionSuccessful();
			} finally {
				// 基本データのトランザクション終了
				db.endTransaction();
			}
		}
		*/
	}
	
	private void insertBasicData(SQLiteDatabase db) {
		
		// 正規表現
		insertRegexp(db, 1, "こんに?ち(わ|は)");
		insertRegexp(db, 2, "おはよ");
		insertRegexp(db, 3, "こんばん(わ|は)");
		insertRegexp(db, 4, "(お?腹|おなか|はら)が?((へ|減)っ|(す|空)い)た");
		insertRegexp(db, 5, "^(#aite#の)?(名前|なまえ)(は(何|なに|なん|(\\?|？)?$)|を?(教|おし)え)");
		insertRegexp(db, 6, "^(#aite#は?)?(誰|だれ)(\\?|？)?$");
		insertRegexp(db, 7, "#jibun#は男");
		insertRegexp(db, 8, "#jibun#は女");
		insertRegexp(db, 9, "#jibun#の趣味は(なに|何|なん)");

		insertRegexp(db, 10, "#jibun#の趣味は.+です");
		insertRegexp(db, 11, "#aite#の趣味は");
		insertRegexp(db, 12, "(こん|コン)", 1);
		insertRegexp(db, 13, "(ただいま|タダイマ)");
		insertRegexp(db, 14, "会話.*(変|噛み|かみ)(合わ|あわ)");
		insertRegexp(db, 15, "#aite#が(好き|すき|スキ)");
		insertRegexp(db, 16, "#aite#が(嫌い|きらい|キライ)");
		insertRegexp(db, 17, "(頑張|がんば|ガンバ)(る|ル)");
		insertRegexp(db, 18, "(頑張|がんば|ガンバ)(れ|レ)");
		insertRegexp(db, 19, "(頑張|がんば|ガンバ)(ろ|ロ)う?");

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

		insertRegexp(db, 40, "(幽霊|ゆうれい|ユウレイ)");
		insertRegexp(db, 41, "変身");
		insertRegexp(db, 42, "返信");
		insertRegexp(db, 43, "政治");
		insertRegexp(db, 44, "(ストレス|すとれす)");
		insertRegexp(db, 45, "(長所|ちょうしょ)");
		insertRegexp(db, 46, "(短所|たんしょ)");
		insertRegexp(db, 47, "(幸せ|しあわせ)");
		insertRegexp(db, 48, "(叫|さけ)");
		
		
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
		
		insertRes(db, 11, "そうなんだ");
		insertRes(db, 12, "知るかバカ");
		insertRes(db, 13, "スポーツ、それと...#user#と話す事かな！");
		insertRes(db, 14, "コンコン、きつね？");
		insertRes(db, 15, "おかえりなさい");
		insertRes(db, 16, "#user#おかえり");
		insertRes(db, 17, "ごめんなさい・・・");
		insertRes(db, 18, "でもね、いつかはあなたも、いい人を見つけて、本当の恋をする時が来るのよ");
		insertRes(db, 19, "そんなこと言わないでください・・・");

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

		insertRes(db, 50, "メール？");
		insertRes(db, 51, "政治家さん、がんばってください");
		insertRes(db, 52, "発散するしかないですね！");
		insertRes(db, 53, "お喋りで人を癒すこと");
		insertRes(db, 54, "少しお喋りに慣れてないことかな・・・");
		insertRes(db, 55, "#user#の幸せが私の幸せ");
		insertRes(db, 56, "#user#がいれば幸せかな・・・");
		insertRes(db, 57, "すきだーーー！");
		
		
		// 会話
		insertTalk(db, 1, 1, 4, 10);		// こんにちは
		insertTalk(db, 1, 2, 11, 15);
		insertTalk(db, 1, 3, 16, 3);
		
		insertTalk(db, 2, 1, 4, 10);		// おはよう
		insertTalk(db, 2, 2, 11, 15);
		insertTalk(db, 2, 3, 16, 3);
		
		insertTalk(db, 3, 1, 4, 10);		// こんばんは
		insertTalk(db, 3, 2, 11, 15);
		insertTalk(db, 3, 3, 16, 3);

		insertTalk(db, 4, 4, 6, 10);		// はらへった
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
		insertTalk(db, 41, 49);
		insertTalk(db, 42, 50);
		insertTalk(db, 43, 51);
		insertTalk(db, 44, 52);
		insertTalk(db, 45, 53);
		insertTalk(db, 46, 54);
		insertTalk(db, 47, 55);
		insertTalk(db, 47, 56);
		insertTalk(db, 48, 57);
		
		/*
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(1,\"こんに?ち[はわ]\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(2,\"おはよう?\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(3,\"こんばん[はわ]\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(4,\"(お?腹|おなか|はら)が?((へ|減)っ|(す|空)い)た\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(5,\"(眠|ねむ)た?い\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(6,\"(.+)を検索(して)?$\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(7,\"(.+)が?((食|く)い|(食|た)べ)たい\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(8,\"設定\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(9,\"^(#aite#の)?(名前|なまえ)(を?(教え|おしえ)|は(\\?|？)?$)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(10,\"(血液型|けつえきがた)を?(教え|おしえ)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(11,\"#jibun#は男\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(12,\"#jibun#は女\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(13,\"#jibun#の趣味は(なに|何|なん)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(14,\"#jibun#の趣味は.+です\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(15,\"#aite#の趣味は\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(16,\"(好き|すき|スキ)な(食べ|たべ)(物|もの)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(17,\"(好き|すき|スキ)な(性格|タイプ|人)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(18,\"(好き|すき|スキ)な(動物|どうぶつ)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(19,\"(好き|すき|スキ)な(花|はな|ハナ)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(20,\"(好き|すき|スキ)な(色|いろ)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(21,\"(好き|すき|スキ)な(季節|きせつ)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(22,\"(食べ|たべ)(物|もの)が(好き|すき|スキ)\")");
		*/
		

		/*
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(1,\"はいこんにちは、#user#\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(2,\"おはよう、#user#\",6,10)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(8,\"もう昼だよ\",11,15)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(11,\"もう夕方だよ\",16,18)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(9,\"こんばんはでしょ\",19,2)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(10,\"早起きだね\",3,5)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(3,\"こんばんは\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(4,\"ご飯食べれば\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(5,\"寝れば\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(6,\"#search#,1,#word#を検索…\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(7,\"#1#！\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(12,\"#setting#,設定画面を開きます\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(13,\"私の名前は#ai#\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(14,\"機械なんで血液なんてないです\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(15,\"そうなんだ\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(16,\"知るかバカ\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(17,\"それと・・・#user#と話すことかな！\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(18,\"電気だよ\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(19,\"#user#かな\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(20,\"アルパカかな\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(21,\"チューリップ！\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(22,\"青が好きです空の色です\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(23,\"私、春が好きですポカポカするから！\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(24,\"疲れた人を癒してあげることかな\")");
		*/
		


		/*
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(1,1,1)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(2,2,2)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(3,3,3)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(4,4,4)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(5,5,5)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(6,6,6)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(7,7,7)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(8,2,8)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(9,2,9)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(10,2,10)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(11,2,11)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(12,8,12)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(13,9,13)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(14,10,14)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(15,11,15)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(16,12,15)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(17,13,16)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(18,14,15)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(19,15,17)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(20,16,18)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(21,17,19)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(22,18,20)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(23,19,21)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(24,20,22)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(25,21,23)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES(26,22,24)");
		*/
		

		
	}
	
	

	int talkId = 1;
	
	private void insertRegexp(SQLiteDatabase db, int id, String regexp) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +"(id,seikihyogen) VALUES("+ id +", \""+ regexp +"\")");
	}
	
	private void insertRegexp(SQLiteDatabase db, int id, String regexp, int priority) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +"(id,seikihyogen,pri) VALUES("+ id +", \""+ regexp +"\", "+ priority +")");

	}
	
	private void insertRes(SQLiteDatabase db, int id, String res) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" VALUES("+ id +", \""+ res +"\")");
	}
	
	private void insertTalk(SQLiteDatabase db, int regexpId, int resId) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId) VALUES("+ talkId++ +", "+ regexpId +", "+ resId +")");
	}
	
	private void insertTalk(SQLiteDatabase db, int regexpId, int resId, int startHour, int endHour) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId,startHour,endHour) VALUES("+ talkId++ +", "+ regexpId +", "+ resId +", "+ startHour +", "+ endHour +")");
	}
	
	private void insertTalk(SQLiteDatabase db, int regexpId, int resId, int charId) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId,charId) VALUES("+ talkId++ +", "+ regexpId +", "+ resId +", "+ charId +")");
	}
	
	private void insertTalk(SQLiteDatabase db, int regexpId, int resId, int startHour, int endHour, int charId) {
		db.execSQL("INSERT INTO "+ DatabaseInfo.TALK_TABLE_NAME +"(id,seikihyogenId,henjiId,startHour,endHour,charId) VALUES("+ talkId++ +", "+ regexpId +", "+ resId +", "+ startHour +", "+ endHour +", "+ charId +")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
