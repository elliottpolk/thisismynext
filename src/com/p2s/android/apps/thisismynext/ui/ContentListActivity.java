/**
 * 
 */
package com.p2s.android.apps.thisismynext.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.p2s.android.apps.thisismynext.R;

/**
 * @author elliott.polk
 *
 */
public class ContentListActivity extends FragmentActivity {
	Bundle b;
	Intent i;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.content_list);
        
//        LayoutInflater inflator = (LayoutInflater)getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
        RelativeLayout actionbar = (RelativeLayout)this.findViewById(R.id.actionbar);
        ImageView homeBtn = (ImageView)actionbar.findViewById(R.id.actionbar_icon);
        
        Button prev = (Button)actionbar.findViewById(R.id.actionbar_prev);
        Button next = (Button)actionbar.findViewById(R.id.actionbar_next);
        
        b = getIntent().getExtras();
        i = new Intent();
        i.setClass(getApplicationContext(), LoadingActivity.class);
        
        next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				i.putExtra("URI", b.getString("next"));
				startActivity(i);
				finish();
			}
		});
        
        if(b.getString("prev") != null) {
        	homeBtn.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				startActivity(i);
    				finish();
    			}
    		});
        	
	        prev.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					i.putExtra("URI", b.getString("prev"));
					startActivity(i);
					finish();
				}
			});
        }
    }
}