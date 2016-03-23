package com.jumpintorivet.rivet.components.start_new_conversation_box;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.IconTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.utils.StringUtil;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartNewConversationView extends RelativeLayout {
    @Bind(R.id.components_start_new_conversation_view_progress_view)
    CircularProgressView progressView;
    @Bind(R.id.components_start_new_conversation_view_icon)
    IconTextView startOrStopText;
    @Bind(R.id.components_start_new_conversation_view_square)
    RoundedSquareView roundedSquareView;
    @Bind(R.id.components_start_new_conversation_view_bottom_label)
    LinearLayout bottomLabel;
    @Bind(R.id.components_start_new_conversation_view_bottom_label_text)
    TextView bottomLabelTextView;
    @Inject
    StringUtil stringUtil;
    @Inject
    Bus bus;

    private String customNotSearchingText;
    private boolean isSearching;

    public StartNewConversationView(Context context) {
        this(context, null);
    }

    public StartNewConversationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StartNewConversationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((MyApplication) context.getApplicationContext()).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.components_start_new_conversation_view_square)
    public void buttonTapped() {
        bus.post(new StartOrStopSearchingForConversationEvent());
    }

    public void setIsSearching(boolean isSearching) {
        this.isSearching = isSearching;
        if (startOrStopText != null) {
            if (customNotSearchingText != null) {
                bottomLabel.setVisibility(GONE);
                startOrStopText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.explanatory_text_size));
            } else {
                bottomLabel.setVisibility(VISIBLE);
                startOrStopText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.start_conversation_icon_size));
            }
            bottomLabelTextView.setText(isSearching ? stringUtil.getStringUsingResourceId(R.string.searching_for_someone_to_talk_to) : stringUtil.getStringUsingResourceId(R.string.start_conversation));
            startOrStopText.setTextColor(getResources().getColor(isSearching ? R.color.rivet_dark_blue : R.color.rivet_off_white));
            startOrStopText.setText(isSearching ? "{fa-pause}" : (customNotSearchingText == null) ? "{fa-comment}" : customNotSearchingText);
            progressView.resetAnimation();
            progressView.setVisibility(isSearching ? VISIBLE : INVISIBLE);
            roundedSquareView.setIsSearching(isSearching);
            invalidate();
        }
    }

    public void setCustomNotSearchingText(String customNotSearchingText) {
        this.customNotSearchingText = customNotSearchingText;
        setIsSearching(isSearching);
    }

    public static class StartOrStopSearchingForConversationEvent {
    }
}