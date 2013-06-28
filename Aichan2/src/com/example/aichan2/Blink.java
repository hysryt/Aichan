package com.example.aichan2;

import java.util.Random;

import android.os.Handler;

public class Blink {
	// �܂΂������̎���
	final static int BLINK_TIME = 150;
	
	// �܂΂����̍ő�Ԋu
	final static int MAX_BLINK_INTERVAL = 5000;
	
	
	// �܂΂������X�i
	BlinkEventListener blinkListener = null;
	
	// �܂΂����t���O
	boolean enableBlink = false;
	
	
	/*
	 *  �܂΂����J�n
	 */
	public void startBlink(BlinkEventListener listener) {
		blinkListener = listener;
		enableBlink = true;
		
		// �u�����_�����Ԍ�ɖڂ����n���h���v���|�X�g
		postCloseHandler();
	}
	
	public void stopBlink() {
		blinkListener = null;
		enableBlink = false;
	}
	
	
	/*
	 *  �ڂ���Ĉ�莞�Ԍ�J��
	 */
	private void closeEye() {
		blinkListener.closeCharacterEye();
		
		// �u��莞�Ԍ�ɖڂ��J����n���h���v���|�X�g
		postOpenHandler();
	}
	
	
	/*
	 *  �ڂ��J���Ėڂ����n���h�����|�X�g
	 */
	private void openEye() {
		if(blinkListener != null) blinkListener.openCharacterEye();
		
		// �u�����_�����Ԍ�ɖڂ����n���h���v���|�X�g
		postCloseHandler();
	}
	
	
	/*
	 *  �n���h�����|�X�g���Ĉ�莞�Ԍ�ɖڂ��J����
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
	 *  �n���h�����|�X�g���ă����_�����Ԍ�ɖڂ����
	 */
	private void postCloseHandler() {
		// ���̂܂΂����܂ł̎��Ԃ������_���Ō��߂�
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
