package com.example.aichan2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.ProgressBar;


public class Talk {
	// �f�[�^�x�[�X�A�N�Z�X�p
	DatabaseOpenHelper helper;
	DatabaseAccess dbAccess;
	
	List<UserSendMessage> userSendMessageList;
	List<RegExpItem> regexpList;
	
	int prevTalkId = -1;
	int charId = 1;
	
	/*
	String[] strPats = {
		"�����?��[�͂�]"
		,"���͂悤?"
		,"����΂�[�͂�]"
		,"(��?��|���Ȃ�|�͂�)��?((��|��)��|(��|��)��)��"
		,"(��|�˂�)��?��"
		,"(��|��)�ꂽ"
		,"����"
		,"(.+)������(����)?$"
		,"(���グ��|������)"
		,"�ݒ�"
		,"(.+)��?((�H|��)��|(�H|��)��)����"
	};
	
	String[] rets = {
		"����ɂ��́A#user#"
		,"���͂悤"
		,"����΂��"
		,"���ѐH�ׂ��"
		,"�Q���"
		,"������"
		,"������#month#��#date#������"
		,"#search#,1,#word#���������܂�"
		,"�������������܍��̎C��؂�C���������̐��s�� �_���� �������H���Q�鏈�ɏZ�ޏ��M�犹�q���M���q�p�C�|�p�C�| �p�C�|�̃V���[�����K���V���[�����K���̃O�[�����_�C�O�[�����_�C�̃|���|�R�s�[�̃|���|�R�i�[�̒��v���̒���"
		,"#setting#,�ݒ��ʂ��J���܂�"
		,"#1#�I"
	};
	*/
	
	String[] non = {
		"�ȂɁH"
		,"�ց`�E�E�E"
		,"�Ӂ`��"
		,"�����"
	};
	
	//Pattern[] pats = new Pattern[strPats.length];
	
    /**
     * @param context �f�[�^�x�[�X�ڑ��Ɏg�p
     */
	Talk() {
	}
	
	public void openDB(Context context) {
		helper = new DatabaseOpenHelper(context);
		dbAccess = new DatabaseAccess(helper);
		
		// ���K�\���̓Ǎ�
		userSendMessageList = dbAccess.loadUserSendMessage();
		regexpList = dbAccess.loadRegExps();
	}
	
	public void closeDB() {
		helper.close();
	}
	
	public void reloadUserMessage() {
		userSendMessageList = dbAccess.loadUserSendMessage();
	}
	
	
	/**
	 *  ���b�Z�[�W��^���ĉ������b�Z�[�W��Ԃ�
	 *  �����@�\�ȂǁA�K�v�ł���Α��̃A�v���̋N�����s��
	 */
	public String sendMessage(Activity activity, String message) {
		DebugViewer.d("----------");
		
		DebugViewer.d("prevTalkId:"+ prevTalkId);
		
		// ���[�U�Ǝ��e�[�u������Ԏ�������
		String[] userResMessages = getUserResMessage(message);
		// TODO:���������炻���Ԃ�
		if(userResMessages != null) {
			Random rnda = new Random();
			int ra = rnda.nextInt(userResMessages.length);
			return userResMessages[ra];
		}
		
		
		// �^����ꂽ���b�Z�[�W�����v���鐳�K�\����T���Č��ʂ�Ԃ�
		RegExpResult regexpResult = getRegExpResult(message);
		
		// ��v���鐳�K�\�������������ꍇ
		if(regexpResult == null) {
			// �\���ʏ��
			CharacterManager cm = CharacterManager.getInstance();
			cm.setCharacterExpression(CharacterManager.EXPRESSION_NORMAL);
			// ������Ȃ��������p�̃��b�Z�[�W��Ԃ�
			prevTalkId = -1;
			return getNonMessage();
		}
		
		// ���K�\��ID����K�؂ȉ�b���擾
		List<TalkItem> talklist = getFilteredTalks(regexpResult.getId());
		
		// ��b����������ꍇ�͈�I��
		// ��b�����������ꍇ�͕�����Ȃ������p�̃��b�Z�[�W�����I��
		int index = 0;
		if(talklist.size() > 1) {
			// ��������ꍇ�̓����_���ň�I��
			int size = talklist.size();
			Random rnd = new Random();
			int r = rnd.nextInt(size);
			index = r;
		} else if(talklist.size() == 0) {
			// �\���ʏ��
			CharacterManager cm = CharacterManager.getInstance();
			cm.setCharacterExpression(CharacterManager.EXPRESSION_NORMAL);
			// ������Ȃ��������p�̃��b�Z�[�W��Ԃ�
			prevTalkId = -1;
			return getNonMessage();
		}
		
		// ��bID�A�Ԏ�ID�m��
		TalkItem talkitem = talklist.get(index);
		int talkId = talkitem.getTalkId();
		int resId = talkitem.getResponseId();
		DebugViewer.d("�Ԏ�ID : "+ resId);
		
		// �Ԏ�ID����Ԏ����擾
		String responseMsg = dbAccess.getResponse(resId);
		DebugViewer.d("�Ԏ� : "+ responseMsg);
		
		// �Ԏ���ResponseBehavior���擾
		ResponseBehavior responseBehavior = ResponseBehavior.createInstance(activity, regexpResult.matcher, responseMsg);
		
		// �ŏI�I�ȕԎ��̎擾
		String resMsg = responseBehavior.getResponseMessage();
		
		responseBehavior = null;
		
		prevTalkId = talkId;
		
		return resMsg;
	}
	
	/**
	 * ���[�U�Ǝ��e�[�u������Ԏ���T��
	 */
	private String[] getUserResMessage(String message) {
		for(int i=0; i<userSendMessageList.size(); i++) {
			UserSendMessage item = userSendMessageList.get(i);
			if(item.match(message)) {
				int id = item.getId();
				
				return dbAccess.getUserResMessages(id);
			}
		}
		
		return null;
	}
	
	/**
	 *  ���b�Z�[�W�����v���鐳�K�\����T���Č��ʂ�Ԃ�
	 */
	private RegExpResult getRegExpResult(String message) {
		String receivedMsg = message;

		String msg;
		// ��l�̂�#jibun#�ɕϊ�
		msg = replaceFirstPerson(message);
		// ��l�̂�#aite#�ɕϊ�
		msg = replaceSecondPerson(msg);
		RegExpResult regexpResult = matchRegExp(msg);
		
		// priority0 �͕ϊ����ꂽ1�l�́A2�l�̂ɔ������Ȃ�
		if(regexpResult == null || regexpResult.getPriority() == 0) {
			if(regexpResult != null && regexpResult.getPriority() == 0) {
				DebugViewer.d("priority:0�̂��ߖ���");
			}
			// �ϊ��O�̃��b�Z�[�W�Ő��K�\�����r�����ʂ����߂�	
			regexpResult = matchRegExp(message);
		}
		
		return regexpResult;
	}
	
	/**
	 *  ���K�\��ID����t�B���^��ʂ����œK�ȉ�b���擾
	 */
	private List<TalkItem> getFilteredTalks(int regexpId) {
		// ���K�\��ID�����b�����߂�
		List<TalkItem> talklist = dbAccess.getTalks(regexpId);

		// �K�؂ȉ�b�𒊏o
		Calendar calendar = Calendar.getInstance();
		int curHour = calendar.get(Calendar.HOUR_OF_DAY);
		talklist = filterTalkList(talklist, curHour, prevTalkId, charId);
		
		return talklist;
	}
	
	
	/**
	 *  ���Ԃɂ��t�B���^
	 */
	List<TalkItem> filterTalkList(List<TalkItem> talklist, int currentHour, int prevTalkId, int charId) {
		DebugViewer.d("  beforeFilter : "+ talklist.size());
		return filterByTime(filterByPrevTalk(filterByCharacter(talklist, charId), prevTalkId), currentHour);
	}
	
	List<TalkItem> filterByTime(List<TalkItem> talklist,int currentHour) {
		List<TalkItem> result = new ArrayList<TalkItem>();
		
		for(int i=0; i<talklist.size(); i++) {
			TalkItem item = talklist.get(i);
			if(item.isMatchTime(currentHour)) {
				result.add(item);
			}
		}

		DebugViewer.d("    timefilter : "+ result.size());
		
		return result;
	}
	
	
	/**
	 *  �O��bID�ɂ��t�B���^
	 */
	List<TalkItem> filterByPrevTalk(List<TalkItem> talklist,int prevTalkId) {
		List<TalkItem> result = new ArrayList<TalkItem>();
		boolean existPrevTalk = false;
		
		for(int i=0; i<talklist.size(); i++) {
			TalkItem item = talklist.get(i);
			if(item.isMatchPrevTalk(prevTalkId)) {
				if(result.size() == 0) {	// �ŏ��̂�̑O��bID������ꍇ��NULL�̂��͎̂擾���Ȃ�
					if(item.getPrevTalkId() == -1) {
						existPrevTalk = false;
					} else {
						existPrevTalk = true;
					}
					result.add(item);
				} else {
					if((item.getPrevTalkId() == -1 && !existPrevTalk)
							|| (item.getPrevTalkId() != -1 && existPrevTalk)) {
						result.add(item);
					}
				}
			}
		}

		DebugViewer.d("    prevTalkfilter : "+ result.size());
		
		return result;
	}
	
	/**
	 *  �L�����N�^ID�ɂ��t�B���^
	 */
	List<TalkItem> filterByCharacter(List<TalkItem> talklist,int charId) {
		List<TalkItem> result = new ArrayList<TalkItem>();
		boolean existCharacterId = false;
		
		for(int i=0; i<talklist.size(); i++) {
			TalkItem item = talklist.get(i);
			if(item.isMatchCharacter(charId)) {
				if(result.size() == 0) {
					if(item.getCharacterId() == 0) {
						existCharacterId = false;
					} else {
						existCharacterId = true;
					}
					result.add(item);
				} else {
					if(item.getCharacterId() == 0 && !existCharacterId
							|| item.getCharacterId() != 0 && existCharacterId) {
						result.add(item);
					}
				}
			}
		}

		
		DebugViewer.d("    characterfilter : "+ result.size());
		
		return result;
	}
	
	
	private String getNonMessage() {
        Random rnd = new Random();
        int r = rnd.nextInt(non.length);
		return non[r];
	}
	
	
	/**
	 *  �S�Ă̐��K�\���Ɣ�r���Ĉ�ԍŏ��Ɉ�v�������ʂ�Ԃ�
	 */
	private RegExpResult matchRegExp(String text) {
		DebugViewer.d(text);
		
		for(int i=0; i<regexpList.size(); i++) {
			RegExpItem item = regexpList.get(i);
			RegExpResult result = item.match(text);
			if(result != null) {
				
				DebugViewer.d("���K�\��ID : "+ result.getId());
				DebugViewer.d("���K�\�� : "+ result.item.regexp.toString());
				DebugViewer.d("priority : "+ result.getPriority());
				
				return result;
			}
		}
		
		DebugViewer.d("�Y���Ȃ�");
		return null;
	}
	
	
	/**
	 *  ��l�̂� #jibun# �ɕϊ�
	 */
	private String replaceFirstPerson(String msg) {
		String userName = App.getInstance().getUserName();
		String result = msg.replaceAll(userName +"|��|����|�I��|�l|�ڂ�|�{�N|��|�킽��|���^�V", "#jibun#");
		return result;
	}
	
	/**
	 *  ��l�̂� #aite# �ɕϊ�
	 */
	private String replaceSecondPerson(String msg) {
		String result = msg.replaceAll(MainActivity.AI_NAME +"|���Ȃ�|�A�i�^|�N|����|�L�~|���O|���܂�|�I�}�G|�M�l|������|�L�T�}|����", "#aite#");
		return result;
	}
}


class AAA extends ProgressBar {

	public AAA(Context context) {
		super(context);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}
}
