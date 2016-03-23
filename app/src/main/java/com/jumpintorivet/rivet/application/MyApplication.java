package com.jumpintorivet.rivet.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.jumpintorivet.rivet.BuildConfig;
import com.jumpintorivet.rivet.activities.conversation_making.ConversationMakingActivity;
import com.jumpintorivet.rivet.injection.ClassInjectionList;
import com.jumpintorivet.rivet.injection.MainModule;
import com.jumpintorivet.rivet.injection.RepositoryModule;
import com.jumpintorivet.rivet.notifications.RivetGCMListenerService;
import com.jumpintorivet.rivet.utils.CrashlyticsTree;
import com.orm.SugarApp;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

public class MyApplication extends SugarApp {
    @Inject
    Bus bus;
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(new MainModule(this), new ClassInjectionList(), new RepositoryModule());
        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashlyticsTree());
        }
        Foreground.init(this);
        inject(this);
        bus.register(this);
    }

    @Subscribe
    public void conversationStarted(ConversationMakingActivity.ConversationStartedEvent event) {
        if (Foreground.get().isBackground()) {
            RivetGCMListenerService.giveRivetNotification(this.getApplicationContext(), 21, "New Conversation", "You've been paired into a conversation", "conversation");
        } else {
            bus.post(new RivetGCMListenerService.NotificationReceivedEvent("You've been paired into a conversation"));
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}