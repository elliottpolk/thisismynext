package com.p2s.android.apps.thisismynext.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.ViewSwitcher;

import com.p2s.android.apps.thisismynext.R;
import com.p2s.android.apps.thisismynext.ui.LoadingActivity;
import com.p2s.android.apps.thisismynext.ui.PostActivity;
import com.p2s.android.apps.thisismynext.util.Content;

public class GalleryFragment extends Fragment implements ViewSwitcher.ViewFactory {
	private ProgressBar mProgress;
	private Handler mHandler = new Handler();
	
	public static GalleryFragment newInstance(int index, long cId) {
		GalleryFragment f = new GalleryFragment();
		
		Bundle args = new Bundle();
        args.putInt("index", index);
        args.putLong("cId", cId);
		
        return f;
	}
	
	RelativeLayout galLayout = null;
	ProgressDialog d;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (container == null) return null;
        
        if(galLayout == null) galLayout = (RelativeLayout)inflater.inflate(R.layout.detail_gallery, null);
        
        RelativeLayout actionbar = (RelativeLayout)galLayout.findViewById(R.id.actionbar_gal_include);
        actionbar.removeView((ImageView)galLayout.findViewById(R.id.actionbar_icon));
        actionbar.removeView((Button)galLayout.findViewById(R.id.actionbar_prev));
        actionbar.removeView((Button)galLayout.findViewById(R.id.actionbar_next));
        actionbar.removeView((View)galLayout.findViewById(R.id.actionbar_divider));
        
        RelativeLayout tabs = (RelativeLayout)inflater.inflate(R.layout.actionbar_tabs, null);
        tabs.removeView((View)tabs.findViewById(R.id.actionbar_tab_post_view));
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
        
        d = new ProgressDialog(getActivity());
        d.setProgress(ProgressDialog.STYLE_SPINNER);
        d.setMessage("loadding...");
        d.setCancelable(false);
        d.show();
        
//        new Thread(new Runnable() {
//        	public void run() {
        		Content cItem = new Content(getActivity(), getArguments().getLong("cId"));
        		
		        int limit = 10;
		        if(cItem.getGallery().size() > limit) {
		        	RelativeLayout nav = (RelativeLayout)galLayout.findViewById(R.id.detail_gallery_nav);
		        	nav.removeView((Button)nav.findViewById(R.id.prev_gal));
		        }else
		        	galLayout.removeView((RelativeLayout)galLayout.findViewById(R.id.detail_gallery_nav));
		        
		        List imgs = new ArrayList();
		        for(int i = 0; i < limit ; i++)
		        	imgs.add(fetch(((ArrayList<String>)cItem.getGallery()).get(i)));
		        
		        Gallery g = (Gallery)galLayout.findViewById(R.id.detail_gallery);
		        g.setAdapter((SpinnerAdapter)new ImageAdapter(getActivity().getBaseContext(), imgs));
		        g.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
					}
		
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
		        	
				});
		        d.dismiss();
//        	}
//        }).start();
        
        return galLayout;
	}

	private Drawable fetch(String address) {
		InputStream is = null;
		try {
			URL url = new URL(address);
			is = (InputStream)url.getContent();
	        Drawable img = Drawable.createFromStream(is, "src");
	        return img;
		}catch(Exception e) {
			Log.e("TIMN", e.getMessage());
			return null;
		}finally {
			try{is.close();}catch(IOException ioe){}
		}
	}
	
	public class ImageAdapter extends BaseAdapter {
		private List imgs;
		private Context mContext;
		
		public ImageAdapter(Context c, List images) {
			imgs = images;
			mContext = c;
		}

		@Override
		public int getCount() {
			return imgs.size();
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
			Drawable d = (Drawable)imgs.get(position);
			
			i.setImageDrawable(d);
			i.setAdjustViewBounds(true);
			return i;
		}
		
	}

	@Override
	public View makeView() {
		ImageView i = new ImageView(getActivity());
		i.setBackgroundColor(Color.BLACK);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}
}
