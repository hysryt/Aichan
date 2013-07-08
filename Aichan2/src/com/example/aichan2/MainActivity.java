package com.example.aichan2;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements BlinkEventListener {
	public static final String EXTRA_USERNAME_IS_NOT_SET = "com.example.aichan2.extraNotSetName";
	
	final static boolean DEBUG = true;
	
	final static String AI_NAME = "あい";
	
	final static int REQUEST_CODE_KEYBOARD = 100;
	final static int REQUEST_CODE_MIKE = 200;
	
	final static int KEYBOARD = 0;
	final static int MIKE = 1;
	
	final static int HEAR_USER_NAME = 0;
	final static int WAIT_YESNO = 1;
	final static int ENDING = 3;
	int setUserNameState = HEAR_USER_NAME;
	
	Button keyboardButton;
	Button mikeButton;
	
	Button escapeButton;
	
	TextView textView1;
	SerifView messageWindow;
	ImageView aiView;

	Blink blink;
	ImageView blinkImageView;

	Talk talk;
	Mike mike;
	
	String mUserName;
	String mTempUserName;
	
	boolean downloadFlg = true;
	
	boolean enableDebugWindow = false;
	ScrollView debugScrollView;
	TextView debugView;
	
	boolean msgOnebyone;
	
	boolean setUserName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try {
			Log.d("MainActivity", "ドライバ読み込み中...");
			Class.forName("com.mysql.jdbc.Driver");
			Log.d("MainActivity", "ドライバ読み込み成功");
		} catch (ClassNotFoundException e) {
			Log.d("MainActivity", "ドライバ読み込み失敗");
			e.printStackTrace();
		}
		
		super.onCreate(savedInstanceState);
		
		LinearLayout root = (LinearLayout)View.inflate(getApplicationContext(), R.layout.activity_main, null);
		setContentView(root);
		
		boolean showDialog = false;
		
		DatabaseOpenHelper helper = new DatabaseOpenHelper(getApplicationContext());
		DatabaseAccess da = new DatabaseAccess(helper);
		
		if(da.isEmpty()) {
			showDialog = true;
			showDatabaseAlert();
			// showDatabaseAlertのボタンを押すと Activity 終了
		}
		
		da = null;
		helper.close();
		

		
		// debug
		debugScrollView = (ScrollView)findViewById(R.id.debugView);
		debugView = (TextView)findViewById(R.id.textDebugView);
		DebugViewer.setTextView(debugView);
		
		talk = new Talk();
		talk.openDB(getApplicationContext());
		
		mike = new Mike();
		
		
		// テキストビューのインスタンス取得
		textView1 = (TextView)root.findViewById(R.id.progressMessage);
		// メッセージウィンドウのインスタンス取得
		messageWindow = (SerifView)root.findViewById(R.id.msgWindow);
		//messageWindow.startSpeak("やあ");
		messageWindow.setText("やあ", true);
		// 人工無能のイメージビューのインスタンス取得
		aiView = (ImageView)root.findViewById(R.id.aiView);
		
		
		
		// キーボードボタンのインスタンス取得
		keyboardButton = (Button)root.findViewById(R.id.tenkiButton);
		keyboardButton.setTypeface(App.getInstance().getFont());
		// マイクボタンのインスタンス取得
		mikeButton = (Button)root.findViewById(R.id.mike_button);
		mikeButton.setTypeface(App.getInstance().getFont());
		
		escapeButton = (Button)root.findViewById(R.id.escape);

		
		// キーボードボタンにクリックリスナをセット
		keyboardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// NextActivity への Intentインスタンス生成
				Intent intent = new Intent(v.getContext(), NextActivity.class);
				
				startActivityForResult(intent, REQUEST_CODE_KEYBOARD);
				
				// 開始アニメーション
				overridePendingTransition(R.anim.op_en, 0);
			}
		});
		

		
		// マイクボタンにクリックリスナをセット
		mikeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickMikeButton();
			}
		});	
		
		// 人工無能のイメージビューにクリックリスナをセット
		aiView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				clickAIView();
			}
		});
		
		// メッセージビューにクリックリスナをセット
		messageWindow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 何もしない
			}
		});
		
		
		// まばたき
		blinkImageView = (ImageView)findViewById(R.id.mabataki);
		blink = new Blink();
		blink.startBlink(this);
		
		//messageWindow.startSpeak("size:"+talk.regexpList.size());
		//messageWindow.setText("si\1ze:"+talk.regexpList.size(), true);
		
		
		// メッセージマネージャ
		MessageManager mm = MessageManager.getInstance();
		mm.setHandler(new Handler());
		mm.setMessageView(messageWindow);
		
		
		// キャラクタマネージャ
		CharacterManager cm = CharacterManager.getInstance();
		Character character = new Character(R.drawable.aiver8_c3_ver3, R.drawable.aiver8_c3_ver2_2);
		character.setExpressionResourceId(CharacterManager.EXPRESSION_THINKING, R.drawable.aiver8_c3_thinking_3, R.drawable.aiver8_c3_thinking_3_2);
		cm.setCharacterView(aiView);
		cm.setBlinkView(blinkImageView);
		cm.setCharacter(character);
		
		Util.setFont(findViewById(R.id.root));
		
		
		Intent srcIntent = getIntent();
		Log.d("MainActivity", srcIntent.toString());
		setUserName = srcIntent.getBooleanExtra(EXTRA_USERNAME_IS_NOT_SET, false);
		/*
		Bundle b = srcIntent.getExtras();
		Set<String> set = b.keySet();
		
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			Log.d("MainActivity", "extra: "+ it.next());
		}
		*/
		
		if(!showDialog) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			mUserName = pref.getString(App.PREFKEY_USERNAME, null);
			App.getInstance().setUserName(mUserName);
	
			if(setUserName || mUserName == null) {
				setUserName = true;
				setUserNameState = HEAR_USER_NAME;
				// TODO ユーザー名を設定する
				messageWindow.setText("あなたのこと、なんて呼べばいい？", true);
			} else {
				messageWindow.setText(talk.sendMessage(this, "こんにちは"), true);
			}
		}
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		

//		aiView.setImageResource(R.drawable.aiver8);
//		blinkImageView.setImageResource(R.drawable.aiver5_2);
		

		
		// 設定の適用
		loadSetting();
	}
	
	@Override
	protected void onResume() {
		talk.reloadUserMessage();
		super.onResume();
	}


	private void loadSetting() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		// 文字サイズ設定
		float msgFontSize = Float.parseFloat(pref.getString("message_fontsize", "13"));
		messageWindow.setTextSize(msgFontSize);
		
		// メッセージを一文字ずつ表示するかどうか
		msgOnebyone = pref.getBoolean("message_onebyone", false);
	}
	
	
	private void clickMikeButton() {
		mike.start(this, REQUEST_CODE_MIKE);
	}
	
	private void clickAIView() {
		startMenuActivity();
	}
	
	private void startMenuActivity() {
		Intent intent = new Intent(this, MenuActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK && data != null) {
			// キー入力から帰ってきた
			if(requestCode == REQUEST_CODE_KEYBOARD) {				
				messageWindow.requestFocus();
				String send_msg = data.getStringExtra(NextActivity.EXTRA_SEND_MESSAGE);
				
				if(DEBUG && send_msg != null && (send_msg.equals("debug") || send_msg.equals("デバッグ"))) {
					if(enableDebugWindow) {
						enableDebugWindow = false;
						setResMessage("デバッグ画面を非表示");
					} else {
						enableDebugWindow = true;
						setResMessage("デバッグ画面を表示");
					}
					visibleDebugWindow(enableDebugWindow);
					return;
				}
				
				if(send_msg != null) {
					sendMessage(send_msg, KEYBOARD);
					/*
					setSendMessage(send_msg);
					
					String resMsg = talk.sendMessage(this, send_msg);
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					setResMessage(resMsg);
					*/
				}
			// マイク入力から帰ってきた
			} else if(requestCode == REQUEST_CODE_MIKE) {
				String send_msg = mike.getString(data);
				if(send_msg != null) {
					sendMessage(send_msg, MIKE);
					/*
					setSendMessage(send_msg);
					String resMsg = talk.sendMessage(this, send_msg);
					setResMessage(resMsg);
					*/
				}
			}
		}
	}
	
	private void setUserName(String userName) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putString(App.PREFKEY_USERNAME, userName);
		editor.commit();
		
		mUserName = userName;
		App.getInstance().setUserName(userName);
	}
	
	public String getUserName() {
		return mUserName;
	}
	
	private void sendMessage(String message, int from) {
		setSendMessage(message);
		
		// ユーザー名の設定中かどうか
		if(setUserName) {
			if(setUserNameState == HEAR_USER_NAME) {
				mTempUserName = message;
				setResMessage("「"+ mTempUserName +"」でいい？（はい／いいえ）");
				setUserNameState = WAIT_YESNO;
				
			} else if(setUserNameState == WAIT_YESNO) {
				if(message.equals("はい")) {
					setUserName(mTempUserName);
					setResMessage("よろしく、"+ mUserName);
					setUserNameState = ENDING;
					setUserName = false;
					
					// TODO: ログイン中なら名前をローカルデータベースとサーバデータベースに登録
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
					String loginUserId = pref.getString(App.PREFKEY_LOGIN_USERID, null);
					if(loginUserId != null && !loginUserId.equals("")) {
						String password;
						
						// ローカル
						DatabaseOpenHelper helper = new DatabaseOpenHelper(getApplicationContext());
						DatabaseAccess da = new DatabaseAccess(helper);
						da.storeUserName(loginUserId, mUserName);
						password = da.getPassword(loginUserId);
						helper.close();
						
						// サーバ
						NetAccessAsyncTask task = new NetAccessAsyncTask() {		
							@Override
							protected void onPostExecute(String result) {
							}
						};
						task.execute("http://192.168.60.1/storeUserName.php", "POST", "userId="+ loginUserId+"&password="+password+"&userName="+mUserName);
					} else {
					}
					
				} else if(message.equals("いいえ")) {
					setUserNameState = HEAR_USER_NAME;
					setResMessage("あなたのこと、なんて呼べばいい？");
				} else {
					setResMessage("「はい」か「いいえ」で答えて・・・\n「"+ mTempUserName +"」でいい？（はい／いいえ）");
				}
			}
			
		} else {
			// メッセージを送って返事を取得
			String resMsg = talk.sendMessage(this, message);
			
			// キーボードだと返信が速いので少し待つ
			if(from == KEYBOARD) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// 返事をセット
			setResMessage(resMsg);
		}
	}
	
	private void setSendMessage(String msg) {
		String name = mUserName;
		if(name == null) {
			name = "あなた";
		}
		textView1.setText(name +" > "+ msg);
	}
	
	private void setResMessage(String msg) {
		if(msgOnebyone) {
			//messageWindow.startSpeak(msg);
			messageWindow.setText(msg, true);
		} else {
			//msg = "<font color='white'>"+ msg +"</font>";
			//messageWindow.setText(Html.fromHtml(msg));
			messageWindow.setText(msg, false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.action_settings);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getApplicationContext(), MainPreferenceActivity.class);
				startActivity(intent);
				return false;
			}
		});
		return true;
	}
	
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		talk.closeDB();
		
		blink.stopBlink();
		
		CharacterManager cm = CharacterManager.getInstance();
		cm.releaseImageView();
		
		// メモリリーク対策としてImageViewのDrawableをnullにする
		aiView.setImageDrawable(null);
		blinkImageView.setImageDrawable(null);
		
		// DebugPrinterからTextViewへの参照を外す
		DebugViewer.setTextView(null);
	}

	
	/*
	 * BlinkEventListener
	 */
	@Override
	public void closeCharacterEye() {
		blinkImageView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void openCharacterEye() {
		blinkImageView.setVisibility(View.GONE);
	}
	
	private void visibleDebugWindow(boolean isVisible) {
		if(isVisible) {
			debugScrollView.setVisibility(View.VISIBLE);
		} else {
			debugScrollView.setVisibility(View.GONE);
		}
	}
	
	
	
	private void showDatabaseAlert() {
		AlertDialog.Builder downloadAlertBuilder = new AlertDialog.Builder(this);
		
		String crlf = System.getProperty("line.separator");

		downloadAlertBuilder.setTitle("データベース");
		downloadAlertBuilder.setMessage("会話データベースを構築します。"+ crlf + "インターネットから最新のデータを取得しますか？");
		
		downloadAlertBuilder.setPositiveButton("はい",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					clickAlertYes();
				}
			}
		);
		
		downloadAlertBuilder.setNegativeButton("いいえ",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					clickAlertNo();
				}
			}
		);

		
		AlertDialog downloadAlert = downloadAlertBuilder.create();

		downloadAlert.show();
	}
	
	private void clickAlertYes() {
		Intent intent = new Intent(this, RemoteDBAccessActivity.class);
		intent.putExtra(RemoteDBAccessActivity.ACCESS_REMOTE_DB_EXTRA, true);
		startActivity(intent);
		finish();
	}
	
	private void clickAlertNo() {
//		DatabaseOpenHelper helper = new DatabaseOpenHelper(getApplicationContext());
//		DatabaseAccess da = new DatabaseAccess(helper);
//		da.insertBasicTalkData();
//		da = null;
//		helper.close();

		Intent intent = new Intent(this, RemoteDBAccessActivity.class);
		intent.putExtra(RemoteDBAccessActivity.ACCESS_REMOTE_DB_EXTRA, false);
		startActivity(intent);
		finish();
	}


	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	

}
