package com.example.aichan2;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class News {
	public static final String CATEGORY_DOMESTIC = "http://headlines.yahoo.co.jp/rss/all-dom.xml";
	public static final String CATEGORY_INTERNATIONAL = "http://headlines.yahoo.co.jp/rss/all-c_int.xml";
	public static final String CATEGORY_BUSINESS = "http://headlines.yahoo.co.jp/rss/all-bus.xml";
	public static final String CATEGORY_ENTERTAINMENT = "http://headlines.yahoo.co.jp/rss/all-c_ent.xml";
	public static final String CATEGORY_SPORTS = "http://headlines.yahoo.co.jp/rss/all-c_spo.xml";
	public static final String CATEGORY_SCIENCE = "http://headlines.yahoo.co.jp/rss/all-c_sci.xml";
	public static final String CATEGORY_LIFE = "http://headlines.yahoo.co.jp/rss/all-c_life.xml";
	public static final String CATEGORY_LOCAL = "http://headlines.yahoo.co.jp/rss/all-loc.xml";
	
	private News.Callback callback = null;
	private String category;
	private boolean stopFlg = false;
	
	
	News() {
		this(null);
	}
	
	News(News.Callback callback) {
		this.callback = callback;
	}
	
	/**
	 * �R�[���o�b�N�̐ݒ�
	 * @param callback
	 */
	public void setCallback(News.Callback callback) {
		this.callback = callback;
	}
	
	
	/**
	 * �j���[�X���擾
	 * setCallback(News.Callback)�Ŏw�肵��callback�Ō��ʂ��擾����
	 * @param category
	 */
	public void getNews(String category) {
		final String mCategory;
		
		if(category == null) {
			mCategory = News.CATEGORY_DOMESTIC;
		} else {
			mCategory = category;
		}
		
		this.category = category;
		
		// �J�e�S������URL�擾
		String url = getUrl(mCategory);
		
		// �l�b�g�ɃA�N�Z�X���� AsyncTask �̐���
		NetAccessAsyncTask task = new NetAccessAsyncTask() {		
			@Override
			protected void onPostExecute(String result) {
				if(this.isError()) {
					cancelAccess(result);
				} else {
					finishAccess(result, mCategory);
				}
			}
		};
		
		// NetAccessAsyncTask �̎��s
		task.execute(url);
	}
	
	
	/**
	 *  �X�g�b�v
	 */
	public void stop() {
		stopFlg = true;
	}
	
	
	/**
	 * �l�b�g�A�N�Z�X���L�����Z�����ꂽ�Ƃ��ɌĂяo�����
	 * @param message
	 */
	private void cancelAccess(String message) {
		if(callback != null) {
			callback.onError(new NewsError(message));
		}
	}
	
	/**
	 * �l�b�g�A�N�Z�X�����������Ƃ��ɌĂяo�����
	 * @param message
	 */
	private void finishAccess(String xml, String category) {
		if(!stopFlg) {
			// xml����I�u�W�F�N�g�ɕϊ�
			List<NewsItem> list = getNewsList(xml);
			
			if(callback != null) {
				callback.onFinish(list, category);
			}
		}
	}
	
	/**
	 * �J�e�S������URL���擾
	 * @param category
	 * @return
	 */
	private String getUrl(String category) {
		return category;
	}
	
	
	/**
	 * �l�b�g����擾����xml���I�u�W�F�N�g�ɕϊ�
	 */
    private List<NewsItem> getNewsList(String xml) {
    	XmlPullParser parser = Xml.newPullParser();
    	List<NewsItem> list = new ArrayList<NewsItem>();

		SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss '+0900'", Locale.US);
    	Calendar calendar = Calendar.getInstance();
		
    	try {
			parser.setInput(new StringReader(xml));
			
			boolean itemFlg = false;
			boolean titleFlg = false;
			boolean linkFlg = false;
			boolean pubDateFlg = false;
			
			String title = null;
			String source = null;
			String link = null;
			String pubDate = null;

    	    int eventType;
    	    eventType = parser.getEventType();
    	    while (eventType != XmlPullParser.END_DOCUMENT) {
    	        if(eventType == XmlPullParser.START_DOCUMENT) {
    	            Log.d("XmlPullParserSample", "Start document");
    	        } else if(eventType == XmlPullParser.END_DOCUMENT) {
    	            Log.d("XmlPullParserSample", "End document");
    	        } else if(eventType == XmlPullParser.START_TAG) {
    	        	// �J�n�^�O
    	        	if(parser.getName().equals("item")) itemFlg = true;
    	        	if(parser.getName().equals("title")) titleFlg = true;
    	        	if(parser.getName().equals("link")) linkFlg = true;
    	        	if(parser.getName().equals("pubDate")) pubDateFlg = true;
    	        } else if(eventType == XmlPullParser.END_TAG) {
    	        	// �I���^�O
    	        	if(parser.getName().equals("item")) {
    	        		if(title != null && link != null && pubDate != null) {
    	        			NewsItem item = new NewsItem(title, source, link, pubDate);
    	        			list.add(item);
    	        			title = null;
    	        			source = null;
    	        			link = null;
    	        			pubDate = null;
    	        		}
    	        		itemFlg = false;
    	        	}
    	        	if(parser.getName().equals("title")) titleFlg = false;
    	        	if(parser.getName().equals("link")) linkFlg = false;
    	        	if(parser.getName().equals("pubDate")) pubDateFlg = false;
    	        } else if(eventType == XmlPullParser.TEXT) {
    	        	// ���e
    	        	if(itemFlg) {
    	        		if(titleFlg) {
    	        			String text = parser.getText();
    	        			Pattern p = Pattern.compile("^(.+)�i([^�i]+)�j$");
    	        			Matcher m = p.matcher(text);

    	        			if(m.find() && m.groupCount() == 2) {
    	        				title = m.group(1);
    	        				source = m.group(2);
    	        			} else {
    	        				title = text;
    	        			}
    	        		}
    	        		
    	        		if(linkFlg) link = parser.getText();
    	        		
    	        		if(pubDateFlg) {
    	        			String mDate = parser.getText();
    	        			try {
								Date d = sdf.parse(mDate);
								calendar.setTime(d);

								String mo = Util.zeroPadding(calendar.get(Calendar.MONTH), 2);
								String da = Util.zeroPadding(calendar.get(Calendar.DATE), 2);
								String ho = Util.zeroPadding(calendar.get(Calendar.HOUR_OF_DAY), 2);
								String mi = Util.zeroPadding(calendar.get(Calendar.MINUTE), 2);
								
								pubDate = mo +"/"+ da +" "+ ho +":"+ mi;
								
							} catch (ParseException e) {
								pubDate = mDate;
							}
    	        		}
    	        	}
    	        }
    	        eventType = parser.next();
    	    }
    	} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
    	return list;
    }
    
	
	/**
	 * �G���[���N�����Ƃ��A�܂��͊��������Ƃ��悤�̃R�[���o�b�N�N���X
	 */
	static interface Callback {
		public void onError(NewsError error);
		public void onFinish(List<NewsItem> list, String category);
	}
}

class NewsError {
	String message;
	
	NewsError(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
