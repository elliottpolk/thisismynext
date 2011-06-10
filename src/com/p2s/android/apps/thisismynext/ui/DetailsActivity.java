package com.p2s.android.apps.thisismynext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.p2s.android.apps.thisismynext.R;
import com.p2s.android.apps.thisismynext.fragments.PostFragment;

public class DetailsActivity extends FragmentActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detail_item);

        if(savedInstanceState == null) {
            PostFragment details = new PostFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();

	        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
	        FrameLayout fl = (FrameLayout)inflater.inflate(R.layout.detail_item, null);
	        
	        Button next = (Button)fl.findViewById(R.id.actionbar_next);
	        Button prev = (Button)fl.findViewById(R.id.actionbar_prev);
	        
	        next.setBackgroundDrawable(null);
	        next.setText("gallery");
	        next.setTextColor(Color.WHITE);
	        next.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
	        
	        prev.setBackgroundDrawable(null);
	        prev.setText("post");
	        prev.setTextColor(Color.WHITE);
	        prev.setTextSize(TypedValue.COMPLEX_UNIT_PT, 8);
        }
    }
}
