/**
 * 
 */
package com.p2s.android.apps.thisismynext.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.p2s.android.apps.thisismynext.R;
import com.p2s.android.apps.thisismynext.util.Content;

/**
 * @author elliott.polk
 *
 */
public class PostFragment extends Fragment {
	public static PostFragment newInstance(int index, long cId) {
        PostFragment f = new PostFragment();

        // Supply index input as an argument.
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
        
//        Button prev = (Button)postLayout.findViewById(R.id.actionbar_prev);
//        Button next = (Button)postLayout.findViewById(R.id.actionbar_next);
        
        TextView titleView = (TextView)postLayout.findViewById(R.id.detailitem_post_title);
        titleView.setText(cItem.getTitle());
        
        TextView pubInfoView = (TextView)postLayout.findViewById(R.id.detailitem_pubinfo);
        pubInfoView.setText("by "+cItem.getAuthor()+cItem.getPostedAt());
        
        TextView postView = (TextView)postLayout.findViewById(R.id.detailitem_post);
        postView.setText(Html.fromHtml(cItem.getPost()));
        
        return postLayout;
    }
}
