package com.example.aichan2;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NewsArrayAdapter extends ArrayAdapter<NewsItem> {
	private LayoutInflater inflater;

	public NewsArrayAdapter(Context context, int resource, List<NewsItem> objects) {
		super(context, resource, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		NewsItemHoldler handler;
		NewsItem item = getItem(position);
		
		if(convertView != null) {
			view = convertView;
			handler = (NewsItemHoldler)view.getTag();
		} else {
			view = inflater.inflate(R.layout.item_newsitem, null);
			TextView titleView = (TextView)view.findViewById(R.id.newsTitleView);
			titleView.setTypeface(App.getInstance().getFont());
			TextView sourceView = (TextView)view.findViewById(R.id.newsSourceView);
			sourceView.setTypeface(App.getInstance().getFont());
			TextView linkView = (TextView)view.findViewById(R.id.newsLinkView);
			TextView dateView = (TextView)view.findViewById(R.id.newsDateView);
			dateView.setTypeface(App.getInstance().getFont());
			handler = new NewsItemHoldler(titleView, sourceView, linkView, dateView);
			
			view.setTag(handler);

		}
		
		handler.getTitleView().setText(Html.fromHtml(item.title));
		handler.getSourceView().setText(item.source);
		handler.getDateView().setText(item.pubDate);
		
		return view;
	}
}


class NewsItemHoldler {
	private TextView titleView;
	private TextView sourceView;
	private TextView linkView;
	private TextView dateView;
	
	NewsItemHoldler(TextView titleView, TextView sourceView, TextView linkView, TextView dateView) {
		this.titleView = titleView;
		this.sourceView = sourceView;
		this.linkView = linkView;
		this.dateView = dateView;
	}
	
	public TextView getTitleView() {
		return titleView;
	}
	
	public TextView getSourceView() {
		return sourceView;
	}
	
	public TextView getLinkView() {
		return linkView;
	}
	
	public TextView getDateView() {
		return dateView;
	}
}
