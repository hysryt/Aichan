package com.example.aichan2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

public abstract class NetAccessAsyncTask extends AsyncTask<String, Object, String>  {
	boolean error = false;
	
	int mTimeout = 5000;
	String method = "GET";
	String query = null;
	
	
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
		
		if(urls.length >= 2 && (urls[1].equals("POST"))) {
			method = "POST";
		}
		
		if(urls.length >= 3 && (urls[2] != null)) {
			query = urls[2];
		}
		

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
			connection.setRequestMethod(method);
			if(method.equals("POST")) {
				connection.setDoOutput(true);
				if(query != null) {
					PrintWriter pw = new PrintWriter(connection.getOutputStream());
					pw.print(query);
					pw.close();
				}
			}
			
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