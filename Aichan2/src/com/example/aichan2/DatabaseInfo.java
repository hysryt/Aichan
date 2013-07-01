package com.example.aichan2;

/*
 *  データベースのメタデータ
 */
public class DatabaseInfo {
	final static public int DB_VERSION = 1;
	final static public String DB_NAME = "aidb";
	
	final static public String REGEXP_TABLE_NAME = "seikihyogen";
	final static public String REGEXP_ID_COLUMN = "id";
	final static public String REGEXP_REGEXP_COLUMN = "seikihyogen";
	final static public String REGEXP_PRIORITY_COLUMN = "pri";
	
	final static public String RESPONSE_TABLE_NAME = "henji";
	final static public String RESPONSE_ID_COLUMN = "id";
	final static public String RESPONSE_RESPONSE_COLUMN = "henji";
	
	final static public String TALK_TABLE_NAME = "kaiwa";
	final static public String TALK_ID_COLUMN = "id";
	final static public String TALK_REGEXPID_COLUMN = "seikihyogenId";
	final static public String TALK_RESPONSEID_COLUMN = "henjiId";
	final static public String TALK_PREVIOUSTALKID_COLUMN = "maeKaiwaId";
	final static public String TALK_STARTHOUR_COLUMN = "startHour";
	final static public String TALK_ENDHOUR_COLUMN = "endHour";
	final static public String TALK_CHARACTERID_COLUMN = "charId";
	
	final static public String USERSEND_TABLE_NAME = "usersend";
	final static public String USERSEND_ID_COLUMN = "id";
	final static public String USERSEND_MESSAGE_COLUMN = "message";
	
	final static public String USERRES_TABLE_NAME = "userres";
	final static public String USERRES_USERNAME_COLUMN = "userId";
	final static public String USERRES_SENDID_COLUMN = "sendId";
	final static public String USERRES_ID_COLUMN = "id";
	final static public String USERRES_MESSAGE_COLUMN = "message";
	
	static public boolean REPLICATED = false;
}
