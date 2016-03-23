package com.jumpintorivet.rivet.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jumpintorivet.rivet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShareButton extends LinearLayout {
    @Bind(R.id.components_share_button_twitter)
    ImageButton twitterButton;
    @Bind(R.id.components_share_button_facebook)
    ImageButton facebookButton;
    @Bind(R.id.components_share_button_sms)
    ImageButton smsButton;

    public ShareButton(Context context) {
        super(context);
    }

    public ShareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShareButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        twitterButton.setOnClickListener(l);
        facebookButton.setOnClickListener(l);
        smsButton.setOnClickListener(l);
    }
}
