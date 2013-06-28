package com.example.aichan2;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

public class MessageManager {
	public static MessageManager instance = null;
	
	private Handler handler = null;
	
	private TextView messageView = null;
	
	
	private MessageManager() {
	}
	
	
	public static MessageManager getInstance() {
		if(instance == null) {
			instance = new MessageManager();
		}
		
		return instance;
	}
	
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public void setMessageView(TextView messageView) {
		this.messageView = messageView;
	}
	
	public void setMessage(final String msg) {
		
		if(handler == null) {
			Log.e("MessageManager", "Handler ‚ªİ’è‚³‚ê‚Ä‚È‚¢");
			return;
		}
		
		if(messageView == null) {
			Log.e("MessageManager", "TextView ‚ªİ’è‚³‚ê‚Ä‚È‚¢");
		}
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				String message = "<font color='white'>"+ msg +"</font>";
				messageView.setText(Html.fromHtml(message));
			}
		});
	}
}
