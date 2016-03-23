package com.jumpintorivet.rivet.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.ProfileActivity;
import com.jumpintorivet.rivet.activities.menu.MenuActivity;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.utils.AppState;
import com.jumpintorivet.rivet.utils.EventTrackingUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("ViewConstructor")
public class OmniBar extends RelativeLayout {
    @Bind(R.id.components_omni_bar_score_button)
    ImageButton profileButton;
    @Bind(R.id.components_omni_bar_close_button)
    Button closeButton;
    @Bind(R.id.components_omni_bar_share_button)
    Button shareButton;
    @Bind(R.id.components_omni_bar_chat_button_text)
    TextView chatButtonTextView;
    @Inject
    AppState appState;
    @Inject
    Bus bus;
    @Inject
    EventTrackingUtil eventTrackingUtil;

    public OmniBar(Context context) {
        this(context, null);
    }

    public OmniBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OmniBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((MyApplication) context.getApplicationContext()).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        updateChatButton(null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bus.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bus.unregister(this);
    }

    @OnClick(R.id.components_omni_bar_menu_button)
    public void openMenu() {
        Intent menuIntent = new Intent(getContext(), MenuActivity.class);
        getContext().startActivity(menuIntent);
        eventTrackingUtil.openedMenu();
    }

    @OnClick(R.id.components_omni_bar_score_button)
    public void openScoreModal() {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        getContext().startActivity(intent);
        eventTrackingUtil.openedScoreModal();
    }

    @OnClick(R.id.components_omni_bar_share_button)
    public void shareButtonTapped() {
        bus.post(new ShowShareDialogEvent());
    }

    @OnClick(R.id.components_omni_bar_chat_button)
    public void chatButtonTapped() {
        bus.post(new ChatButtonTappedEvent());
    }

    @OnClick(R.id.components_omni_bar_close_button)
    public void closeButtonTapped() {
        bus.post(new CloseButtonTappedEvent());
    }

    @Subscribe
    public void showFeaturedConversationViewingButtons(ShowFeaturedConversationViewingButtons event) {
        closeButton.setVisibility(VISIBLE);
        shareButton.setVisibility(VISIBLE);
    }

    @Subscribe
    public void showConversationViewingButtons(ShowConversationViewingButtons event) {
        shareButton.setVisibility(VISIBLE);
        closeButton.setVisibility(VISIBLE);
    }

    @Subscribe
    public void showCloseConversationButton(ShowCloseConversationButtonEvent event) {
        closeButton.setVisibility(VISIBLE);
    }

    @Subscribe
    public void hideFeaturedConversationViewingButtons(HideFeaturedConversationViewingButtons event) {
        closeButton.setVisibility(GONE);
        shareButton.setVisibility(GONE);
    }

    @Subscribe
    public void hideConversationViewingButtons(HideConversationViewingButtons event) {
        shareButton.setVisibility(GONE);
        closeButton.setVisibility(GONE);
    }

    @Subscribe
    public void hideCloseConversationButton(HideCloseConversationButtonEvent event) {
        closeButton.setVisibility(GONE);
    }

    @Subscribe
    public void updateChatButton(UpdateChatButtonEvent event) {
        profileButton.setVisibility(VISIBLE);
        chatButtonTextView.setVisibility(GONE);
        if (appState.getActiveConversation() != null) {
            chatButtonTextView.setVisibility(VISIBLE);
            chatButtonTextView.setText(R.string.active_conversation_chat_button_text);
        } else if (event != null && event.talkAboutThis && appState.getWaitForMatchChannel() == null) {
            profileButton.setVisibility(GONE);
            shareButton.setVisibility(GONE);
            chatButtonTextView.setVisibility(VISIBLE);
            chatButtonTextView.setText(R.string.talk_about_this);
        }
    }

    public static class ShowShareDialogEvent {
    }

    public static class ShowConversationViewingButtons {
    }

    public static class HideConversationViewingButtons {
    }

    public static class ShowCloseConversationButtonEvent {
    }

    public static class ShowFeaturedConversationViewingButtons {
    }

    public static class HideFeaturedConversationViewingButtons {
    }

    public static class HideCloseConversationButtonEvent {
    }

    public static class CloseButtonTappedEvent {
    }

    public static class ChatButtonTappedEvent {
    }

    public static class UpdateChatButtonEvent {
        public final boolean talkAboutThis;

        public UpdateChatButtonEvent(boolean talkAboutThis) {
            this.talkAboutThis = talkAboutThis;
        }
    }
}
