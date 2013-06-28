package com.example.aichan2;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

public class Weather {
	Weather.Callback callback = null;
	
	
	Weather() {
		
	}
	
	public void setCallback(Weather.Callback callback) {
		this.callback = callback;
	}
	
	public void getWeather() {
		String areaCode = loadAreaData();
		getWeather(areaCode);
	}
	
	public void getWeather(String areaCode) {
		// 地域コードからURL取得
		String url = getUrl(areaCode);
		
		// ネットにアクセスする AsyncTask の生成
		NetAccessAsyncTask task = new NetAccessAsyncTask() {		
			@Override
			protected void onPostExecute(String result) {
				if(this.isError()) {
					errorAccess(result);
				} else {
					finishAccess(result);
				}
			}
		};
		
		// タスクの実行
		task.execute(url);
	}
	
	
	private String loadAreaData() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplicationContext());
		return pref.getString("weatherAreaCode", "1100");
	}
	
	private String getUrl(String areaCode) {
		return "http://feeds.feedburner.com/hitokuchi_" + areaCode;
	}
	
	private void errorAccess(String message) {
		if(callback != null) {
			callback.onError(new WeatherError(message));
		}
	}
	
	private void finishAccess(String xml) {
		if(callback != null) {
			WeatherObject wObject = getWeatherObject(xml);
			callback.onFinish(wObject);
		}
	}
	
	private WeatherObject getWeatherObject(String xml) {
		String nameSpace = "http://www.weathermap.co.jp/rss/ns/forecast.dtd";
		
    	XmlPullParser parser = Xml.newPullParser();
    	
    	int nsCount = 0;

		String prefecture = null;
		String region = null;
		String city = null;
		
		List<DateWeather> dateList = new ArrayList<DateWeather>();
		List<DateWeather> weekList = new ArrayList<DateWeather>();
    	
    	try {
			parser.setInput(new StringReader(xml));
			
    	    int eventType;
    	    eventType = parser.getEventType();
    	    

    	    String term = null;
			String date = null;
			String wday = null;
			String weather = null;
			String max = null;
			String min = null;
			String prob = null;
			String prob0006 = null;
			String prob0612 = null;
			String prob1218 = null;
			String prob1824 = null;
			
			boolean kyo_asuFlg = false;
			boolean weekFlg = false;
			
			boolean weatherFlg = false;
			boolean areaFlg = false;
			boolean prefectureFlg = false;
			boolean regionFlg = false;
			boolean cityFlg = false;
			boolean maxFlg = false;
			boolean minFlg = false;
			boolean probFlg = false;
			boolean prob0006Flg = false;
			boolean prob0612Flg = false;
			boolean prob1218Flg = false;
			boolean prob1824Flg = false;
			
			boolean detailProb = false;
			
			
    	    while (eventType != XmlPullParser.END_DOCUMENT) {
    	        if(eventType == XmlPullParser.START_DOCUMENT) {
    	            
    	        } else if(eventType == XmlPullParser.END_DOCUMENT) {
    	            
    	        } else if(eventType == XmlPullParser.START_TAG) {
    	        	if(parser.getNamespace().equals(nameSpace)) {
    	        		nsCount++;
    	        	}
    	        	if(nsCount > 0) {

    	        		// <forecast> 開始タグ
    	        		if(parser.getName().equals("forecast")) {
    	        			int attrCnt = parser.getAttributeCount();
    	        			for(int i=0; i<attrCnt; i++) {
    	        				String attrName = parser.getAttributeName(i);
    	        				
    	        				// term 属性
    	        				if(attrName.equals("term")) {
    	        					term = parser.getAttributeValue(i);
    	        					if(term.equals("kyo_asu")) {
    	        						kyo_asuFlg = true;
    	        						
    	        	        			// 初期化
    	        	        			date = null;
    	        	        			weather = null;
    	        	        			max = null;
    	        	        			min = null;
    	        	        			prob = null;
    	        	        			prob0006 = null;
    	        	        			prob0612 = null;
    	        	        			prob1218 = null;
    	        	        			prob1824 = null;
    	        	        			detailProb = false;
    	        	        			
    	        					} else if(term.equals("week")) {
    	        						weekFlg = true;
    	        					}
    	        				}
    	        			}

        	        	// <forecast term="week"> 内の <content> 開始タグ
    	        		} else if(weekFlg && parser.getName().equals("content")) {
    	        			
    	        			// 初期化
    	        			date = null;
    	        			weather = null;
    	        			max = null;
    	        			min = null;
    	        			prob = null;
    	        			prob0006 = null;
    	        			prob0612 = null;
    	        			prob1218 = null;
    	        			prob1824 = null;
    	        			detailProb = false;
    	        			
    	        			// 属性取得
    	        			int attrCnt = parser.getAttributeCount();
    	        			for(int i=0; i<attrCnt; i++) {
    	        				String attrName = parser.getAttributeName(i);
    	        				
    	        				// date 属性
    	        				if(attrName.equals("date")) {
    	        					date = parser.getAttributeValue(i);
    	        					
    	        				// wday 属性
    	        				} else if(attrName.equals("wday")) {
    	        					wday = parser.getAttributeValue(i);
    	        				}
    	        			}
    	        		} else if(parser.getName().equals("content")) {
    	        			int attrCnt = parser.getAttributeCount();
    	        			for(int i=0; i<attrCnt; i++) {
    	        				String attrName = parser.getAttributeName(i);
    	        				
    	        				// date 属性
    	        				if(attrName.equals("date")) {
    	        					date = parser.getAttributeValue(i);
    	        					
    	        				// wday 属性
    	        				} else if(attrName.equals("wday")) {
    	        					wday = parser.getAttributeValue(i);
    	        				}
    	        			}
        				
        				// <weather> 開始タグ
    	        		} else if(parser.getName().equals("weather")) {
    	        			weatherFlg = true;
    	        		
    	        		// <area> 開始タグ
    	        		} else if(parser.getName().equals("area")) {
    	        			areaFlg = true;
    	        		
    	        		// <area> 内の <prefecture> 開始タグ
    	        		} else if(areaFlg && parser.getName().equals("prefecture")) {
    	        			prefectureFlg = true;
    	        		
    	        		// <area> 内の <region> 開始タグ
    	        		} else if(areaFlg && parser.getName().equals("region")) {
    	        			regionFlg = true;
    	        		
    	        		// <area> 内の <city> 開始タグ
    	        		} else if(areaFlg && parser.getName().equals("city")) {
    	        			cityFlg = true;
    	        		
    	        		// <max> 開始タグ
    	        		} else if(parser.getName().equals("max")) {
    	        			maxFlg = true;
    	        		
    	        		// <min> 開始タグ
    	        		} else if(parser.getName().equals("min")) {
    	        			minFlg = true;
    	        		
    	        		// <prob> 開始タグ
    	        		} else if(parser.getName().equals("prob")) {
    	        			probFlg = true;
    	        			
    	        			int attrCnt = parser.getAttributeCount();
    	        			for(int i=0; i<attrCnt; i++) {
    	        				String attrName = parser.getAttributeName(i);
    	        				// hour属性
    	        				if(attrName.equals("hour")) {
    	        					String attrValue = parser.getAttributeValue(i);
    	        					if(attrValue.equals("00-06")) {
    	        						detailProb = true;
    	        						prob0006Flg = true;
    	        						//Log.d("XmlPullParserSample", "prob0006Flg");
    	        					} else if(attrValue.equals("06-12")) {
    	        						detailProb = true;
    	        						prob0612Flg = true;
    	        						//Log.d("XmlPullParserSample", "prob0612Flg");
    	        					} else if(attrValue.equals("12-18")) {
    	        						detailProb = true;
    	        						prob1218Flg = true;
    	        						//Log.d("XmlPullParserSample", "prob1218Flg");
    	        					} else if(attrValue.equals("18-24")) {
    	        						detailProb = true;
    	        						prob1824Flg = true;
    	        						//Log.d("XmlPullParserSample", "prob1824Flg");
    	        					}
    	        				}
    	        			}
    	        		
    	        		}
    	        	}
    	        	
    	        } else if(eventType == XmlPullParser.END_TAG) {
    	        	//Log.d("XmlPullParserSample", "</"+ parser.getName() +">");
    	        	if(nsCount > 0) {
//    	        		Log.d("XmlPullParserSample", "</"+ parser.getName() +">");
    	        		
    	        		
    	        		// <forecast> 終了タグ
    	        		if(parser.getName().equals("forecast")) {
	    	        		if(kyo_asuFlg) {
	    	        			
	    	        			DateWeather item = new DateWeather(date, wday, weather, max, min, prob0006, prob0612, prob1218, prob1824);
	    	        			dateList.add(item);
	    	        			
	    						kyo_asuFlg = false;
	    					} else if(weekFlg) {
	    						weekFlg = false;
	    					}
    	        		
        	        	// <weather> 終了タグ
    	        		} else if(parser.getName().equals("weather")) {
    	        			weatherFlg = false;
    	        		
    	        		// <area> 終了タグ
    	        		} else if(parser.getName().equals("area")) {
    	        			areaFlg = false;
    	        		
    	        		// <area> 内の <prefecture> 終了タグ
    	        		} else if(parser.getName().equals("prefecture")) {
    	        			prefectureFlg = false;
    	        		
    	        		// <area> 内の <region> 終了タグ
    	        		} else if(parser.getName().equals("region")) {
    	        			regionFlg = false;
    	        		
    	        		// <area> 内の <city> 終了タグ
    	        		} else if(parser.getName().equals("city")) {
    	        			cityFlg = false;
    	        		
    	        		// <max> 終了タグ
    	        		} else if(parser.getName().equals("max")) {
    	        			maxFlg = false;
    	        		
    	        		// <min> 終了タグ
    	        		} else if(parser.getName().equals("min")) {
    	        			minFlg = false;
    	        			
        	        	// <prob> 終了タグ
    	        		} else if(parser.getName().equals("prob")) {
    	        			probFlg = false;
    	        			prob0006Flg = false;
    	        			prob0612Flg = false;
    	        			prob1218Flg = false;
    	        			prob1824Flg = false;

        	        	// <forecast term="week"> 内の <content> 終了タグ
    	        		} else if(weekFlg && parser.getName().equals("content")) {
    	        			DateWeather item;
    	        			
    	        			if(detailProb) {
        	        			item = new DateWeather(date, wday, weather, max, min
        	        					, prob0006, prob0612, prob1218, prob1824);
    	        			} else {
    	        				item = new DateWeather(date, wday, weather, max, min, prob);
    	        			}
    	        			
    	        			weekList.add(item);
    	        		}
    	        	}
    	        	if(parser.getNamespace().equals(nameSpace)) {
    	        		nsCount--;
    	        	}
    	        	
    	        } else if(eventType == XmlPullParser.TEXT) {
    	        	//Log.d("XmlPullParserSample", "text:"+ parser.getText());
    	        	if(nsCount > 0) {
    	        		String text = parser.getText();
    	        		
    	        		// <weather>内
    	        		if(weatherFlg) {
    	        			weather = parser.getText();
    	        			weatherFlg = false;
    	        		
    	        		// <area>内
    	        		} else if(areaFlg) {
    	        			if(prefectureFlg) {
    	        				prefecture = text;
    	        			} else if(regionFlg) {
    	        				region = text;
    	        			} else if(cityFlg) {
    	        				city = text;
    	        			}
    	        		
    	        		// <max>内
    	        		} else if(maxFlg) {
    	        			max = text;
    	        		
    	        		// <min>内
    	        		} else if(minFlg) {
    	        			min = text;
    	        		
    	        		// <prob>内
    	        		} else if(probFlg) {
    	        			if(prob0006Flg) {
    	        				prob0006 = text;
    	        			} else if(prob0612Flg) {
    	        				prob0612 = text;
    	        			} else if(prob1218Flg) {
    	        				prob1218 = text;
    	        			} else if(prob1824Flg) {
    	        				prob1824 = text;
    	        			} else {
    	        				prob = text;
    	        			}
    	        		}
    	        	}
    	        }
    	        eventType = parser.next();
    	    }
    	} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
    	}
    	
    	return new WeatherObject(prefecture, region, city, dateList, weekList);
	}
	
	
	static class Callback {
		public void onError(WeatherError error){}
		public void onFinish(WeatherObject wObject){}
	}
}


class WeatherError {
	String message;
	
	WeatherError(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
