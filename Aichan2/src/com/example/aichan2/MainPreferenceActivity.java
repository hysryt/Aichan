package com.example.aichan2;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class MainPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getClass().getMethod("getFragmentManager");
        } catch (NoSuchMethodException e) {
            // API < 11
            addResourceApiLT11();
        }
    }
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_header, target);
    }
    
    
    @SuppressWarnings("deprecation")
    protected void addResourceApiLT11() {
        addPreferencesFromResource(R.xml.preference_fragment);
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TestPref extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
 
            // Load the preferences
            addPreferencesFromResource(R.xml.preference_fragment);
        }
    }
    
}
