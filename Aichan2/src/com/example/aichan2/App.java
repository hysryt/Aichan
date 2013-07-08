package com.example.aichan2;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

public class App extends Application {
	private static final String FONT_NAME = "migmix-1p-regular.ttf";
	
	public static final String FIRST_BOOT = "firstBoot";
	
	public static final String PREFKEY_USERNAME = "userName";
	
	// ���O�C�����ĂȂ��ꍇ�͑��݂��Ȃ����󕶎�("")
	public static final String PREFKEY_LOGIN_USERID = "loginUserId";
	
	private static App instance = null;
	private Typeface font;
	private Typeface defaultFont;
	
	private String mUserName;
	
	public App() {
		instance = this;
	}
	
	public static App getInstance() {
		return instance;
	}
	
	public Typeface getFont() {
		if(font == null) {
			font = Typeface.createFromAsset(getAssets(), FONT_NAME);
		}
		return font;
	}
	
	public void setUserName(String userName) {
		this.mUserName = userName;
	}
	
	public String getUserName() {
		return mUserName;
	}
}
