package com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.IconButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.base.TimeAgoTextView;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.utils.ConstUtil;
import com.jumpintorivet.rivet.utils.P;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationListRow extends LinearLayout {
    @Bind(R.id.bar_on_bottom_conversation_list_row_description_text)
    TextView descriptionText;
    @Bind(R.id.bar_on_bottom_conversation_list_row_timestamp)
    TimeAgoTextView timestampTextView;
    @Bind(R.id.bar_on_bottom_conversation_list_row_number_of_messages)
    TextView numberOfMessagesTextView;
    @Bind(R.id.bar_on_bottom_conversation_list_row_wrapper)
    LinearLayout tabWrapper;
    @Bind(R.id.bar_on_bottom_conversation_list_row_extra_info)
    LinearLayout extraInfoSection;
    @Bind(R.id.bar_on_bottom_conversation_list_row_tab)
    LinearLayout tab;
    @Bind(R.id.bar_on_bottom_conversation_list_row_headline)
    TextView headlineTextView;
    @Bind(R.id.bar_on_bottom_conversation_list_row_non_featured_extra_info)
    LinearLayout nonFeaturedExtraInfoSection;
    @Inject
    ConstUtil constUtil;

    public ConversationListRow(Context context) {
        super(context);
    }

    public ConversationListRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConversationListRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ((MyApplication) getContext().getApplicationContext()).inject(this);
        ButterKnife.bind(this);
    }

    public void setConversationSummary(ConversationSummary conversationSummary) {
        timestampTextView.setReferenceTime(conversationSummary.getEndTime().getTime());
        if (conversationSummary.getMessageCount() == 1) {
            numberOfMessagesTextView.setText(getResources().getString(R.string.number_of_message, 1));
        } else {
            numberOfMessagesTextView.setText(getResources().getString(R.string.number_of_messages, conversationSummary.getMessageCount()));
        }
        if (conversationSummary.getHeadline() != null) {
            nonFeaturedExtraInfoSection.setVisibility(GONE);
            headlineTextView.setVisibility(VISIBLE);
            headlineTextView.setText(conversationSummary.getHeadline());
            headlineTextView.setSelected(true);
            headlineTextView.setPadding(headlineTextView.getPaddingLeft(), P.convert(6), headlineTextView.getPaddingRight(), P.convert(6));
            extraInfoSection.setPadding(extraInfoSection.getPaddingLeft(), P.convert(6), extraInfoSection.getPaddingRight(), P.convert(6));
        } else {
            nonFeaturedExtraInfoSection.setVisibility(VISIBLE);
            headlineTextView.setVisibility(INVISIBLE);
        }
        if (conversationSummary.getDescription() != null) {
            descriptionText.setText(conversationSummary.getDescription());
        } else {
            descriptionText.setText(getResources().getString(R.string.last_message_sent_by_me) + conversationSummary.getLastMessageSentByMe());
        }
        updateWrapperMinHeight();
    }

    private void updateWrapperMinHeight() {
        extraInfoSection.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        tab.measure(MeasureSpec.makeMeasureSpec(constUtil.getScreenWidth() - 2 * getResources().getDimensionPixelSize(R.dimen.double_standard_margin), MeasureSpec.AT_MOST), MeasureSpec.UNSPECIFIED);
        tabWrapper.setMinimumHeight(2 * extraInfoSection.getMeasuredHeight() + tab.getMeasuredHeight());
    }
}
