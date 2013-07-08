package com.example.aichan2;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

public class SerifView extends TextView {	
	MyHandler handler;
	
	public SerifView(Context context) {
		this(context, null);
	}
	
	public SerifView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
	}
	
	public SerifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		handler = new MyHandler();
		handler.setView(this);
	}

	
	/**
	 * ������\��
	 * @param text �\�������镶����
	 * @param flg �ꕶ�����\�������邩�ǂ���
	 */
	public void setText(CharSequence text, boolean flg) {
		if(flg) {
			handler.setText((String)text);
		} else {
			setText(text);
		}
	}
	
	public void setText(String text) {
		setText(text, false);
	}
}

class MyHandler extends Handler {
	private static final int INTERVAL = 50;
	
	private TextView view;
	private String text;
	private String currentString;
	private int cnt = 0;
	private boolean flg = false;
	
	public void setText(String text) {
		this.text = text;
		cnt = 0;
		
		if(flg) {
			
		} else {
			sendMessage(new Message());
			flg = true;
		}
	}
	
	public void setView(TextView view) {
		this.view = view;
	}
	
	
	public void handleMessage(Message msg) {
		int interval = INTERVAL;
		
		currentString = text.substring(0, cnt);
		
		if(view != null) {
			// �A�j���[�V�������͕�����̍Ō�ɃA���_�[�o�[�i�J�[�\���I�Ȃ́j��\��
			view.setText(currentString + (cnt == text.length() ? "" : "_"));
		}
		
		if(cnt >= text.length()) {
			flg = false;
		} else {
			// �u�A�v�܂��́u�E�E�E�v�ŏ�����~
			if(currentString.endsWith("�A") || (currentString.endsWith("�E�E�E") && !text.substring(cnt).startsWith("�E")) || currentString.endsWith("�v")) {
				interval = 200;
			}
			
			cnt++;
			sendMessageDelayed(new Message(), interval);
		}
	}
}
