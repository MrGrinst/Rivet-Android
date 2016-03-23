package com.jumpintorivet.rivet.components.conversation_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.start_new_conversation_box.StartNewConversationView;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.utils.StringUtil;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationEndedListRow extends LinearLayout {
    @Inject
    StringUtil stringUtil;
    @Inject
    Bus bus;
    @Bind(R.id.components_conversation_view_conversation_ended_list_row_text)
    TextView conversationEndedTextView;
    @Bind(R.id.components_conversation_view_conversation_ended_list_row_start_conversation_view_frame)
    StartNewConversationView startNewConversationView;

    public ConversationEndedListRow(Context context) {
        this(context, null);
    }

    public ConversationEndedListRow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConversationEndedListRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((MyApplication) context.getApplicationContext()).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setConversation(Conversation conversation, boolean isPartnerView) {
        conversationEndedTextView.setText(getAppropriateTextForConversationEndedTextView(conversation));
        startNewConversationView.setIsSearching(false);
        startNewConversationView.setVisibility(isPartnerView ? VISIBLE : GONE);
    }

    private String getAppropriateTextForConversationEndedTextView(Conversation conversation) {
        if (conversation.didSelfParticipate()) {
            if (conversation.getMyParticipantNumber() == conversation.getEndedByParticipantNumber()) {
                return stringUtil.getStringUsingResourceId(R.string.you_ended_the_conversation);
            } else {
                return stringUtil.getStringUsingResourceId(R.string.your_partner_ended_the_conversation);
            }
        } else {
            return stringUtil.getStringUsingResourceId(R.string.one_of_the_users_ended_the_conversation);
        }

    }
}