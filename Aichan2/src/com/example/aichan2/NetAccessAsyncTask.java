package com.example.aichan2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public abstract class NetAccessAsyncTask extends AsyncTask<String, Object, String>  {
	boolean error = false;
	
	int mTimeout = 5000;
	
	
	public boolean isError() {
		return error;
	}
	
	public void setTimeout(int milli) {
		mTimeout = milli;
	}
	
	protected String doInBackground(String... urls) {
		BufferedReader reader = null;
		String result = null;
		
		String strUrl = urls[0];
		URL url;
		HttpURLConnection connection = null;
		

		try {
			url = new URL(strUrl);
		} catch(MalformedURLException e) {
			return null;
		}

		try {
			connection = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			error = true;
			return "ê⁄ë±Ç≈Ç´Ç‹ÇπÇÒ";
		}

		try {
			connection.setRequestMethod("GET");
			
			connection.setConnectTimeout(mTimeout);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			StringBuffer sb = new StringBuffer();
			String line;
			
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
			
			result = sb.toString();
			
		} catch (IOException e) {
			error = true;
			return "ê⁄ë±Ç≈Ç´Ç‹ÇπÇÒÇ≈ÇµÇΩ";
			
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	abstract protected void onPostExecute(String result);
}