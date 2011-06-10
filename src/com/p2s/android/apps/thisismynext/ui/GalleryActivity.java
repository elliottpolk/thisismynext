package com.p2s.android.apps.thisismynext.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.p2s.android.apps.thisismynext.fragments.GalleryFragment;

public class GalleryActivity extends FragmentActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        if(savedInstanceState == null) {
            GalleryFragment gallery = new GalleryFragment();
            gallery.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, gallery).commit();
        }
    }
}
