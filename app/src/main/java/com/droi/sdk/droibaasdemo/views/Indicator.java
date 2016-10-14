package com.droi.sdk.droibaasdemo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.utils.UnitUtil;

public class Indicator extends LinearLayout {
    private int mCount;

    public Indicator(Context context) {
        super(context);
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCount(int count) {
        mCount = count;
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT
                , LayoutParams.WRAP_CONTENT);
        lp.setMargins(UnitUtil.dpToPx(getContext(), 3), 0, 0, 0);
        this.removeAllViews();
        for (int i = 0; i < mCount; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(R.drawable.ic_indicator);
            this.addView(imageView, lp);
        }
    }

    public void select(int position) {
        for (int i = 0; i < mCount; i++) {
            this.getChildAt(i).setSelected(false);
        }
        if (this.getChildAt(position) != null) {
            this.getChildAt(position).setSelected(true);
        }
    }
}
