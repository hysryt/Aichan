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
		// �n��R�[�h����URL�擾
		String url = getUrl(areaCode);
		
		// �l�b�g�ɃA�N�Z�X���� AsyncTask �̐���
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
		
		// �^�X�N�̎��s
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

    	        		// <forecast> �J�n�^�O
    	        		if(parser.getName().equals("forecast")) {
    	        			int attrCnt = parser.getAttributeCount();
    	        			for(int i=0; i<attrCnt; i++) {
    	        				String attrName = parser.getAttributeName(i);
    	        				
    	        				// term ����
    	        				if(attrName.equals("term")) {
    	        					term = parser.getAttributeValue(i);
    	        					if(term.equals("kyo_asu")) {
    	        						kyo_asuFlg = true;
    	        						
    	        	        			// ������
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

        	        	// <forecast term="week"> ���� <content> �J�n�^�O
    	        		} else if(weekFlg && parser.getName().equals("content")) {
    	        			
    	        			// ������
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
    	        			
    	        			// �����擾
    	        			int attrCnt = parser.getAttributeCount();
    	        			for(int i=0; i<attrCnt; i++) {
    	        				String attrName = parser.getAttributeName(i);
    	        				
    	        				// date ����
    	        				if(attrName.equals("date")) {
    	        					date = parser.getAttributeValue(i);
    	        					
    	        				// wday ����
    	        				} else if(attrName.equals("wday")) {
    	        					wday = parser.getAttributeValue(i);
    	        				}
    	        			}
    	        		} else if(parser.getName().equals("content")) {
    	        			int attrCnt = parser.getAttributeCount();
    	        			for(int i=0; i<attrCnt; i++) {
    	        				String attrName = parser.getAttributeName(i);
    	        				
    	        				// date ����
    	        				if(attrName.equals("date")) {
    	        					date = parser.getAttributeValue(i);
    	        					
    	        				// wday ����
    	        				} else if(attrName.equals("wday")) {
    	        					wday = parser.getAttributeValue(i);
    	        				}
    	        			}
        				
        				// <weather> �J�n�^�O
    	        		} else if(parser.getName().equals("weather")) {
    	        			weatherFlg = true;
    	        		
    	        		// <area> �J�n�^�O
    	        		} else if(parser.getName().equals("area")) {
    	        			areaFlg = true;
    	        		
    	        		// <area> ���� <prefecture> �J�n�^�O
    	        		} else if(areaFlg && parser.getName().equals("prefecture")) {
    	        			prefectureFlg = true;
    	        		
    	        		// <area> ���� <region> �J�n�^�O
    	        		} else if(areaFlg && parser.getName().equals("region")) {
    	        			regionFlg = true;
    	        		
    	        		// <area> ���� <city> �J�n�^�O
    	        		} else if(areaFlg && parser.getName().equals("city")) {
    	        			cityFlg = true;
    	        		
    	        		// <max> �J�n�^�O
    	        		} else if(parser.getName().equals("max")) {
    	        			maxFlg = true;
    	        		
    	        		// <min> �J�n�^�O
    	        		} else if(parser.getName().equals("min")) {
    	        			minFlg = true;
    	        		
    	        		// <prob> �J�n�^�O
    	        		} else if(parser.getName().equals("prob")) {
    	        			probFlg = true;
    	        			
    	        			int attrCnt = parser.getAttributeCount();
    	        			for(int i=0; i<attrCnt; i++) {
    	        				String attrName = parser.getAttributeName(i);
    	        				// hour����
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
    	        		
    	        		
    	        		// <forecast> �I���^�O
    	        		if(parser.getName().equals("forecast")) {
	    	        		if(kyo_asuFlg) {
	    	        			
	    	        			DateWeather item = new DateWeather(date, wday, weather, max, min, prob0006, prob0612, prob1218, prob1824);
	    	        			dateList.add(item);
	    	        			
	    						kyo_asuFlg = false;
	    					} else if(weekFlg) {
	    						weekFlg = false;
	    					}
    	        		
        	        	// <weather> �I���^�O
    	        		} else if(parser.getName().equals("weather")) {
    	        			weatherFlg = false;
    	        		
    	        		// <area> �I���^�O
    	        		} else if(parser.getName().equals("area")) {
    	        			areaFlg = false;
    	        		
    	        		// <area> ���� <prefecture> �I���^�O
    	        		} else if(parser.getName().equals("prefecture")) {
    	        			prefectureFlg = false;
    	        		
    	        		// <area> ���� <region> �I���^�O
    	        		} else if(parser.getName().equals("region")) {
    	        			regionFlg = false;
    	        		
    	        		// <area> ���� <city> �I���^�O
    	        		} else if(parser.getName().equals("city")) {
    	        			cityFlg = false;
    	        		
    	        		// <max> �I���^�O
    	        		} else if(parser.getName().equals("max")) {
    	        			maxFlg = false;
    	        		
    	        		// <min> �I���^�O
    	        		} else if(parser.getName().equals("min")) {
    	        			minFlg = false;
    	        			
        	        	// <prob> �I���^�O
    	        		} else if(parser.getName().equals("prob")) {
    	        			probFlg = false;
    	        			prob0006Flg = false;
    	        			prob0612Flg = false;
    	        			prob1218Flg = false;
    	        			prob1824Flg = false;

        	        	// <forecast term="week"> ���� <content> �I���^�O
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
    	        		
    	        		// <weather>��
    	        		if(weatherFlg) {
    	        			weather = parser.getText();
    	        			weatherFlg = false;
    	        		
    	        		// <area>��
    	        		} else if(areaFlg) {
    	        			if(prefectureFlg) {
    	        				prefecture = text;
    	        			} else if(regionFlg) {
    	        				region = text;
    	        			} else if(cityFlg) {
    	        				city = text;
    	        			}
    	        		
    	        		// <max>��
    	        		} else if(maxFlg) {
    	        			max = text;
    	        		
    	        		// <min>��
    	        		} else if(minFlg) {
    	        			min = text;
    	        		
    	        		// <prob>��
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
