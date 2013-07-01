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
		// ���K�\���e�[�u��
		db.execSQL(
			"CREATE TABLE "+ DatabaseInfo.REGEXP_TABLE_NAME +"(" +
			"	"+ DatabaseInfo.REGEXP_ID_COLUMN +" INT NOT NULL PRIMARY KEY" +
			"	,"+ DatabaseInfo.REGEXP_REGEXP_COLUMN +" text"+
			"	,"+ DatabaseInfo.REGEXP_PRIORITY_COLUMN +" INT DEFAULT 5"+
			");"
		);
		
		// �Ԏ��e�[�u��
		db.execSQL(
			"CREATE TABLE "+ DatabaseInfo.RESPONSE_TABLE_NAME +"(" +
			"	"+ DatabaseInfo.RESPONSE_ID_COLUMN +" int NOT NULL PRIMARY KEY" +
			"	,"+ DatabaseInfo.RESPONSE_RESPONSE_COLUMN +" TEXT"+
			");"
		);
		
		// ��b�e�[�u��
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
		
		// ���[�U���M���b�Z�[�W�e�[�u��
		db.execSQL(
			"CREATE TABLE "+ DatabaseInfo.USERSEND_TABLE_NAME +"("+
			"	"+ DatabaseInfo.USERSEND_ID_COLUMN +" INT NOT NULL PRIMARY KEY"+
			"	,"+ DatabaseInfo.USERSEND_MESSAGE_COLUMN +" TEXT"+
			");"
		);
		
		// ���[�U�ԐM���b�Z�[�W�e�[�u��
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
		// �T�[�o��̃f�[�^�x�[�X�Ɠ���
		// �o���Ȃ������ꍇ�͊�{�f�[�^��}��
		Replicate r = new Replicate();
//		if(!r.replicate(db)) {
		if(true) {
			
			// ��{�f�[�^�̃g�����U�N�V�����J�n
			db.beginTransaction();
			try {
				insertBasicData(db);
				db.setTransactionSuccessful();
			} finally {
				// ��{�f�[�^�̃g�����U�N�V�����I��
				db.endTransaction();
			}
		}
		*/
	}
	
	private void insertBasicData(SQLiteDatabase db) {
		
		// ���K�\��
		insertRegexp(db, 1, "�����?��(��|��)");
		insertRegexp(db, 2, "���͂�");
		insertRegexp(db, 3, "����΂�(��|��)");
		insertRegexp(db, 4, "(��?��|���Ȃ�|�͂�)��?((��|��)��|(��|��)��)��");
		insertRegexp(db, 5, "^(#aite#��)?(���O|�Ȃ܂�)(��(��|�Ȃ�|�Ȃ�|(\\?|�H)?$)|��?(��|����)��)");
		insertRegexp(db, 6, "^(#aite#��?)?(�N|����)(\\?|�H)?$");
		insertRegexp(db, 7, "#jibun#�͒j");
		insertRegexp(db, 8, "#jibun#�͏�");
		insertRegexp(db, 9, "#jibun#�̎��(�Ȃ�|��|�Ȃ�)");

		insertRegexp(db, 10, "#jibun#�̎��.+�ł�");
		insertRegexp(db, 11, "#aite#�̎��");
		insertRegexp(db, 12, "(����|�R��)", 1);
		insertRegexp(db, 13, "(��������|�^�_�C�})");
		insertRegexp(db, 14, "��b.*(��|����|����)(����|����)");
		insertRegexp(db, 15, "#aite#��(�D��|����|�X�L)");
		insertRegexp(db, 16, "#aite#��(����|���炢|�L���C)");
		insertRegexp(db, 17, "(�撣|�����|�K���o)(��|��)");
		insertRegexp(db, 18, "(�撣|�����|�K���o)(��|��)");
		insertRegexp(db, 19, "(�撣|�����|�K���o)(��|��)��?");

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

		insertRegexp(db, 40, "(�H��|�䂤�ꂢ|���E���C)");
		insertRegexp(db, 41, "�ϐg");
		insertRegexp(db, 42, "�ԐM");
		insertRegexp(db, 43, "����");
		insertRegexp(db, 44, "(�X�g���X|���Ƃꂷ)");
		insertRegexp(db, 45, "(����|���傤����)");
		insertRegexp(db, 46, "(�Z��|���񂵂�)");
		insertRegexp(db, 47, "(�K��|�����킹)");
		insertRegexp(db, 48, "(��|����)");
		
		
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
		
		insertRes(db, 11, "�����Ȃ�");
		insertRes(db, 12, "�m�邩�o�J");
		insertRes(db, 13, "�X�|�[�c�A�����...#user#�Ƙb�������ȁI");
		insertRes(db, 14, "�R���R���A���ˁH");
		insertRes(db, 15, "��������Ȃ���");
		insertRes(db, 16, "#user#��������");
		insertRes(db, 17, "���߂�Ȃ����E�E�E");
		insertRes(db, 18, "�ł��ˁA�����͂��Ȃ����A�����l�������āA�{���̗������鎞������̂�");
		insertRes(db, 19, "����Ȃ��ƌ���Ȃ��ł��������E�E�E");

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

		insertRes(db, 50, "���[���H");
		insertRes(db, 51, "�����Ƃ���A����΂��Ă�������");
		insertRes(db, 52, "���U���邵���Ȃ��ł��ˁI");
		insertRes(db, 53, "������Ől���������");
		insertRes(db, 54, "����������Ɋ���ĂȂ����Ƃ��ȁE�E�E");
		insertRes(db, 55, "#user#�̍K�������̍K��");
		insertRes(db, 56, "#user#������΍K�����ȁE�E�E");
		insertRes(db, 57, "�������[�[�[�I");
		
		
		// ��b
		insertTalk(db, 1, 1, 4, 10);		// ����ɂ���
		insertTalk(db, 1, 2, 11, 15);
		insertTalk(db, 1, 3, 16, 3);
		
		insertTalk(db, 2, 1, 4, 10);		// ���͂悤
		insertTalk(db, 2, 2, 11, 15);
		insertTalk(db, 2, 3, 16, 3);
		
		insertTalk(db, 3, 1, 4, 10);		// ����΂��
		insertTalk(db, 3, 2, 11, 15);
		insertTalk(db, 3, 3, 16, 3);

		insertTalk(db, 4, 4, 6, 10);		// �͂�ւ���
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
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(1,\"�����?��[�͂�]\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(2,\"���͂悤?\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(3,\"����΂�[�͂�]\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(4,\"(��?��|���Ȃ�|�͂�)��?((��|��)��|(��|��)��)��\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(5,\"(��|�˂�)��?��\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(6,\"(.+)������(����)?$\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(7,\"(.+)��?((�H|��)��|(�H|��)��)����\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(8,\"�ݒ�\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(9,\"^(#aite#��)?(���O|�Ȃ܂�)(��?(����|������)|��(\\?|�H)?$)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(10,\"(���t�^|����������)��?(����|������)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(11,\"#jibun#�͒j\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(12,\"#jibun#�͏�\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(13,\"#jibun#�̎��(�Ȃ�|��|�Ȃ�)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(14,\"#jibun#�̎��.+�ł�\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(15,\"#aite#�̎��\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(16,\"(�D��|����|�X�L)��(�H��|����)(��|����)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(17,\"(�D��|����|�X�L)��(���i|�^�C�v|�l)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(18,\"(�D��|����|�X�L)��(����|�ǂ��Ԃ�)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(19,\"(�D��|����|�X�L)��(��|�͂�|�n�i)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(20,\"(�D��|����|�X�L)��(�F|����)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(21,\"(�D��|����|�X�L)��(�G��|������)\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.REGEXP_TABLE_NAME +" (id,seikihyogen) VALUES(22,\"(�H��|����)(��|����)��(�D��|����|�X�L)\")");
		*/
		

		/*
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(1,\"�͂�����ɂ��́A#user#\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(2,\"���͂悤�A#user#\",6,10)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(8,\"����������\",11,15)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(11,\"�����[������\",16,18)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(9,\"����΂�͂ł���\",19,2)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji,startHour,endHour) VALUES(10,\"���N������\",3,5)");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(3,\"����΂��\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(4,\"���ѐH�ׂ��\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(5,\"�Q���\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(6,\"#search#,1,#word#�������c\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(7,\"#1#�I\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(12,\"#setting#,�ݒ��ʂ��J���܂�\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(13,\"���̖��O��#ai#\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(14,\"�@�B�Ȃ�Ō��t�Ȃ�ĂȂ��ł�\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(15,\"�����Ȃ�\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(16,\"�m�邩�o�J\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(17,\"����ƁE�E�E#user#�Ƙb�����Ƃ��ȁI\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(18,\"�d�C����\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(19,\"#user#����\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(20,\"�A���p�J����\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(21,\"�`���[���b�v�I\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(22,\"���D���ł���̐F�ł�\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(23,\"���A�t���D���ł��|�J�|�J���邩��I\")");
		db.execSQL("INSERT INTO "+ DatabaseInfo.RESPONSE_TABLE_NAME +" (id,henji) VALUES(24,\"��ꂽ�l������Ă����邱�Ƃ���\")");
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
