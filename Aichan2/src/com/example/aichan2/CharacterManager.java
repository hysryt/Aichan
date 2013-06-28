package com.example.aichan2;

import android.view.View;
import android.widget.ImageView;

public class CharacterManager {
	public static final int EXPRESSION_NORMAL = 0;
	public static final int EXPRESSION_THINKING = 1;
	
	private static CharacterManager instance;
	
	ImageView characterView;
	ImageView blinkView;
	
	Character character;
	
	int currentExpression = EXPRESSION_NORMAL;
	
	private CharacterManager() {
		
	}
	
	public static CharacterManager getInstance() {
		if(instance == null) {
			instance = new CharacterManager();
		}
		return instance;
	}
	
	public void setCharacter(Character character) {
		this.character = character;
		setCurrentExpression();
	}
	
	public void setCharacterView(ImageView characterView) {
		this.characterView = characterView;
		setCurrentExpression();
	}
	
	public void setBlinkView(ImageView blinkView) {
		this.blinkView = blinkView;
		setCurrentExpression();
	}
	
	private void setCurrentExpression() {
		if(character != null && characterView != null && blinkView != null) {
			int resId = character.getExpressionResourceId(currentExpression);
			characterView.setImageResource(resId);
		
			if(character.isBlinkableExpression(currentExpression)) {
				blinkView.setImageResource(character.getExpressionBlinkResourceId(currentExpression));
				
			} else {
				blinkView.setImageDrawable(null);
			}
		}
	}
	
	public void setCharacterExpression(int exp) {
		currentExpression = exp;
		
		if(characterView == null || blinkView == null) {
			return;
		}
		
		int resId = character.getExpressionResourceId(exp);
		characterView.setImageResource(resId);
		
		if(character.isBlinkableExpression(currentExpression)) {
			blinkView.setImageResource(character.getExpressionBlinkResourceId(currentExpression));
			
		} else {
			blinkView.setImageDrawable(null);
		}
	}
	
	public void releaseImageView() {
		characterView = null;
		blinkView = null;
	}
}
