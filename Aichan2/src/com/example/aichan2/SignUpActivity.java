package com.example.aichan2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SignUpActivity extends Activity {
	EditText userIdEditText;
	EditText passwordEditText;
	
	Button duplicationButton;
	Button okButton;
	ProgressBar progress;
	
	String duplicationCheckFile = "DuplicationCheck.php";
	String signUpFile = "signUp.php";

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
		
		duplicationButton = (Button)findViewById(R.id.duplicationCheckButton);
		duplicationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickDuplicationCheck();
			}
		});
		
		okButton = (Button)findViewById(R.id.okButton);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickOk();
			}
		});
		
		progress = (ProgressBar)findViewById(R.id.progress);
		
		Util.setFont(findViewById(R.id.root));
	}
	
	
	private void onClickDuplicationCheck() {
		// �܂��K��ɉ��������[�UID�����`�F�b�N
		if(!checkUserIdPolicy()) return;
		
		duplicationButton.setEnabled(false);
		okButton.setEnabled(false);
		progress.setVisibility(View.VISIBLE);
		
		String strUrl = "http://"+ App.SERVER_IP +"/"+ duplicationCheckFile;
		String tempUserId = userIdEditText.getText().toString();
		
		// �d���`�F�b�N
		NetAccessAsyncTask task = new NetAccessAsyncTask() {		
			@Override
			protected void onPostExecute(String result) {
				duplicationButton.setEnabled(true);
				okButton.setEnabled(true);
				progress.setVisibility(View.INVISIBLE);
				
				if(this.isError()) {
					Toast.makeText(SignUpActivity.this, "�T�[�o�ɃA�N�Z�X�ł��܂���", Toast.LENGTH_SHORT).show();
				}
				if(result.equals("OK")) {
					Toast.makeText(SignUpActivity.this, "�g�p�\�ł�", Toast.LENGTH_LONG).show();
				} else if(result.equals("NG")){
					Toast.makeText(SignUpActivity.this, "���Ɏg�p����Ă��܂�", Toast.LENGTH_LONG).show();
				}
			}
		};
		task.setTimeout(3000);
		task.execute("http://"+ App.SERVER_IP +"/"+ duplicationCheckFile, "POST", "userId="+ tempUserId);
	}
	
	private void onClickOk() {
		// �K��̉����Ă邩���`�F�b�N
		if(!checkUserIdPolicy()) return;
		if(!checkPasswordPolicy()) return;
		
		duplicationButton.setEnabled(false);
		okButton.setEnabled(false);
		progress.setVisibility(View.VISIBLE);
		
		String tempUserId = userIdEditText.getText().toString();
		String tempPass = passwordEditText.getText().toString();
		
		// �o�^����
		NetAccessAsyncTask task = new NetAccessAsyncTask() {		
			@Override
			protected void onPostExecute(String result) {
				duplicationButton.setEnabled(true);
				okButton.setEnabled(true);
				progress.setVisibility(View.INVISIBLE);
				
				if(this.isError()) {
					Toast.makeText(SignUpActivity.this, "�T�[�o�ɃA�N�Z�X�ł��܂���ł���", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result.equals("OK")) {
					Toast.makeText(SignUpActivity.this, "�o�^�ł��܂���", Toast.LENGTH_LONG).show();
					String userId = userIdEditText.getText().toString();
					String password = passwordEditText.getText().toString();

					// ���݂̃��O�C��ID�Ƃ��Đݒ�
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
					Editor editor = pref.edit();
					editor.putString(App.PREFKEY_LOGIN_USERID, userId);
					editor.commit();
					
					// �f�[�^�x�[�X�ɓo�^
					DatabaseOpenHelper helper = new DatabaseOpenHelper(getApplicationContext());
					DatabaseAccess da = new DatabaseAccess(helper);
					da.signUpUser(userId, password);
					helper.close();
					
					Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
					startActivity(intent);
				} else if(result.equals("Duplicate")){
					Toast.makeText(SignUpActivity.this, "���Ɏg�p����Ă��܂�", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(SignUpActivity.this, "�o�^���s", Toast.LENGTH_LONG).show();
				}
			}
		};
		
		task.setTimeout(3000);
		
		// TODO: ���[�U�������ɓo�^����Ă���ꍇ�͂�����ꏏ�ɓo�^������
		task.execute("http://"+ App.SERVER_IP +"/"+ signUpFile, "POST", "userId="+ tempUserId +"&password="+ tempPass);
	}
	
	private boolean checkUserIdPolicy() {
		String userId = userIdEditText.getText().toString();
		
		// ���͂���ĂȂ�
		if(userId.length() == 0) {
			userIdEditText.setError(format("���͂��ĉ�����"));
			return false;
		}
		
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
