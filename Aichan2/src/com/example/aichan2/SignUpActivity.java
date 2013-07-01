package com.example.aichan2;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class SignUpActivity extends Activity {
	EditText userIdEditText;
	EditText passwordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		userIdEditText = (EditText)findViewById(R.id.userId);
		passwordEditText = (EditText)findViewById(R.id.password);
		
		userIdEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int arg1, KeyEvent arg2) {
				((EditText)v).setError(null);
				return false;
			}
		});
		
		findViewById(R.id.duplicationCheckButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickDuplicationCheck();
			}
		});
		
		findViewById(R.id.okButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickOk();
			}
		});
		
		Util.setFont(findViewById(R.id.root));
	}
	
	
	private void onClickDuplicationCheck() {
		// �܂��K��ɉ��������[�UID�����`�F�b�N
		if(!checkUserIdPolicy()) return;
		
		// TODO: �d���`�F�b�N
	}
	
	private void onClickOk() {
		// �K��̉����Ă邩���`�F�b�N
		if(!checkUserIdPolicy()) return;
		if(!checkPasswordPolicy()) return;
		
		// TODO: �o�^����
	}
	
	private boolean checkUserIdPolicy() {
		String userId = userIdEditText.getText().toString();
		
		// 12������蒷��������
		if(userId.length() > 12) {
			userIdEditText.setError(format("12�����ȓ��ɂ��Ă�������"));
			return false;
		}
		
		// �p�����ȊO���g���Ă���
		if(!userId.matches("^[0-9a-zA-Z]*$")) {
			userIdEditText.setError(format("�p�����̂݉\"));
			return false;
		}
		
		return true;
	}
	
	private boolean checkPasswordPolicy() {
		String pass = passwordEditText.getText().toString();
		
		// 4�������Z����12������蒷��������
		if(pass.length() < 4 || pass.length() > 12) {
			passwordEditText.setError(format("4�����ȏ�12�����ȓ��ɂ��Ă�������"));
			return false;
		}
		
		// �p�����ȊO���g���Ă���
		if(!pass.matches("^[0-9a-zA-Z]*$")) {
			passwordEditText.setError(format("�p�����̂݉\"));
			return false;
		}
		
		return true;
	}
	
	private Spanned format(String str) {
		return Html.fromHtml("<font color='black'>"+ str +"</font>");
	}
}
