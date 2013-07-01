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
 *  �f�[�^�x�[�X�ւ̐ڑ����ꋓ�Ɉ����󂯂�N���X
 */
public class DatabaseAccess {
	DatabaseOpenHelper helper;

	
	DatabaseAccess(DatabaseOpenHelper helper) {
		this.helper = helper;
	}
	
	
	/**
	 *  �f�[�^�x�[�X���󂩂ǂ���
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
	 *  ���K�\�������ׂēǂݍ���
	 */
	public List<RegExpItem> loadRegExps() {
		// �ǂݍ��ݐ�p��SQLiteDatabase�擾
		SQLiteDatabase db = helper.getReadableDatabase();
		
		// ���K�\���e�[�u���̃f�[�^��S�����X�g�ɓ����
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
	 *  ���K�\��ID�����b�e�[�u���ƕԎ��e�[�u�������������Ď擾
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
	 *  �Ԏ�ID����Ԏ����擾
	 */
	public String getResponse(int responseId) {
		// �ǂݍ��ݐ�p��SQLiteDatabase�擾
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
			task.forwardProgress("���K�\���f�[�^�x�[�X���\�z��...(1/3)");
			
			insertRegexp(db, 1, "�����?��(��|��)");
			insertRegexp(db, 2, "���͂�");
			insertRegexp(db, 3, "����΂�(��|��)");
			insertRegexp(db, 4, "(��?��|���Ȃ�|�͂�)��?((��|��)��|(��|��)��)��");
			insertRegexp(db, 5, "^(#aite#��)?(���O|�Ȃ܂�)(��(��|�Ȃ�|�Ȃ�|(\\?|�H)?$)|��?(��|����)��)");
			insertRegexp(db, 6, "^(#aite#��?)?(�N|����)(\\?|�H)?$");
			insertRegexp(db, 7, "#jibun#�͒j");
			insertRegexp(db, 8, "#jibun#�͏�");
			insertRegexp(db, 9, "#jibun#�̎��(�Ȃ�|��|�Ȃ�)");
			
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
	
			insertRegexp(db, 10, "#jibun#�̎��.+�ł�");
			insertRegexp(db, 11, "^(#aite#��)?���");
			insertRegexp(db, 12, "(����|�R��)", 1);
			insertRegexp(db, 13, "(��������|�^�_�C�})");
			insertRegexp(db, 14, "��b.*(��|����|����)(����|����)");
			insertRegexp(db, 15, "(�D��|����|�X�L)");
			insertRegexp(db, 16, "(����|���炢|�L���C)");
			insertRegexp(db, 17, "(�撣|�����|�K���o)(��|��)");
			insertRegexp(db, 18, "(�撣|�����|�K���o)(��|��)");
			insertRegexp(db, 19, "(�撣|�����|�K���o)(��|��)��?");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
	
			insertRegexp(db, 20, "(�ԑg|�΂񂮂�)");
			insertRegexp(db, 21, ".+(��|��)(���h|���񂯂�)");
			insertRegexp(db, 22, "(�E��|���傭���傤)");
			insertRegexp(db, 23, "��.*��");
			insertRegexp(db, 24, "(���s|��傱��)");
			insertRegexp(db, 25, "(����|������|��������)");
			insertRegexp(db, 26, "(�C��|���Ԃ�|���q|���傤��)��?(�ǂ�|����|�C�C)");
			insertRegexp(db, 27, "(�C��|���Ԃ�|���q|���傤��)��?(����|��邢|�����C)");
			insertRegexp(db, 28, "(��|�Ȃ�)��?(�~����|�ق���)");
			insertRegexp(db, 29, ".+��?(�~����|�ق���)");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRegexp(db, 30, "��.*��");
			insertRegexp(db, 31, "���^");
			insertRegexp(db, 32, "(�Q|��)");
			insertRegexp(db, 33, "(�����܂�|�����܂�)");
			insertRegexp(db, 34, "����");
			insertRegexp(db, 35, "#jibun#.+(��.*�Z|�Ƃ���|�Ƃ����킴)");
			insertRegexp(db, 36, "(��.*�Z|�Ƃ���|�Ƃ����킴)");
			insertRegexp(db, 37, ".+��(����|�Ƃ���|�g�N�C)");
			insertRegexp(db, 38, ".+��(���|�ɂ���|�j�K�e)");
			insertRegexp(db, 39, "(������|���΂�|�I�o�P)");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRegexp(db, 40, "(�H��|�䂤�ꂢ|���E���C)");
			insertRegexp(db, 41, "�ϐg");
			insertRegexp(db, 42, "�ԐM");
			insertRegexp(db, 43, "����");
			insertRegexp(db, 44, "(�X�g���X|���Ƃꂷ)");
			insertRegexp(db, 45, "(����|���傤����)");
			insertRegexp(db, 46, "(�Z��|���񂵂�)");
			insertRegexp(db, 47, "(�K��|�����킹)");
			insertRegexp(db, 48, "(��|����)");
			insertRegexp(db, 49, "(.+)(��|����)?����");
			insertRegexp(db, 50, "(.+)����(��|�Ȃ�)");
			insertRegexp(db, 51, "����|�͂�|����|��낵��|��(�˂�|��)��|��");
			
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);

			task.forwardProgress("�Ԏ��f�[�^�x�[�X���\�z��...(2/3)");
			// �Ԏ�
			insertRes(db, 1, "���͂悤�A#user#");
			insertRes(db, 2, "����ɂ��́A#user#");
			insertRes(db, 3, "����΂�́A#user#");
			insertRes(db, 4, "�����͂�H�ׂ��H");
			insertRes(db, 5, "���낻�뒋����");
			insertRes(db, 6, "�����͂�H�ׂ��H");
			insertRes(db, 7, "���낻��ӂ���");
			insertRes(db, 8, "�ӂ��ѐH�ׂ��H");
			insertRes(db, 9, "�Ȃ񂩐H�ׂ��");
			insertRes(db, 10, "����#ai#����A��낵����");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 11, "�����Ȃ�");
			insertRes(db, 12, "�m�邩�o�J");
			insertRes(db, 13, "�X�|�[�c�A�����...#user#�Ƙb�������ȁI");
			insertRes(db, 14, "�R���R���A���ˁH");
			insertRes(db, 15, "��������Ȃ���");
			insertRes(db, 16, "#user#��������");
			insertRes(db, 17, "���߂�Ȃ����E�E�E");
			insertRes(db, 18, "�ł��ˁA�����͂��Ȃ����A�����l�������āA�{���̗������鎞������̂�");
			insertRes(db, 19, "����Ȃ��ƌ���Ȃ��ł��������E�E�E");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 20, "����΂��Ă�������");
			insertRes(db, 21, "�͂��A����΂�܂�");
			insertRes(db, 22, "����΂�܂��傤");
			insertRes(db, 23, "�ς�΂�������Ȃ�");
			insertRes(db, 24, "�Ȃɂ��ʔ����ԑg����Ă�H");
			insertRes(db, 25, "��ꂽ�l������Ă����鎖����");
			insertRes(db, 26, "�l�ԂɂȂ���#user#�ɉ������");
			insertRes(db, 27, "�A��čs���Ă�");
			insertRes(db, 28, "�s�������I");
			insertRes(db, 29, "�}�Ɍ����Ă�������Ȃ���");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 30, "���ȂȂ�ĂȂ���E�E�E����");
			insertRes(db, 31, "����͗ǂ�������");
			insertRes(db, 32, "�ǂ������́H");
			insertRes(db, 33, "����#user#������Ώ\���ł�");
			insertRes(db, 34, "#user#���~�����ł��I");
			insertRes(db, 35, "���܂ɂ͉䖝���厖�ł�");
			insertRes(db, 36, "�����ˁI");
			insertRes(db, 37, "���̔��^�C�ɓ����Ă��");
			insertRes(db, 38, "�����Q��H");
			insertRes(db, 39, "�����A�ǂ�������������Ă�������");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 40, "#user#����΂�");
			insertRes(db, 41, "���́A�����肪���Z�ł�");
			insertRes(db, 42, "�����肪���Z�ł�");
			insertRes(db, 43, "�����ł���");
			insertRes(db, 44, "���Ȃ疳���ɍ�������K�v�͂Ȃ��ł�");
			insertRes(db, 45, "���΂��Ȃ�ĐM���Ă��ł����H");
			insertRes(db, 46, "���A���΂��Ȃ�Ă���킯�Ȃ�����Ȃ��ł����E�E�E");
			insertRes(db, 47, "�H��Ȃ�ĐM���Ă��ł���");
			insertRes(db, 48, "��A�H��Ȃ�Ă���킯�Ȃ�����Ȃ��ł����E�E�E");
			insertRes(db, 49, "�ߑ��`�F���W�ł����H");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertRes(db, 50, "���[���H");
			insertRes(db, 51, "�����Ƃ���A����΂��Ă�������");
			insertRes(db, 52, "���U���邵���Ȃ��ł��ˁI");
			insertRes(db, 53, "������Ől���������");
			insertRes(db, 54, "����������Ɋ���ĂȂ����Ƃ��ȁE�E�E");
			insertRes(db, 55, "#user#�̍K�������̍K��");
			insertRes(db, 56, "#user#������΍K�����ȁE�E�E");
			insertRes(db, 57, "�������[�[�[�I");
			insertRes(db, 58, "#search#,1,�͂���");
			insertRes(db, 59, "#beforesearch#,1,�E�E�E��������H");
			insertRes(db, 60, "#search#,-1,�͂���I");
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			

			task.forwardProgress("��b�f�[�^�x�[�X���\�z��...(3/3)");
			// ��b
			insertTalk(db, 1, 1, 4, 10);		// ����ɂ���
			insertTalk(db, 1, 2, 11, 18);
			insertTalk(db, 1, 3, 19, 3);
			insertTalk(db, 2, 1, 4, 10);		// ���͂悤
			insertTalk(db, 2, 2, 11, 18);
			insertTalk(db, 2, 3, 19, 3);
			insertTalk(db, 3, 1, 4, 10);		// ����΂��
			insertTalk(db, 3, 2, 11, 18);
			insertTalk(db, 3, 3, 19, 3);
			insertTalk(db, 4, 4, 6, 10);		// �͂�ւ���
	
			progress += 100/groupCnt;
			task.forwardProgress((int)progress);
			
			insertTalk(db, 4, 5, 11, 11);
			insertTalk(db, 4, 6, 12, 15);
			insertTalk(db, 4, 7, 16, 18);
			insertTalk(db, 4, 8, 19, 0);
			insertTalk(db, 4, 9, 1, 5);
			insertTalk(db, 5, 10);				// ���ꂾ
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
	 * ���[�U�Ǝ����b�Z�[�W�̋L�^
	 */
	public boolean registUserMessage(String sendMessage, String[] resMessages) {
		if(sendMessage.equals("")) {
			return false;
		}
		
		// ���[�U���擾
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplicationContext());
		String userName = pref.getString(App.PREFKEY_USERNAME, "-");
		
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		int sendId = 1, resId = 1;
		
		// ���M���b�Z�[�W��ID���擾
		Cursor c1 = db.query(DatabaseInfo.USERSEND_TABLE_NAME
				, new String[]{"max(id)"}
				, null, null, null, null, null);
		
		if(c1.getCount() != 0) {
			c1.moveToFirst();
			sendId = c1.getInt(0) + 1;
		}
		
		c1.close();
		
		// �ԐM���b�Z�[�W��ID���擾
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
				
				// �󔒂�������}�����Ȃ�
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
	 * ���[�U�Ǝ����M���b�Z�[�W�̓ǂ݂���
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
	 * ���[�U�Ǝ��ԐM���b�Z�[�W�̎擾
	 * ���݂��Ȃ��ꍇ�� null ��Ԃ�
	 */
	public String[] getUserResMessages(int sendId) {
		// ���[�U���擾
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplicationContext());
		String userName = pref.getString(App.PREFKEY_USERNAME, "-");
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		List<String> list = new ArrayList<String>();
		
		// ���M���b�Z�[�WID����v�����[�U������v������̂��擾
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
 * ��̐��K�\���e�[�u���̍s��\���N���X
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
	 *  ��v����ꍇ��RegExpResult�̃C���X�^���X��Ԃ�
	 *  ��v���Ȃ��ꍇ��null��Ԃ�
	 */
	public RegExpResult match(String text) {
		// ���K�\��������
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
 *  ���K�\���̌��ʂ�\���N���X
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
 * ��b��\���N���X
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
