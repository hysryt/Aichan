package com.example.aichan2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class Replicate {
	/**
	 *  ネット上のデータベースとローカルのデータベースを同期
	 *  @param db ローカルDB
	 */
	public boolean replicate(SQLiteDatabase db, ProgressAndMessageAsyncTask task, Task taskManager) {
		// ネット上のデータベースとローカルのデータベースを同期
		
		task.forwardProgress("データベースに接続中...(1/4)");
		
		RemoteDatabaseAccess remoteDBAccess = new RemoteDatabaseAccess();
		
		// 接続できるか確認
		try {
			remoteDBAccess.testConnect();
		} catch (IOException e1) {
			task.error();
			if(!taskManager.isStopped()) {
				task.forwardProgress("データベースに接続できませんでした");
			}
			return false;
		}
		
		task.forwardProgress(5);
		
		remoteDBAccess.openDB();

		////DebugViewer.d("リモートデータベースに接続．．．");
		
		if(!remoteDBAccess.isConnected()) {
			//DebugViewer.d("失敗");
			Log.e("Replicate", "データベースに接続できませんでした");
			task.forwardProgress("データベースに接続できませんでした");
			return false;
		}
		
		int prog = 0;
		
		int num;
		int offset;
		boolean flg;
		
		try {
			int regexpRowCount = remoteDBAccess.getRowCount(DatabaseInfo.REGEXP_TABLE_NAME);
			int resRowCount = remoteDBAccess.getRowCount(DatabaseInfo.RESPONSE_TABLE_NAME);
			int talkRowCount = remoteDBAccess.getRowCount(DatabaseInfo.TALK_TABLE_NAME);
			int allRowCount = regexpRowCount + resRowCount + talkRowCount;
			
			prog += 5;
			task.forwardProgress(prog, "正規表現テーブル取得中...(2/4)");
			
			int loadedRowCount = 0;

			
			/*
			 *  正規表現テーブル
			 */
			//DebugViewer.d("    正規表現テーブル取得．．．");
			num = 20;
			offset = 0;
			flg = true;
			
			db.beginTransaction();
			try {
				while(flg) {
					// 中断されたかどうかの確認
					if(taskManager.isStopped()) {
						return false;
					}
					
					// 正規表現テーブル取得
					List<RegExpTableRow> regexpList = remoteDBAccess.getRegExpTablePer(num, offset);
					if(regexpList == null) {
						return false;
					}
					// 正規表現テーブル挿入
					insertRegExpTable(regexpList, db);
					
					prog = (int)((loadedRowCount/(double)allRowCount) * 95) + 5;
					task.forwardProgress(prog);
					
					loadedRowCount += regexpList.size();
					
					if(regexpList.size() < num) flg = false;
					offset += num;
				}
			
				task.forwardProgress("返事テーブル取得中...(3/4)");
				
				
				/*
				 *  返事テーブル
				 */
				//DebugViewer.d("    返事テーブル取得．．．");
				num = 20;
				offset = 0;
				flg = true;
			
				while(flg) {
					// 中断されたかどうかの確認
					if(taskManager.isStopped()) {
						return false;
					}
					
					List<ResponseTableRow> resList = remoteDBAccess.getResponseTablePer(num, offset);
					if(resList == null) {
						return false;
					}
					
					insertResponseTable(resList, db);
					loadedRowCount += resList.size();
	
					prog = (int)((loadedRowCount/(double)allRowCount) * 95) + 5;
					task.forwardProgress(prog);
					
					if(resList.size() < num) flg = false;
					offset += num;
				}
					
				task.forwardProgress("会話テーブル取得中...(4/4)");
				
				
				/*
				 *  会話テーブル
				 */
				//DebugViewer.d("    会話テーブル取得．．．");
				num = 20;
				offset = 0;
				flg = true;
				
				while(flg) {
					// 中断されたかどうかの確認
					if(taskManager.isStopped()) {
						return false;
					}
					
					List<TalkTableRow> talkList = remoteDBAccess.getTalkTablePer(num ,offset);
					if(talkList == null) {
						return false;
					}
					
					insertTalkTable(talkList, db);
					loadedRowCount += talkList.size();
	
					prog = (int)((loadedRowCount/(double)allRowCount) * 95) + 5;
					task.forwardProgress(prog);
					
					if(talkList.size() < num) flg = false;
					offset += num;
				}
			db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			remoteDBAccess.closeDB();
		}
		
		return true;
	}
	
	
	public boolean replicate(SQLiteDatabase db) {
		return replicate(db, null, null);
	}
	
	
	/**
	 *  正規表現テーブル挿入
	 */
	private void insertRegExpTable(List<RegExpTableRow> list, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		
		int cnt = 0;
		for(int i=0; i<list.size(); i++) {
			RegExpTableRow row = list.get(i);
			
			values.clear();
			values.put(DatabaseInfo.REGEXP_ID_COLUMN, row.id);
			values.put(DatabaseInfo.REGEXP_REGEXP_COLUMN, row.regexp);
			values.put(DatabaseInfo.REGEXP_PRIORITY_COLUMN, row.priority);
			
			long id = db.insert(DatabaseInfo.REGEXP_TABLE_NAME, null, values);
			if(id != -1) cnt++;
		}
		
		//DebugViewer.d(DatabaseInfo.REGEXP_TABLE_NAME +"テーブルに"+ cnt +"件追加");
	}
	
	
	/**
	 *  返事テーブル挿入
	 */
	private void insertResponseTable(List<ResponseTableRow> list, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		
		int cnt = 0;
		for(int i=0; i<list.size(); i++) {
			ResponseTableRow row = list.get(i);
			
			values.clear();
			values.put(DatabaseInfo.RESPONSE_ID_COLUMN, row.id);
			values.put(DatabaseInfo.RESPONSE_RESPONSE_COLUMN, row.response);
			
			long id = db.insert(DatabaseInfo.RESPONSE_TABLE_NAME, null, values);
			if(id != -1) cnt++;
		}

		//DebugViewer.d(DatabaseInfo.RESPONSE_TABLE_NAME +"テーブルに"+ cnt +"件追加");
	}
	
	
	/**
	 *  会話テーブル挿入
	 */
	private void insertTalkTable(List<TalkTableRow> list, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		
		int cnt = 0;
		for(int i=0; i<list.size(); i++) {
			TalkTableRow row = list.get(i);
			
			values.clear();
			values.put(DatabaseInfo.TALK_ID_COLUMN, row.id);
			values.put(DatabaseInfo.TALK_REGEXPID_COLUMN, row.regexpId);
			values.put(DatabaseInfo.TALK_RESPONSEID_COLUMN, row.responseId);
			values.put(DatabaseInfo.TALK_PREVIOUSTALKID_COLUMN, row.prevTalkId);
			values.put(DatabaseInfo.TALK_STARTHOUR_COLUMN, row.startHour);
			values.put(DatabaseInfo.TALK_ENDHOUR_COLUMN, row.endHour);
			values.put(DatabaseInfo.TALK_CHARACTERID_COLUMN, row.charId);
			
			long id = db.insert(DatabaseInfo.TALK_TABLE_NAME, null, values);
			if(id != -1) cnt++;
		}

		//DebugViewer.d(DatabaseInfo.TALK_TABLE_NAME +"テーブルに"+ cnt +"件追加");
	}
}
