package com.example.aichan2;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.widget.TextView;

class DebugViewer {
	private static List<String> list = new ArrayList<String>();
	private static int maxLine = 50;
	private static TextView debugView = null;
	
	public static void d(String line) {
		list.add(line);
		while(list.size() > maxLine) {
			list.remove(0);
		}
		reload();
	}

	public static void setTextView(TextView view) {
		debugView = view;
		reload();
	}

	private static void reload() {
		if(debugView == null) return;
		
		new Handler().post(new Runnable() {
			public void run() {
				StringBuffer sb = new StringBuffer();
				for(int i=0; i<list.size(); i++) {
					sb.append(list.get(i));
					sb.append("\n");
				}
				debugView.setText(sb.toString());
			}
		});
	}
}