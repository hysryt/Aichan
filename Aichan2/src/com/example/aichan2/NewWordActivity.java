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
				// TODO: �ҏW��ʂɈړ�
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
		
		// �N�����ɕԐM���b�Z�[�W����ǉ�
		addResMessageForm();
		
		sendMessageEdit.requestFocus();
		
		Util.setFont(findViewById(R.id.root));
	}
	
	/**
	 *  �ԐM���b�Z�[�W�̃t�H�[����ǉ�
	 */
	private void addResMessageForm() {		
		LinearLayout item = (LinearLayout)inflater.inflate(R.layout.item_newworditem, null);
		
		item.findViewById(R.id.removeButton).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				removeResMessageForm((LinearLayout)v.getParent());
			}
		});
		
		// ��U�S�Ẵt�H�[�J�X���N���A
		for(int i=0; i<item.getChildCount(); i++) {
			item.getChildAt(i).clearFocus();
		}
		
		Util.setFont(item);
		
		// �t�H�[���ǉ�
		resMessagesContainer.addView(item);
		
		// �ǉ������t�H�[���Ƀt�H�[�J�X�𓖂Ă�
		item.findViewById(R.id.resMessage).requestFocus();
		
		// �A�Ԃ��X�V
		updateSerialNumber();
	}
	
	/**
	 * �ԐM���b�Z�[�W�̃t�H�[�����폜
	 */
	private void removeResMessageForm(LinearLayout form) {
		// �t�H�[���̌���1���ǂ���
		if(resMessagesContainer.getChildCount() == 1) {
			// 1�Ȃ�΃t�H�[���͏������ɓ��e��������
			((EditText)form.findViewById(R.id.resMessage)).setText("");
		} else {
			// 1�ȏ゠��΃t�H�[�����Ə���
			resMessagesContainer.removeView(form);
		}
		
		// �A�Ԃ��X�V
		updateSerialNumber();
	}
	
	/**
	 *  �ԐM���b�Z�[�W�̘A�Ԃ��X�V
	 */
	private void updateSerialNumber() {
		// TODO: �X�V����
		int count = resMessagesContainer.getChildCount();
		
		for(int i=0; i<count; i++) {
			View form = resMessagesContainer.getChildAt(i);
			((TextView)form.findViewById(R.id.serial)).setText("�ԐM"+ (i+1));
		}
	}
	
	/**
	 *  �o��������
	 */
	private void commit() {
		String sendMessage = sendMessageEdit.getText().toString();
		
		if(sendMessage.equals("")) {
			sendMessageEdit.setError("���͂��ĉ�����");
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
			((EditText)form.findViewById(R.id.resMessage)).setError("���͂��ĉ�����");
			return;
		}
		
		commitButton.setEnabled(false);
		
		DatabaseOpenHelper helper = new DatabaseOpenHelper(this);
		DatabaseAccess dbAccess = new DatabaseAccess(helper);
		
		boolean result = dbAccess.registUserMessage(sendMessage, resMessages);
		if(result) {
			Toast.makeText(this, "�o���܂����I", Toast.LENGTH_SHORT).show();
		}
		
		commitButton.setEnabled(true);
		
		helper.close();
	}
}
