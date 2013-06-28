package com.example.aichan2;

import java.util.Random;

import android.os.Handler;

public class Blink {
	// まばたき中の時間
	final static int BLINK_TIME = 150;
	
	// まばたきの最大間隔
	final static int MAX_BLINK_INTERVAL = 5000;
	
	
	// まばたきリスナ
	BlinkEventListener blinkListener = null;
	
	// まばたきフラグ
	boolean enableBlink = false;
	
	
	/*
	 *  まばたき開始
	 */
	public void startBlink(BlinkEventListener listener) {
		blinkListener = listener;
		enableBlink = true;
		
		// 「ランダム時間後に目を閉じるハンドラ」をポスト
		postCloseHandler();
	}
	
	public void stopBlink() {
		blinkListener = null;
		enableBlink = false;
	}
	
	
	/*
	 *  目を閉じて一定時間後開く
	 */
	private void closeEye() {
		blinkListener.closeCharacterEye();
		
		// 「一定時間後に目を開けるハンドラ」をポスト
		postOpenHandler();
	}
	
	
	/*
	 *  目を開いて目を閉じるハンドラをポスト
	 */
	private void openEye() {
		if(blinkListener != null) blinkListener.openCharacterEye();
		
		// 「ランダム時間後に目を閉じるハンドラ」をポスト
		postCloseHandler();
	}
	
	
	/*
	 *  ハンドラをポストして一定時間後に目を開ける
	 */
	private void postOpenHandler() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				openEye();
			}
		}, BLINK_TIME);
	}
	
	
	/*
	 *  ハンドラをポストしてランダム時間後に目を閉じる
	 */
	private void postCloseHandler() {
		// 次のまばたきまでの時間をランダムで決める
        Random rnd = new Random();
        int r = rnd.nextInt(MAX_BLINK_INTERVAL);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(enableBlink) {
					closeEye();
				}
			}
		}, r);
	}
}
