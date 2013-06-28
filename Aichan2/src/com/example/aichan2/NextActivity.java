package com.example.aichan2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class NextActivity extends Activity {
	final static String EXTRA_SEND_MESSAGE = "extra.SEND_MESSAGE";
	
	EditText editText;
	TextView textView;
	Button talkButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.next_layout);
		
		// �G�f�B�b�g�e�L�X�g�̃C���X�^���X�擾
		editText = (EditText)findViewById(R.id.editText1);
		
		// �G�f�B�b�g�e�L�X�g�̃A�N�V�������X�i
		editText.setOnEditorActionListener(new OnEditorActionListener() {  
		    @Override  
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        talkButton.performClick();
		        return true;
		    }
		}); 
		
		
		// �e�L�X�g�r���[�̃C���X�^���X�擾
		textView = (TextView)findViewById(R.id.progressMessage);
		
		// �e�L�X�g�r���[�̃N���b�N���X�i��ݒ�
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		// �{�^���̃C���X�^���X���擾
		talkButton = (Button)findViewById(R.id.tenkiButton);
		
		// �{�^���̃N���b�N���X�i��ݒ�
		talkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickTalkButton();
			}
		});
		
		Util.setFont(findViewById(R.id.root));
		
		// �L�[�{�[�h�\��
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	void clickTalkButton() {
		String msg = editText.getText().toString();
		Intent intent = new Intent();
		intent.putExtra(EXTRA_SEND_MESSAGE, msg);
		setResult(RESULT_OK, intent);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(0, R.anim.cl_ex);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}