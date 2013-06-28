package com.example.aichan2;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;

public class Character {

	int normalExpResId;
	int normalExpBlinkResId;
	
	List<Integer> expIdList = new ArrayList<Integer>();
	List<Integer> resIdList = new ArrayList<Integer>();
	List<Integer> blinkResIdList = new ArrayList<Integer>();
	
	
	Character(int normalExpResId) {
		this(normalExpResId, -1);
	}
	
	Character(int normalExpResId, int normalExpBlinkResId) {
		/*
		this.normalExpResId = normalExpResId;
		this.normalExpBlinkResId = normalExpBlinkResId;
		*/
		
		setExpressionResourceId(CharacterManager.EXPRESSION_NORMAL, normalExpResId, normalExpBlinkResId);
	}
	
	public void setNormalExpressionResourceId(int resId) {
		normalExpResId = resId;
	}
	
	public void setExpressionResourceId(int expId, int resId) {
		setExpressionResourceId(expId, resId, -1);
	}
	
	public void setExpressionResourceId(int expId, int resId, int blinkResId) {
		expIdList.add(expId);
		resIdList.add(resId);
		blinkResIdList.add(blinkResId);
	}
	
	public int getExpressionResourceId(int expId) {
		for(int i=0; i<expIdList.size(); i++) {
			int item = expIdList.get(i);
			if(item == expId) {
				return resIdList.get(i);
			}
		}
		
		return normalExpResId;
	}
	
	public int getExpressionBlinkResourceId(int expId) {
		for(int i=0; i<expIdList.size(); i++) {
			int item = expIdList.get(i);
			if(item == expId) {
				return blinkResIdList.get(i);
			}
		}
		
		return normalExpBlinkResId;
	}
	
	public boolean isBlinkableExpression(int expId) {
		for(int i=0; i<expIdList.size(); i++) {
			int eId = expIdList.get(i);
			if(eId == expId) {
				int bId = blinkResIdList.get(i);
				return (bId != -1);
			}
		}
		
		return isBlinkableExpression(CharacterManager.EXPRESSION_NORMAL);
	}
}
