package com.example.aichan2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

public class RemoteDatabaseAccess {
	private Connection conn;
	private String dbUrl = "jdbc:mysql://"+ RemoteDatabaseInfo.HOST +":3306/" + RemoteDatabaseInfo.DB_NAME +"?user=testuser&password=password";
	
	RemoteDatabaseAccess() {
		try {
			Log.d("RemoteDatabaseAccess", "jdbc 読込...");
			Class.forName("com.mysql.jdbc.Driver");
			Log.d("RemoteDatabaseAccess", "jdbc 読込完了");
		} catch (ClassNotFoundException e) {
			DebugViewer.d("ClassNotFoundException : "+ e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void testConnect() throws IOException {
		// テスト用のURLを作成
		//XamppにはApatchが入っててMySQLと一緒に起動している前提
		URL urlTest = new URL("http://"+ RemoteDatabaseInfo.HOST);
		// MySQLサーバー接続用オブジェクトの作成
		URLConnection connection =  urlTest.openConnection();
		// タイムアウト値の設定
		connection.setReadTimeout(5000);
		connection.setConnectTimeout(5000);
		// 接続開始
		// タイムアウトになったらExceptionが発生する
		connection.connect();
	}
	
	public void openDB() {
		try {
			conn = DriverManager.getConnection(dbUrl);
		} catch (SQLException e) {
			DebugViewer.d("SQLException : "+ e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		try {
			if(conn != null && !conn.isClosed()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	/**
	 *  正規表現テーブルのデータを取得
	 *  @return 取得できなかった場合はnull
	 * @throws SQLException 
	 */
	public List<RegExpTableRow> getRegExpTable() throws SQLException {
		if(!isConnected()) {
			Log.e("RemoteDatabaseAccess", "データベースと接続されていません");
			return null;
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<RegExpTableRow> list = null;

		list = new ArrayList<RegExpTableRow>();
		
		String sql = "SELECT * FROM "+ DatabaseInfo.REGEXP_TABLE_NAME;
		ps = conn.prepareStatement(sql);
		
		rs = ps.executeQuery();
		list = createRegExpList(rs);
		
		return list;
	}
	
	
	/**
	 *  @param offset どこから取得するか
	 *  @param num 取得する数
	 * @throws SQLException 
	 */
	public List<RegExpTableRow> getRegExpTablePer(int num, int offset) throws SQLException {
		if(!isConnected()) {
			Log.e("RemoteDatabaseAccess", "データベースと接続されていません");
			return null;
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<RegExpTableRow> list = null;

		list = new ArrayList<RegExpTableRow>();
		String sql = "SELECT * FROM "+ DatabaseInfo.REGEXP_TABLE_NAME +" LIMIT "+ offset +", "+ num;
		ps = conn.prepareStatement(sql);

		rs = ps.executeQuery();
		list = createRegExpList(rs);
		
		return list;
	}
	
	
	/**
	 *  @param offset どこから取得するか
	 *  @param num 取得する数
	 * @throws SQLException 
	 */
	public List<ResponseTableRow> getResponseTablePer(int num, int offset) throws SQLException {
		if(!isConnected()) {
			Log.e("RemoteDatabaseAccess", "データベースと接続されていません");
			return null;
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ResponseTableRow> list = null;

		list = new ArrayList<ResponseTableRow>();
		String sql = "SELECT * FROM "+ DatabaseInfo.RESPONSE_TABLE_NAME +" LIMIT "+ offset +", "+ num;
		ps = conn.prepareStatement(sql);

		rs = ps.executeQuery();
		list = createResponseList(rs);
		
		return list;
	}
	
	
	public List<TalkTableRow> getTalkTablePer(int num, int offset) throws SQLException {
		if(!isConnected()) {
			Log.e("RemoteDatabaseAccess", "データベースと接続されていません");
			return null;
		}
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<TalkTableRow> list = null;

		list = new ArrayList<TalkTableRow>();
		String sql = "SELECT * FROM "+ DatabaseInfo.TALK_TABLE_NAME +" LIMIT "+ offset +", "+ num;
		ps = conn.prepareStatement(sql);

		rs = ps.executeQuery();
		list = createTalkList(rs);
		
		return list;
	}
	
	
	private List<RegExpTableRow> createRegExpList(ResultSet set) throws SQLException {
		List<RegExpTableRow> list = new ArrayList<RegExpTableRow>();
		while(set.next()) {
			int regexpId = set.getInt(RemoteDatabaseInfo.REGEXP_ID_COLUMN);
			String regexp = set.getString(RemoteDatabaseInfo.REGEXP_REGEXP_COLUMN);
			int priority = set.getInt(RemoteDatabaseInfo.REGEXP_PRIORITY_COLUMN);
			
			RegExpTableRow row = new RegExpTableRow(regexpId, regexp, priority);
			list.add(row);
		}
		return list;
	}
	
	private List<ResponseTableRow> createResponseList(ResultSet set) throws SQLException {
		List<ResponseTableRow> list = new ArrayList<ResponseTableRow>();
		while(set.next()) {
			int resId = set.getInt(RemoteDatabaseInfo.RESPONSE_ID_COLUMN);
			String res = set.getString(RemoteDatabaseInfo.RESPONSE_RESPONSE_COLUMN);
			
			ResponseTableRow row = new ResponseTableRow(resId, res);
			list.add(row);
		}
		return list;
	}
	
	private List<TalkTableRow> createTalkList(ResultSet set) throws SQLException {
		List<TalkTableRow> list = new ArrayList<TalkTableRow>();
		while(set.next()) {
			int talkId = set.getInt(RemoteDatabaseInfo.TALK_ID_COLUMN);
			int regexpId = set.getInt(RemoteDatabaseInfo.TALK_REGEXPID_COLUMN);
			int resId = set.getInt(RemoteDatabaseInfo.TALK_RESPONSEID_COLUMN);
			int prevTalkId = set.getInt(RemoteDatabaseInfo.TALK_PREVIOUSTALKID_COLUMN);
			if(prevTalkId == 0) prevTalkId = -1;
			int startHour = set.getInt(RemoteDatabaseInfo.TALK_STARTHOUR_COLUMN);
			int endHour = set.getInt(RemoteDatabaseInfo.TALK_ENDHOUR_COLUMN);
			int charId = set.getInt(RemoteDatabaseInfo.TALK_CHARACTERID_COLUMN);
			
			TalkTableRow row = new TalkTableRow(talkId, regexpId, resId, prevTalkId, startHour, endHour, charId);
			
			list.add(row);
		}
		return list;
	}
	
	
	/**
	 *  返事テーブルのデータを取得
	 *  @return 取得できなかった場合はnull
	 */
	public List<ResponseTableRow> getResponseTable() {
		if(!isConnected()) {
			Log.e("RemoteDatabaseAccess", "データベースと接続されていません");
			return null;
		}
		
		PreparedStatement ps = null;
		List<ResponseTableRow> list = null;
		
		try {
			list = new ArrayList<ResponseTableRow>();
			
			String sql = "SELECT * FROM "+ RemoteDatabaseInfo.RESPONSE_TABLE_NAME;
			ps = conn.prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			
			int cnt = 0;
			while(rs.next()) {
				int resId = rs.getInt(RemoteDatabaseInfo.RESPONSE_ID_COLUMN);
				String response = rs.getString(RemoteDatabaseInfo.RESPONSE_RESPONSE_COLUMN);
				
				ResponseTableRow row = new ResponseTableRow(resId, response);
				list.add(row);
				
				cnt++;
			}
			
			DebugViewer.d("サーバ上の"+ RemoteDatabaseInfo.RESPONSE_TABLE_NAME +"テーブルから"+ cnt +"件取得");
			
		} catch (SQLException e) {
			closeDB();
			e.printStackTrace();
			
		} finally {
			try {
				if(ps != null) {
					ps.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		return list;
	}
	
	
	/**
	 *  会話テーブルのデータ取得
	 *  @return 取得できなかったらnull
	 */
	public List<TalkTableRow> getTalkTable() {
		if(!isConnected()) {
			Log.e("RemoteDatabaseAccess", "データベースと接続されていません");
			return null;
		}
		
		PreparedStatement ps = null;
		List<TalkTableRow> list = null;
		
		try {
			list = new ArrayList<TalkTableRow>();
			
			String sql = "SELECT * FROM "+ RemoteDatabaseInfo.TALK_TABLE_NAME;
			ps = conn.prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			
			int cnt=0;
			while(rs.next()) {
				
				int talkId = rs.getInt(RemoteDatabaseInfo.TALK_ID_COLUMN);
				int regexpId = rs.getInt(RemoteDatabaseInfo.TALK_REGEXPID_COLUMN);
				int resId = rs.getInt(RemoteDatabaseInfo.TALK_RESPONSEID_COLUMN);
				int prevTalkId = rs.getInt(RemoteDatabaseInfo.TALK_PREVIOUSTALKID_COLUMN);
				if(prevTalkId == 0) prevTalkId = -1;
				int startHour = rs.getInt(RemoteDatabaseInfo.TALK_STARTHOUR_COLUMN);
				int endHour = rs.getInt(RemoteDatabaseInfo.TALK_ENDHOUR_COLUMN);
				int charId = rs.getInt(RemoteDatabaseInfo.TALK_CHARACTERID_COLUMN);
				
				TalkTableRow row = new TalkTableRow(talkId, regexpId, resId, prevTalkId, startHour, endHour, charId);
				
				list.add(row);
				
				cnt++;
			}
			
			DebugViewer.d("サーバ上の"+ RemoteDatabaseInfo.TALK_TABLE_NAME +"テーブルから"+ cnt +"件取得");
			
		} catch (SQLException e) {			
			closeDB();
			e.printStackTrace();
			
		} finally {
			try {
				if(ps != null) {
					ps.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		return list;
	}
	
	
	
	public int getRowCount(String tableName) throws SQLException {
		if(!isConnected()) {
			Log.e("RemoteDatabaseAccess", "データベースと接続されていません");
		}
		
		String sql = "SELECT COUNT(*) AS count FROM "+ tableName;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		int count = -1;
		if(rs.next()) {
			count = rs.getInt("count");
		}
		
		return count;
	}
	
	
	public void closeDB() {
		try {
			if(conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}


class RegExpTableRow {
	int id;
	String regexp;
	int priority;
	
	RegExpTableRow(int id, String regexp, int priority) {
		this.id = id;
		this.regexp = regexp;
		this.priority = priority;
	}
}


class ResponseTableRow {
	int id;
	String response;
	
	ResponseTableRow(int id, String response) {
		this.id = id;
		this.response = response;
	}
}


class TalkTableRow {
	int id;
	int regexpId;
	int responseId;
	int prevTalkId;
	int startHour;
	int endHour;
	int charId;
	
	TalkTableRow(int id, int regexpId, int responseId, int prevTalkId, int startHour, int endHour, int charId) {
		this.id = id;
		this.regexpId = regexpId;
		this.responseId = responseId;
		this.prevTalkId = prevTalkId;
		this.startHour = startHour;
		this.endHour = endHour;
		this.charId = charId;
	}
}
