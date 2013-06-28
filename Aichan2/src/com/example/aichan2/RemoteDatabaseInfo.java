package com.example.aichan2;

public class RemoteDatabaseInfo {
	// エミュレータでやる場合
	//final static public String HOST = "172.18.92.131";
	
	// 実機(学校内)でやる場合
	final static public String HOST = "192.168.60.1";
	final static public String USER = "testuser";
	final static public String PASSWORD = "password";
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
	final static public String TALK_REGEXPID_COLUMN = "sId";
	final static public String TALK_RESPONSEID_COLUMN = "hId";
	final static public String TALK_PREVIOUSTALKID_COLUMN = "maeKaiwaId";
	final static public String TALK_STARTHOUR_COLUMN = "startHour";
	final static public String TALK_ENDHOUR_COLUMN = "endHour";
	final static public String TALK_CHARACTERID_COLUMN = "charId";
	 
}
