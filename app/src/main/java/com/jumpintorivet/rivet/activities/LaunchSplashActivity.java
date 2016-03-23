package com.jumpintorivet.rivet.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jumpintorivet.rivet.BuildConfig;
import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.bar_on_bottom.BarOnBottomActivity;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.models.Message;
import com.jumpintorivet.rivet.models.ParentConversation;
import com.jumpintorivet.rivet.models.UserProfile;
import com.jumpintorivet.rivet.models.UserState;
import com.jumpintorivet.rivet.repositories.UserRepository;
import com.jumpintorivet.rivet.utils.AppState;
import com.jumpintorivet.rivet.utils.EventTrackingUtil;
import com.jumpintorivet.rivet.utils.LocationUtil;
import com.squareup.otto.Bus;

import org.parceler.apache.commons.lang.mutable.MutableBoolean;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class LaunchSplashActivity extends Activity {
    @Bind(R.id.splash_activity_progress_view)
    CircularProgressView progressView;
    @Inject
    Bus bus;
    @Inject
    AppState appState;
    @Inject
    UserRepository userRepository;
    @Inject
    EventTrackingUtil eventTrackingUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_splash_activity);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).inject(this);
        appState.setJustOpenedApp(true);
        progressView.startAnimation();
        handleUrlIntentIfNecessary();
    }

    private void handleUrlIntentIfNecessary() {
        if (getIntent() != null && getIntent().getData() != null && getIntent().getData().getFragment() != null) {
            String[] pathParts = getIntent().getData().getFragment().split("/");
            if (pathParts.length > 1 && pathParts[0].equals("conversation")) {
                int conversationId = Integer.parseInt(pathParts[1]);
                if (conversationId % 17 == 0) {
                    Bundle options = new Bundle();
                    options.putInt(BarOnBottomActivity.URL_INTENT_CONVERSATION_ID, conversationId / 17);
                    if (pathParts.length > 3 && pathParts[2].equals("featured")) {
                        options.putString(BarOnBottomActivity.URL_INTENT_IS_FEATURED, pathParts[3]);
                    }
                    startBarOnBottomActivity(options);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        boolean userIsLoggedIn = appState.getAuthToken() != null;
        final MutableBoolean databasesReady = new MutableBoolean(false);
        final MutableBoolean registerDone = new MutableBoolean(false);
        if (!userIsLoggedIn) {
            setupDatabases(new Runnable() {
                @Override
                public void run() {
                    databasesReady.setValue(true);
                    if (registerDone.booleanValue()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startBarOnBottomActivity(null);
                            }
                        });
                    }
                }
            });
            userRepository.registerUser(new Action1<Void>() {
                @Override
                public void call(Void object) {
                    registerDone.setValue(true);
                    if (databasesReady.booleanValue()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startBarOnBottomActivity(null);
                            }
                        });
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable error) {
                    if (error instanceof LocationUtil.LocationFetchException) {
                        new AlertDialog.Builder(LaunchSplashActivity.this)
                                .setTitle(R.string.error_updating_location)
                                .setMessage(R.string.error_getting_location_message)
                                .setPositiveButton(R.string.Gotcha, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(viewIntent);
                                    }
                                })
                                .show();
                    } else if (error instanceof LocationUtil.ConnectionErrorException) {
                        GooglePlayServicesUtil.getErrorDialog(((LocationUtil.ConnectionErrorException) error).connectionResult.getErrorCode(), LaunchSplashActivity.this, 0).show();
                    }
                }
            });
        } else {
            if (!BuildConfig.DEBUG) {
                Crashlytics.getInstance().core.setUserIdentifier(appState.getUserId() + "");
                eventTrackingUtil.openedApp();
            }
            setupDatabases(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startBarOnBottomActivity(null);
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    private void setupDatabases(final Runnable runnable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Conversation.findAll(Conversation.class);
                ConversationSummary.findAll(ConversationSummary.class);
                UserProfile.findAll(UserProfile.class);
                UserState.findAll(UserState.class);
                Message.findAll(Message.class);
                ParentConversation.findAll(ParentConversation.class);
                runnable.run();
                return null;
            }
        }.execute();
    }

    private void startBarOnBottomActivity(Bundle options) {
        Intent intent = new Intent(getApplicationContext(), BarOnBottomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivity(intent);
        finish();
    }
}
