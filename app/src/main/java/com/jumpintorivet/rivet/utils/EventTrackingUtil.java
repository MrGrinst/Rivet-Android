package com.jumpintorivet.rivet.utils;

import com.jumpintorivet.rivet.BuildConfig;
import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.injection.ForApplication;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventTrackingUtil {
    private MixpanelAPI mixpanelAPI;
    private AppState appState;
    private boolean shouldLog;

    @Inject
    public EventTrackingUtil(@ForApplication MyApplication myApplication, AppState appState) {
        shouldLog = BuildConfig.FLAVOR.equals("production") || BuildConfig.FLAVOR.equals("staging");
        if (shouldLog) {
            this.appState = appState;
            mixpanelAPI = MixpanelAPI.getInstance(myApplication, myApplication.getResources().getString(R.string.mixpanel_token));
            if (appState.getUserId() != -1) {
                mixpanelAPI.identify(appState.getUserId() + "");
                mixpanelAPI.getPeople().identify(appState.getUserId() + "");
                mixpanelAPI.getPeople().set("Internal ID", appState.getUserId() + "");
            }
        }
    }

    public void setUserId(long userId) {
        if (shouldLog) {
            mixpanelAPI.identify(userId + "");
            mixpanelAPI.getPeople().identify(userId + "");
            mixpanelAPI.getPeople().set("Internal ID", appState.getUserId() + "");
        }
    }

    private void trackIfShouldLog(String eventName) {
        if (shouldLog) {
            mixpanelAPI.track(eventName);
        }
    }

    private void trackIfShouldLog(String eventName, Map<String, Object> map) {
        if (shouldLog) {
            mixpanelAPI.trackMap(eventName, map);
        }
    }

    public void openedApp() {
        trackIfShouldLog("Opened App");
    }

    public void selectedFeaturedConversationFromListView(long conversationId) {
        Map<String, Object> map = new HashMap<>();
        map.put("conversationId", conversationId);
        trackIfShouldLog("Selected Conversation From List View", map);
    }

    public void selectedConversationFromListView(long conversationId) {
        Map<String, Object> map = new HashMap<>();
        map.put("conversationId", conversationId);
        trackIfShouldLog("Selected Featured Conversation From List View", map);
    }

    public void slidPrivacyMaskLeftForTheFirstTime() {
        trackIfShouldLog("Slid Privacy Mask Left For The First Time");
    }

    public void openedMenu() {
        trackIfShouldLog("Opened Menu");
    }

    public void openedScoreModal() {
        trackIfShouldLog("Opened Score Modal");
    }

    public void selectedMyConversations() {
        trackIfShouldLog("Selected My Conversations");
    }

    public void featuredFilterSelected() {
        trackIfShouldLog("Featured Filter Selected");
    }

    public void nearbyFilterSelected() {
        trackIfShouldLog("Nearby Filter Selected");
    }

    public void reportButtonTapped() {
        trackIfShouldLog("Tapped Report Button");
    }

    public void shareButtonTapped() {
        trackIfShouldLog("Tapped Share Button");
    }

    public void conversationStarted() {
        trackIfShouldLog("Conversation Started");
    }

    public void conversationEnded() {
        trackIfShouldLog("Conversation Ended");
    }


    public void startedSearchingFromWaitingView() {
        trackIfShouldLog("Started Searching From Waiting View");
    }

    public void startedSearchingAfterConversationEnd() {
        trackIfShouldLog("Started Searching After Conversation End");
    }

    public void stoppedSearching() {
        trackIfShouldLog("Stopped Searching");
    }
}
