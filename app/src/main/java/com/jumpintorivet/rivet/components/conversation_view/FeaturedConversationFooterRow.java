package com.jumpintorivet.rivet.components.conversation_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.start_new_conversation_box.StartNewConversationView;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.utils.AppState;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeaturedConversationFooterRow extends LinearLayout {
    @Bind(R.id.components_conversation_view_featured_conversation_footer_row_talk_about_this)
    StartNewConversationView startNewConversationView;
    @Inject
    Bus bus;
    @Inject
    AppState appState;

    public FeaturedConversationFooterRow(Context context) {
        super(context);
    }

    public FeaturedConversationFooterRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeaturedConversationFooterRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ((MyApplication) getContext().getApplicationContext()).inject(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.components_conversation_view_featured_conversation_footer_row_share)
    public void shareConversation() {
        bus.post(new OmniBar.ShowShareDialogEvent());
    }

    public void setConversation(Conversation conversation) {
        if (appState.getActiveConversation() == null && appState.getWaitForMatchChannel() == null) {
            startNewConversationView.setVisibility(VISIBLE);
            startNewConversationView.setCustomNotSearchingText(getResources().getString(R.string.talk_about_this_split_lines));
            startNewConversationView.setIsSearching(false);
        } else {
            startNewConversationView.setVisibility(GONE);
        }
    }
}
