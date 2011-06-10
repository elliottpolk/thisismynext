/**
 * 
 */
package com.p2s.android.apps.thisismynext.fragments;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.p2s.android.apps.thisismynext.R;
import com.p2s.android.apps.thisismynext.ui.PostActivity;
import com.p2s.android.apps.thisismynext.util.Content;
import com.p2s.android.apps.thisismynext.util.Content.ContentDBAdaptor;

/**
 * @author elliott.polk
 *
 */
public class ContentListFragment extends ListFragment {
	boolean mDualPane;
	int mCurCheckPosition = 0;
	List content;
	
	@Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        content = fetchContent();
        setListAdapter(new ContentArrayAdapter(getActivity(),R.id.title_text,content));
    }
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
    	View riv = v.findViewById(R.id.read_indicator);
        showDetails(pos);
        if(((Content)content.get(pos)).isRead())
        	riv.setBackgroundColor(R.color.timn_black);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(int index) {
        mCurCheckPosition = index;
        
        Intent intent = new Intent();
        intent.setClass(getActivity(), PostActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("cId", Long.parseLong(((Content)content.get(index)).getId()));
        startActivity(intent);
    }
    
    private List fetchContent() {
    	List contentItems = new ArrayList<Content>();
    	Content c = new Content(getActivity());
    	ContentDBAdaptor dba = c.getContentDBAdaptor();
    	
    	dba.open();
    	
    	Cursor cur = dba.fetchAllContent();
    	Content contentObj;
    	while(cur.isAfterLast() == false) {
    		contentObj = new Content();
    		
    		contentObj.setId(cur.getString(0));
    		contentObj.setTitle(cur.getString(1));
    		contentObj.setMeta(cur.getString(2));
    		contentObj.setMeta_URI(cur.getString(3));
    		contentObj.setContent_URI(cur.getString(4));
    		contentObj.setAuthor(cur.getString(5));
    		contentObj.setPostedAt(cur.getString(6));
    		contentObj.setPost(cur.getString(7));
    		
    		contentItems.add(contentObj);
    		
    		cur.moveToNext();
    	}
    	cur.close();
    	cur = null;
    	dba.close();
    	
    	return contentItems;
    }
	
    private class ContentArrayAdapter extends ArrayAdapter {
    	Context mContext;
    	List<Content> mContent;
    	
		public ContentArrayAdapter(Context context, int textViewResourceId, List objects) {
			super(context, textViewResourceId, objects.toArray());
			this.mContext = context;
			this.mContent = (ArrayList)objects;
		}
    	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if(v == null) {
				LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.content_list_item, null);
			}
			
			Content c = mContent.get(position);
			if(c != null) {
				TextView ttv = (TextView)v.findViewById(R.id.title_text);
				if(ttv != null) ttv.setText(c.getTitle());
				
				TextView mtv = (TextView)v.findViewById(R.id.meta_text);
				if(mtv != null) mtv.setText(c.getMeta().toUpperCase());
			}
			
			return v;
		}
    }
}
