package com.jumpintorivet.rivet.activities.conversation_making;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.jumpintorivet.rivet.R;

public class SlidPrivacyMaskTutorial extends RelativeLayout {
    public SlidPrivacyMaskTutorial(Context context) {
        this(context, null);
    }

    public SlidPrivacyMaskTutorial(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidPrivacyMaskTutorial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.conversation_making_slid_privacy_mask_tutorial_view, this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
