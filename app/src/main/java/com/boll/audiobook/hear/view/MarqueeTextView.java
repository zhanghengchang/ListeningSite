package com.boll.audiobook.hear.view;

import android.content.Context;
import android.util.AttributeSet;

public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView{

	public MarqueeTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	}
}
