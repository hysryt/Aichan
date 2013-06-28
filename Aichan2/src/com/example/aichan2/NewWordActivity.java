package com.example.aichan2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewWordActivity extends Activity {
	LayoutInflater inflater;
	
	EditText sendMessageEdit;
	LinearLayout resMessagesContainer;
	
	Button commitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newword);
		
		Button editButton = (Button)findViewById(R.id.editButton);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: 編集画面に移動
			}
		});
		
		Button addButton = (Button)findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addResMessageForm();
			}
		});
		
		commitButton = (Button)findViewById(R.id.commitButton);
		commitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				commit();
			}
		});
		
		sendMessageEdit = (EditText)findViewById(R.id.sendMessage);
		resMessagesContainer = (LinearLayout)findViewById(R.id.resMessageWrapper);
		
		inflater = getLayoutInflater();
		
		// 起動時に返信メッセージを一つ追加
		addResMessageForm();
		
		sendMessageEdit.requestFocus();
		
		Util.setFont(findViewById(R.id.root));
	}
	
	/**
	 *  返信メッセージのフォームを追加
	 */
	private void addResMessageForm() {		
		LinearLayout item = (LinearLayout)inflater.inflate(R.layout.item_newworditem, null);
		
		item.findViewById(R.id.removeButton).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				removeResMessageForm((LinearLayout)v.getParent());
			}
		});
		
		// 一旦全てのフォーカスをクリア
		for(int i=0; i<item.getChildCount(); i++) {
			item.getChildAt(i).clearFocus();
		}
		
		Util.setFont(item);
		
		// フォーム追加
		resMessagesContainer.addView(item);
		
		// 追加したフォームにフォーカスを当てる
		item.findViewById(R.id.resMessage).requestFocus();
		
		// 連番を更新
		updateSerialNumber();
	}
	
	/**
	 * 返信メッセージのフォームを削除
	 */
	private void removeResMessageForm(LinearLayout form) {
		// フォームの個数が1つかどうか
		if(resMessagesContainer.getChildCount() == 1) {
			// 1つならばフォームは消さずに内容だけ消す
			((EditText)form.findViewById(R.id.resMessage)).setText("");
		} else {
			// 1つ以上あればフォームごと消す
			resMessagesContainer.removeView(form);
		}
		
		// 連番を更新
		updateSerialNumber();
	}
	
	/**
	 *  返信メッセージの連番を更新
	 */
	private void updateSerialNumber() {
		// TODO: 更新処理
		int count = resMessagesContainer.getChildCount();
		
		for(int i=0; i<count; i++) {
			View form = resMessagesContainer.getChildAt(i);
			((TextView)form.findViewById(R.id.serial)).setText("返信"+ (i+1));
		}
	}
	
	/**
	 *  覚えさせる
	 */
	private void commit() {
		String sendMessage = sendMessageEdit.getText().toString();
		
		if(sendMessage.equals("")) {
			sendMessageEdit.setError("入力して下さい");
			return;
		}
		
		List<String> list = new ArrayList<String>();
		
		int count = resMessagesContainer.getChildCount();
		for(int i=0; i<count; i++) {
			View form = resMessagesContainer.getChildAt(i);
			String resMessage = ((EditText)form.findViewById(R.id.resMessage)).getText().toString();
			list.add(resMessage);
		}
		
		String[] resMessages = list.toArray(new String[list.size()]);
		
		boolean allblank = true;
		for(int i=0; i<resMessages.length; i++) {
			if(!resMessages[i].equals("")) {
				allblank = false;
				break;
			}
		}
		
		if(allblank) {
			View form = resMessagesContainer.getChildAt(0);
			((EditText)form.findViewById(R.id.resMessage)).setError("入力して下さい");
			return;
		}
		
		commitButton.setEnabled(false);
		
		DatabaseOpenHelper helper = new DatabaseOpenHelper(this);
		DatabaseAccess dbAccess = new DatabaseAccess(helper);
		
		boolean result = dbAccess.registUserMessage(sendMessage, resMessages);
		if(result) {
			Toast.makeText(this, "覚えました！", Toast.LENGTH_SHORT).show();
		}
		
		commitButton.setEnabled(true);
		
		helper.close();
	}
}
