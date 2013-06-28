package com.example.aichan2;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class ProgressAndMessageAsyncTask extends AsyncTask {
	TextView messageView = null;
	ProgressBar progress = null;
	int prog = 0;
	String text = null;
	boolean errorFlg = false;

	public void setViews(TextView messageView, ProgressBar progress) {
		this.messageView = messageView;
		this.progress = progress;
	}
	
	abstract protected Object doInBackground(Object... obj);
	
	protected void onProgressUpdate(Object... values) {
		if(progress != null) {
			progress.setProgress(prog);
		}
		
		if(messageView != null) {
			messageView.setText(text);
		}
	}
	
	protected void forwardProgress(int prog) {
		this.prog = prog;
		publishProgress();
	}
	
	protected void forwardProgress(String text) {
		this.text = text;
		publishProgress();
	}
	
	protected void forwardProgress(int prog, String text) {
		this.prog = prog;
		this.text = text;
		publishProgress();
	}
	
	public void error() {
		errorFlg = true;
	}
	
	public boolean isError() {
		return errorFlg;
	}

	abstract public void stop();
}
