package com.example.aichan2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class WeatherIconManager {
	private Map<Integer, Drawable> drawableMap;
	private Context context;
	
	private String[] weatherStringArray = new String[] {
		"°‚ê"
		, "°‚ê‚Ì‚¿‚­‚à‚è"
		, "°‚ê‚Ì‚¿‰J"
		, "°‚êX‚­‚à‚è"
		, "°‚êX‰J"
		, "‚­‚à‚è"
		, "‚­‚à‚è‚Ì‚¿°‚ê"
		, "‚­‚à‚è‚Ì‚¿‰J"
		, "‚­‚à‚èX°‚ê"
		, "‚­‚à‚èX‰J"
		, "‰J"
		, "‰J‚Ì‚¿°‚ê"
		, "‰J‚Ì‚¿‚­‚à‚è"
		, "‰JX°‚ê"
		, "‰J‚Ì‚¿‚­‚à‚è"
		, "á"
	};
	
	private int[] weatherResourceIdArray = new int[] {
		R.drawable.hare
		, R.drawable.hare_kumori_af
		, R.drawable.hare_ame_af
		, R.drawable.hare_kumori_st
		, R.drawable.hare_ame_st
		, R.drawable.kumori
		, R.drawable.kumori_hare_af
		, R.drawable.kumori_ame_af
		, R.drawable.kumori_hare_st
		, R.drawable.kumori_ame_st
		, R.drawable.ame
		, R.drawable.ame_hare_af
		, R.drawable.ame_kumori_af
		, R.drawable.ame_hare_st
		, R.drawable.ame_kumori_st
		, R.drawable.yuki
	};
	
	WeatherIconManager(Context context) {
		drawableMap = new HashMap<Integer, Drawable>();
		this.context = context;
	}
	
	public Drawable getIcon(String weather) {
		Drawable d;
		
		int resId = getResId(weather);
		d = drawableMap.get(resId);
		if(d == null) {
			if(resId == -1) {
				d = context.getResources().getDrawable(R.drawable.hare);
			} else {
				d = context.getResources().getDrawable(resId);
			}
			drawableMap.put(resId, d);
		}
		
		return d;
	}
	
	private int getResId(String weather) {
		for(int i=0; i<weatherStringArray.length; i++) {
			if(weather.equals(weatherStringArray[i])) {
				return weatherResourceIdArray[i];
			}
		}
		return -1;
	}
}
