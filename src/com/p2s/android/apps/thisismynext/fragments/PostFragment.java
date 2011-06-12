/**
 * 
 */
package com.p2s.android.apps.thisismynext.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.p2s.android.apps.thisismynext.R;
import com.p2s.android.apps.thisismynext.ui.GalleryActivity;
import com.p2s.android.apps.thisismynext.ui.LoadingActivity;
import com.p2s.android.apps.thisismynext.util.Content;

/**
 * @author elliott.polk
 *
 */
public class PostFragment extends Fragment {
	public static PostFragment newInstance(int index, long cId) {
        PostFragment f = new PostFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putLong("cId", cId);
        f.setArguments(args);

        return f;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (container == null) return null;

        
        Content cItem = new Content(getActivity(), getArguments().getLong("cId"));
        RelativeLayout postLayout = (RelativeLayout)inflater.inflate(R.layout.detail_post, null);
        
        RelativeLayout actionbar = (RelativeLayout)postLayout.findViewById(R.id.actionbar_post_include);
        actionbar.removeView((ImageView)postLayout.findViewById(R.id.actionbar_icon));
        actionbar.removeView((Button)postLayout.findViewById(R.id.actionbar_prev));
        actionbar.removeView((Button)postLayout.findViewById(R.id.actionbar_next));
        actionbar.removeView((View)postLayout.findViewById(R.id.actionbar_divider));
        
        RelativeLayout tabs = (RelativeLayout)inflater.inflate(R.layout.actionbar_tabs, null);
        tabs.removeView((View)tabs.findViewById(R.id.actionbar_tab_gallery_view));

        actionbar.addView(tabs);
        
        if(cItem.getGallery().size() > 0) {
        	((Button)tabs.findViewById(R.id.actionbar_tab_gallery)).setOnClickListener(new OnClickListener() {
        		
        		@Override
        		public void onClick(View v) {
        			Intent i = new Intent();
        			i.setClass(getActivity(), GalleryActivity.class);
        			i.putExtra("index", getArguments().getInt("index"));
        			i.putExtra("cId", getArguments().getLong("cId"));
        			startActivity(i);
        		}
        	});
        }else {
        	tabs.removeView((Button)tabs.findViewById(R.id.actionbar_tab_gallery));
        	tabs.removeView((View)tabs.findViewById(R.id.actionbar_divider));
        }
        ((ImageView)tabs.findViewById(R.id.actionbar_icon)).setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		Intent i = new Intent();
        		i.setClass(getActivity(), LoadingActivity.class);
        		startActivity(i);
        	}
        });
        
        TextView titleView = (TextView)postLayout.findViewById(R.id.detailitem_post_title);
        titleView.setText(cItem.getTitle());
        
        TextView pubInfoView = (TextView)postLayout.findViewById(R.id.detailitem_pubinfo);
        pubInfoView.setText("by "+cItem.getAuthor()+cItem.getPostedAt());
        
        TextView postView = (TextView)postLayout.findViewById(R.id.detailitem_post);
        postView.setText(Html.fromHtml(cItem.getPost()));
        
        return postLayout;
    }
}
