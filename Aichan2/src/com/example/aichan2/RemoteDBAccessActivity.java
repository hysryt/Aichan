package com.example.aichan2;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RemoteDBAccessActivity extends Activity {
	public static final String ACCESS_REMOTE_DB_EXTRA = "access_remote_db_extra";
	
	ProgressAndMessageAsyncTask task;
	ImageView progressImage = null;
	
	Button stopButton;
	Button finishButton;
	
	Drawable read1, read2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accessremotedb);
		
		read1 = getResources().getDrawable(R.drawable.reading);
		read2 = getResources().getDrawable(R.drawable.reading2);
		
		Typeface font = App.getInstance().getFont();
		
		progressImage = (ImageView)findViewById(R.id.imageView01);
		TextView messageView = (TextView)findViewById(R.id.progressMessage);
		messageView.setTypeface(font);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
		stopButton = (Button)findViewById(R.id.stopButton);
		stopButton.setTypeface(font);
		
		finishButton = (Button)findViewById(R.id.finishButton);
		finishButton.setTypeface(font);
		
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickStopButton();
			}
		});
		
		finishButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickFinishButton();
			}
		});
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		boolean accessRemoteDB = b.getBoolean(ACCESS_REMOTE_DB_EXTRA);
		
		if(accessRemoteDB) {
			task = createAccessRemoteDBTask();
		} else {
			task = createDBTask();
		}
		
		task.setViews(messageView, progressBar);
		task.execute();
	}
	
	private ProgressAndMessageAsyncTask createDBTask() {

		task = new ProgressAndMessageAsyncTask() {
			Boolean stopFlg = false;
			
			@Override
			protected Object doInBackground(Object... obj) {
				DatabaseOpenHelper helper = new DatabaseOpenHelper(getApplicationContext());
				DatabaseAccess da = new DatabaseAccess(helper);
				da.insertBasicTalkData(task);
				da = null;
				helper.close();
				return null;
			}
			
			@Override
			protected void onPostExecute(Object result) {
				if(!task.isError()) {
					forwardProgress(100, "äÆóπÅI");
					progressImage.setImageDrawable(read2);
				} else {
					forwardProgress(100);
					progressImage.setImageResource(R.drawable.reading6);
				}
				stopButton.setEnabled(false);
				finishButton.setEnabled(true);
			}
			
			@Override
			public void stop() {
				stopFlg = true;
			}
		};
		
		return task;
	}
	
	private ProgressAndMessageAsyncTask createAccessRemoteDBTask() {
		task = new ProgressAndMessageAsyncTask() {
			Task taskManager = new Task();
			
			@Override
			protected Object doInBackground(Object... obj) {
				Replicate replicate = new Replicate();
				DatabaseOpenHelper helper = new DatabaseOpenHelper(getApplicationContext());
				SQLiteDatabase db = helper.getWritableDatabase();
				
				forwardProgress(3);
				
				replicate.replicate(db, this, taskManager);
				
				db.close();
				helper.close();
				return null;
			}
			
			@Override
			protected void onPostExecute(Object result) {
				if(!task.isError()) {
					forwardProgress(100, "äÆóπÅI");
					progressImage.setImageDrawable(read2);
				} else {
					forwardProgress(100);
					progressImage.setImageResource(R.drawable.reading6);
				}
				stopButton.setEnabled(false);
				finishButton.setEnabled(true);
			}
			
			@Override
			public void stop() {
				taskManager.stop();
			}
		};
		
		return task;
	}
	
	public void clickStopButton() {
		task.forwardProgress("íÜé~ÇµÇ‹ÇµÇΩ");
		progressImage.setImageResource(R.drawable.reading6);
		
		task.stop();
		
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void clickFinishButton() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		read1.setCallback(null);
		read2.setCallback(null);
		progressImage.setImageDrawable(null);
	}
}
