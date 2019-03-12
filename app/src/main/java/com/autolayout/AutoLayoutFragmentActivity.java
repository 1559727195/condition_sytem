package com.chenhongxin.autolayout;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by zhy on 15/11/19.
 */
public class AutoLayoutFragmentActivity extends FragmentActivity {

	private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
	private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
	private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View view = null;
		if (name.equals(LAYOUT_FRAMELAYOUT)) {
			view = new com.chenhongxin.autolayout.AutoFrameLayout(context, attrs);
		}

		if (name.equals(LAYOUT_LINEARLAYOUT)) {
			view = new com.chenhongxin.autolayout.AutoLinearLayout(context, attrs);
		}

		if (name.equals(LAYOUT_RELATIVELAYOUT)) {
			view = new com.chenhongxin.autolayout.AutoRelativeLayout(context, attrs);
		}

		if (view != null)
			return view;

		return super.onCreateView(name, context, attrs);
	}

}
