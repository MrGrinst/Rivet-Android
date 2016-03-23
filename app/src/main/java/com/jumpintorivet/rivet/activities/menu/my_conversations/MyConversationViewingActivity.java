package com.jumpintorivet.rivet.activities.menu.my_conversations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing.ConversationViewingFragment;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.models.ConversationSummary;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyConversationViewingActivity extends AppCompatActivity {
    public static final String CONVERSATION = "conversation";
    private ConversationViewingFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_my_conversation_viewing_activity);
        ButterKnife.bind(this);
        ConversationSummary conversation = (ConversationSummary) getIntent().getSerializableExtra(CONVERSATION);
        fragment = ConversationViewingFragment.newInstance(conversation);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_my_conversation_viewing_main_view, fragment)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.menu_my_conversation_viewing_back_button)
    public void toolbarBackButtonTapped() {
        onBackPressed();
    }
}