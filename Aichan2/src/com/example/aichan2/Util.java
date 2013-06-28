package com.example.aichan2;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class Util {
	/**
	 * 0000/00/00 または 00/00 のパース
	 * @return 0.年, 1.月, 2.日　または　0.月, 1.日
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
	 * アルファベット3文字の曜日(Aaa)を数値に変換
	 * @return 0.日　～　6.土 　不明な場合は-1
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
	 * ゼロでパディング
	 */
	public static String zeroPadding(int num, int length) {
		return zeroPadding(num+"", length);
	}
	
	/**
	 * ゼロでパディング
	 */
	public static String zeroPadding(String num, int length) {
		while(num.length() < length) {
			num = "0"+ num;
		}
		return num;
	}
	
	
	/**
	 * TextViewのフォントを設定
	 * @param ルートview
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
