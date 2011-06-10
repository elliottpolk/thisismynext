/**
 * 
 */
package com.p2s.android.apps.thisismynext.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

/**
 * @author elliott.polk
 *
 */
public class SelectableFrameLayout extends FrameLayout implements Checkable {
	private boolean mSelected;
	
	public SelectableFrameLayout(Context context) {
		super(context);
	}
	
	public SelectableFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean isChecked() {
		return mSelected;
	}

	@Override
	public void setChecked(boolean checked) {
		mSelected = checked;
		setBackgroundDrawable(checked ? new ColorDrawable(0x333) : null);
	}

	@Override
	public void toggle() {
		setChecked(!mSelected);
	}

}
