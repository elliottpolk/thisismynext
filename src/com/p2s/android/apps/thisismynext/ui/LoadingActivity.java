package com.p2s.android.apps.thisismynext.ui;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.p2s.android.apps.thisismynext.R;
import com.p2s.android.apps.thisismynext.util.Content;
import com.p2s.android.apps.thisismynext.util.ThisIsMyNextScrapper;

public class LoadingActivity extends Activity {
	private int mStatus = 0;
	String[] navURIs;
	
	private ProgressBar mProgress;
	private Handler mHandler = new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.init_loading);
        
        TextView tv = (TextView)this.findViewById(R.id.thisismynext_text);
        SpannableString str = new SpannableString(tv.getText());
        str.setSpan(new ForegroundColorSpan(Color.WHITE), 10, tv.getText().length(), 0);
        tv.setText(str, BufferType.SPANNABLE);
        
        mProgress = (ProgressBar)findViewById(R.id.loading_bar);
        new Thread(new Runnable() {
        	public void run() {
        		String URI = getApplicationContext().getString(R.string.thisismynext_uri);

        		if(getIntent().getExtras() != null)
        			URI = getIntent().getExtras().getString("URI"); // load extras (probably just the URL)

        		mStatus = 5;
        		mHandler.post(new Runnable() {
    				public void run() {
						mProgress.setProgress(mStatus);
    				}
    			});
        		List contentItems = ThisIsMyNextScrapper.scrape(getApplicationContext(), URI);
        		
        		mStatus += 5;
        		mHandler.post(new Runnable() {
    				public void run() {
						mProgress.setProgress(mStatus);
    				}
    			});
        		navURIs = ThisIsMyNextScrapper.getNavURIs(URI);
        		
        		((Content)contentItems.get(0)).clearDB();
        		for(int i = 0; i < contentItems.size(); i++) {
        			Content tmp = ThisIsMyNextScrapper.getPost(((Content)contentItems.get(i)).getContent_URI());
        			((Content)contentItems.get(i)).setPost(tmp.getPost());
        			((Content)contentItems.get(i)).setGallery(tmp.getGallery());
        			((Content)contentItems.get(i)).saveContent();
        			
        			mStatus += 10;
        			mHandler.post(new Runnable() {
        				public void run() {
        					if(mStatus <= 100)
        						mProgress.setProgress(mStatus);
        					else{
        						Intent intent = new Intent();
        						intent.setClass(getApplicationContext(), ContentListActivity.class);
        						intent.putExtra("next", navURIs[0]);
        						intent.putExtra("prev", navURIs[1]);
        						startActivity(intent);
        						finish();
        					}
        				}
        			});
        		}
        	}
        }).start();
    }
    
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}