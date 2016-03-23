package com.jumpintorivet.rivet.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.jumpintorivet.rivet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoadingView extends LinearLayout {
    @Bind(R.id.components_loading_view_progress_view)
    CircularProgressView circularProgressView;
    @Bind(R.id.components_loading_view_text_view)
    TextView textView;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.components_loading_view, this);
        ButterKnife.bind(this);
        circularProgressView.startAnimation();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void doneLoading() {
        circularProgressView.resetAnimation();
        setVisibility(GONE);
    }
}
