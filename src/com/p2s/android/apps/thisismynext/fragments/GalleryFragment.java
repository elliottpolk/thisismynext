package com.p2s.android.apps.thisismynext.fragments;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.p2s.android.apps.thisismynext.R;
import com.p2s.android.apps.thisismynext.ui.LoadingActivity;
import com.p2s.android.apps.thisismynext.ui.PostActivity;
import com.p2s.android.apps.thisismynext.util.Content;

public class GalleryFragment extends Fragment implements AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private ProgressBar mProgress;
	private Handler mHandler = new Handler();
	
	private ImageSwitcher mSwitcher;
	private List mImages;
	
	public static GalleryFragment newInstance(int index, long cId) {
		GalleryFragment f = new GalleryFragment();
		
		Bundle args = new Bundle();
        args.putInt("index", index);
        args.putLong("cId", cId);
		
        return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (container == null) return null;
        
//        new Thread(new Runnable() {
//        	public void run() {
        Content cItem = new Content(getActivity(), getArguments().getLong("cId"));
        RelativeLayout galLayout = (RelativeLayout)inflater.inflate(R.layout.detail_gallery, null);
        
        RelativeLayout actionbar = (RelativeLayout)galLayout.findViewById(R.id.actionbar_gal_include);
        actionbar.removeView((ImageView)galLayout.findViewById(R.id.actionbar_icon));
        actionbar.removeView((Button)galLayout.findViewById(R.id.actionbar_prev));
        actionbar.removeView((Button)galLayout.findViewById(R.id.actionbar_next));
        actionbar.removeView((View)galLayout.findViewById(R.id.actionbar_divider));
        
        RelativeLayout tabs = (RelativeLayout)inflater.inflate(R.layout.actionbar_tabs, null);
        actionbar.addView(tabs);
        
        ((Button)tabs.findViewById(R.id.actionbar_tab_post)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(getActivity(), PostActivity.class);
				i.putExtra("index", getArguments().getInt("index"));
				i.putExtra("cId", getArguments().getLong("cId"));
				startActivity(i);
			}
		});
        ((ImageView)tabs.findViewById(R.id.actionbar_icon)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
		        i.setClass(getActivity(), LoadingActivity.class);
				startActivity(i);
			}
		});
        
        mSwitcher = (ImageSwitcher)galLayout.findViewById(R.id.detailgallery_switcher);
        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
        
        Gallery g = (Gallery)galLayout.findViewById(R.id.detail_gallery);
        g.setAdapter(new ImageAdapter(getActivity().getApplicationContext(), mImages));
        g.setOnItemSelectedListener(this);
        
        return galLayout;
	}
	
	private Drawable fetch(String address) {
		try {
			URL url = new URL(address);
			InputStream is = (InputStream)url.getContent();
			Drawable img = Drawable.createFromStream(is, "src");
			return img;
		}catch(Exception e) {
			Log.e("TIMN", e.getMessage());
			return null;
		}
	}
	
	public class ImageAdapter extends BaseAdapter {
		private List mImages;
		private Context mContext;
		
		public ImageAdapter(Context c, List images) {
			mImages = images;
			mContext = c;
		}

		@Override
		public int getCount() {
			return mImages.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			
			i.setBackgroundDrawable((Drawable)mImages.get(position));
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			return i;
		}
		
	}

	@Override
	public View makeView() {
		ImageView i = new ImageView(getActivity());
		i.setBackgroundColor(Color.BLACK);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return i;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		mSwitcher.setBackgroundDrawable((Drawable)mImages.get(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
