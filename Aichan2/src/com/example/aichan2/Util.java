package com.example.aichan2;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class Util {
	/**
	 * 0000/00/00 �܂��� 00/00 �̃p�[�X
	 * @return 0.�N, 1.��, 2.���@�܂��́@0.��, 1.��
	 */
	public static int[] parseDate(String strDate) {
		String[] sp = strDate.split("/");
		int[] result = new int[sp.length];
		for(int i=0; i<sp.length; i++) {
			result[i] = Integer.parseInt(sp[i]);
		}
		return result;
	}
	
	/**
	 * �A���t�@�x�b�g3�����̗j��(Aaa)�𐔒l�ɕϊ�
	 * @return 0.���@�`�@6.�y �@�s���ȏꍇ��-1
	 */
	public static int parseIntWeekDay(String strDate) {
		String[] strW = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		for(int i=0; i<strW.length; i++) {
			if(strDate.equals(strW[i])) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * �[���Ńp�f�B���O
	 */
	public static String zeroPadding(int num, int length) {
		return zeroPadding(num+"", length);
	}
	
	/**
	 * �[���Ńp�f�B���O
	 */
	public static String zeroPadding(String num, int length) {
		while(num.length() < length) {
			num = "0"+ num;
		}
		return num;
	}
	
	
	/**
	 * TextView�̃t�H���g��ݒ�
	 * @param ���[�gview
	 */
	public static void setFont(View view) {
		if(view instanceof ViewGroup) { 
			ViewGroup vg = (ViewGroup)view;
			int cnt = vg.getChildCount();
			for(int i=0; i<cnt; i++) {
				setFont(vg.getChildAt(i));
			}
		} else if(view instanceof TextView) {
			((TextView)view).setTypeface(App.getInstance().getFont());
		}
	}
}
