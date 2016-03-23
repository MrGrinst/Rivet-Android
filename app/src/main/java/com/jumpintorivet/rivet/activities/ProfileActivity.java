package com.jumpintorivet.rivet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.menu.my_conversations.MyConversationListActivity;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.models.UserProfile;
import com.jumpintorivet.rivet.repositories.RequestManager;
import com.jumpintorivet.rivet.repositories.RivetCallback;
import com.jumpintorivet.rivet.repositories.UserRepository;
import com.jumpintorivet.rivet.utils.AppState;
import com.jumpintorivet.rivet.utils.EventTrackingUtil;

import java.text.DecimalFormat;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Response;
import rx.functions.Action1;

public class ProfileActivity extends Activity {
    private static final String CALLBACK_TAG_PROFILE = "CALLBACK_TAG_PROFILE";

    @Bind(R.id.profile_activity_started_value)
    TextView conversationsStartedText;
    @Bind(R.id.profile_activity_time_chatting_value)
    TextView timeChattingText;
    @Bind(R.id.profile_activity_average_messages_value)
    TextView averageMessagesText;
    @Inject
    RequestManager requestManager;
    @Inject
    UserRepository userRepository;
    @Inject
    AppState appState;
    @Inject
    EventTrackingUtil eventTrackingUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ((MyApplication) getApplication()).inject(this);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appState.getUserProfile(new Action1<UserProfile>() {
            @Override
            public void call(final UserProfile userProfile) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProfileElements(userProfile);
                    }
                });
            }
        });
        loadProfile();
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestManager.cancelRequestsWithTag(CALLBACK_TAG_PROFILE);
    }

    @OnClick(R.id.profile_activity_my_conversations_row)
    public void openMyConversations() {
        startActivity(new Intent(this, MyConversationListActivity.class));
        eventTrackingUtil.selectedMyConversations();
    }

    @OnClick(R.id.profile_activity_toolbar_exit_button)
    public void closeProfileModal() {
        finish();
    }

    private void updateProfileElements(UserProfile userProfile) {
        DecimalFormat formatter = new DecimalFormat();
        formatter.setGroupingSize(3);
        formatter.setMaximumFractionDigits(0);
        conversationsStartedText.setText(userProfile.getConversationCount() + "");
        formatter.setMinimumIntegerDigits(2);
        timeChattingText.setText(((int) (userProfile.getSecondsInConversation() / 3600.0)) + ":" + formatter.format((int) ((userProfile.getSecondsInConversation() % 3600) / 60.0)) + ":" + formatter.format(userProfile.getSecondsInConversation() % 60));
        averageMessagesText.setText(userProfile.getMedianMessagesPerConversation() + "");
    }

    private void loadProfile() {
        userRepository.getUserProfile(new RivetCallback<UserProfile>() {
            @Override
            public void success(Response<UserProfile> response) {
                appState.setUserProfile(response.body());
                updateProfileElements(response.body());
            }

            @Override
            public void failure(Throwable throwable, Response response) {
            }
        }, CALLBACK_TAG_PROFILE);
    }
}
