package com.example.aichan2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class NewsActivity extends Activity implements News.Callback {
	Spinner categorySpinner;
	Button reloadButton;

	ProgressBar progressBar;
	TextView textView;
	ListView listView;
	ImageView imageView;
	
	LinearLayout progressLayout;
	
	LinearLayout errorLayout;
	TextView errorMessageView;
	Button retryButton;
	ImageView errorImageView;
	
	
	List<NewsItem> list = new ArrayList<NewsItem>();
	
	Task currentTask;
	String currentCategory = null;
	
	News currentNews = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_news);
        
        categorySpinner = (Spinner)findViewById(R.id.categorySpinner);
        reloadButton = (Button)findViewById(R.id.reloadButton);
        
        textView = (TextView)findViewById(R.id.errorMessageView);
        listView = (ListView)findViewById(R.id.newsLayout);
        
        progressLayout = (LinearLayout)findViewById(R.id.progressLayout);
        imageView = (ImageView)findViewById(R.id.imageView01);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        
        errorLayout = (LinearLayout)findViewById(R.id.errorLayout);
        errorMessageView = (TextView)findViewById(R.id.errorMessageView);
        retryButton = (Button)findViewById(R.id.retryButton);
        errorImageView = (ImageView)findViewById(R.id.errorImageView);

        // プログレス画面を表示
        visibleProgressLayout();
        
        // カテゴリスピナ設定
        setCategorySpinner();
        categorySpinner.setSelection(loadSelectedCategoryPosition());
        
        
        // 更新ボタンがクリックされたときの挙動を設定
        reloadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				visibleProgressLayout();
				getNews(currentCategory);
			}
        });
        
        
        // ニュースがクリックされたときの挙動を設定
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> views, View v, int pos, long lpos) {
				NewsItem item = (NewsItem)views.getItemAtPosition(pos);				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.link));
				startActivity(intent);
			}
        });
        
        
        // 再試行ボタンがクリックされたときの挙動を設定
        retryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				visibleProgressLayout();
				getNews(currentCategory);
			}
        });
        
        
        Util.setFont(findViewById(R.id.root));
    }
    
    
    private void setCategorySpinner() {
    	String[] categories = new String[] {
    			"国内"
    			, "国際"
    			, "経済"
    			, "エンターテイメント"
    			, "スポーツ"
    			, "IT・科学"
    			, "ライフ"
    			, "地域"
    	};
    	
    	
    	// フォントを変えるために微妙に変更
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
    		@Override
    		public View getView (int position, View convertView, ViewGroup parent) {
    			View view = super.getView(position, convertView, parent);
    			Util.setFont(view);
    			return view;
    		}
    		
    		@Override
    		public View getDropDownView (int position, View convertView, ViewGroup parent) {
    			View view = super.getDropDownView(position, convertView, parent);
    			Util.setFont(view);
    			return view;
    		}
    	};
    	
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	categorySpinner.setAdapter(adapter);
    	categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(currentNews != null) {
					currentNews.stop();
				}
				changeCategory(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
    	});
    }
    
    
    private void changeCategory(int categoryId) {
    	String category = null;
    	switch(categoryId) {
    		case 0: category = News.CATEGORY_DOMESTIC; break;
    		case 1: category = News.CATEGORY_INTERNATIONAL; break;
    		case 2: category = News.CATEGORY_BUSINESS; break;
    		case 3: category = News.CATEGORY_ENTERTAINMENT; break;
    		case 4: category = News.CATEGORY_SPORTS; break;
    		case 5: category = News.CATEGORY_SCIENCE; break;
    		case 6: category = News.CATEGORY_LIFE; break;
    		case 7: category = News.CATEGORY_LOCAL; break;
    	}
    	
    	if(currentCategory == null || !currentCategory.equals(category)) {
    		currentCategory = category;
    		
	    	if(currentTask != null) {
	    		currentTask.stop();
	    	}
	    	
	    	visibleProgressLayout();
	    	getNews(category);
    	}
    }
    
    
    private void getNews(String category) {
		News news = new News();
		currentNews = news;
        
        news.setCallback(this);
	        
        news.getNews(category);
    }
    
    private void visibleNewsLayout() {
    	reloadButton.setEnabled(true);
    	
    	progressLayout.setVisibility(View.GONE);
    	errorLayout.setVisibility(View.GONE);
    	listView.setVisibility(View.VISIBLE);
    }
    
    private void visibleProgressLayout() {
    	reloadButton.setEnabled(false);
    	
    	listView.setVisibility(View.GONE);
    	errorLayout.setVisibility(View.GONE);
    	progressLayout.setVisibility(View.VISIBLE);
    }
    
    private void visibleErrorLayout() {
    	reloadButton.setEnabled(false);
    	
    	listView.setVisibility(View.GONE);
    	progressLayout.setVisibility(View.GONE);
    	errorLayout.setVisibility(View.VISIBLE);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	
    	saveSelectedCategoryPosition();
    	
    	imageView.setImageDrawable(null);
    	errorImageView.setImageDrawable(null);
    }
    
    
    private int loadSelectedCategoryPosition() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		return pref.getInt("newsCategory", 0);
    }
    
    private void saveSelectedCategoryPosition() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = pref.edit();
		editor.putInt("newsCategory", categorySpinner.getSelectedItemPosition());
		editor.commit();
    }


    @Override
	public void onError(NewsError error) {
		errorMessageView.setText(error.getMessage());
		
		visibleErrorLayout();
	}
	
	@Override
	public void onFinish(List<NewsItem> list, String category) {
		if(currentCategory.equals(category)) {
	    	NewsArrayAdapter adapter = new NewsArrayAdapter(getBaseContext(), R.layout.item_newsitem, list);
	        listView.setAdapter(adapter);

	        visibleNewsLayout();
		}
	}
}


class NewsItem {
	String title;
	String source;
	String link;
	String pubDate;
	
	NewsItem(String title, String source, String link, String pubDate) {
		this.title = title;
		this.source = source;
		this.link = link;
		this.pubDate = pubDate;
	}
}


class Task {
	private boolean stop = false;
	private String tag;
	
	public boolean isStopped() {
		return stop;
	}
	
	public void setTag(String t) {
		tag = t;
	}
	
	public String getTag() {
		return tag;
	}
	
	public void stop() {
		stop = true;
	}
}
