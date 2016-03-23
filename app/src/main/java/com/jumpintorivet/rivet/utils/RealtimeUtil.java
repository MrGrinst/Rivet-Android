package com.jumpintorivet.rivet.utils;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.injection.ForApplication;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class RealtimeUtil {
    public static final String MATCH_FOUND = "MATCH_FOUND";
    public static final String MESSAGE = "MESSAGE";
    public static final String CONVERSATION_END = "CONVERSATION_END";
    public static final String CONVERSATION_UPDATED = "CONVERSATION_UPDATED";
    public static final String USER_TYPING = "client-TYPING";

    private Pusher pusher;
    private HashMap<String, Channel> subscribedChannels = new HashMap<>();

    @Inject
    public RealtimeUtil(@ForApplication MyApplication application, Bus bus) {
        HttpAuthorizer authorizer = new HttpAuthorizer(application.getResources().getString(R.string.pusher_auth_url));
        PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
        pusher = new Pusher(application.getResources().getString(R.string.pusher_api_key), options);
        connectToPusher(null);
        bus.register(this);
    }

    @Subscribe
    public void connectToPusher(NetworkConnectivityReceiver.InternetConnectionAvailableEvent event) {
        if (pusher.getConnection().getState() == ConnectionState.DISCONNECTED
                || pusher.getConnection().getState() == ConnectionState.DISCONNECTING) {
            pusher.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {
                    if (change.getPreviousState() == ConnectionState.CONNECTED
                            && change.getCurrentState() == ConnectionState.DISCONNECTED) {
                        pusher.connect();
                    }
                }

                @Override
                public void onError(String message, String code, Exception e) {
                    Timber.i(e, "Pusher connection error: " + message + ". Code: " + code);
                }
            }, ConnectionState.ALL);
        }
    }

    public void listenToChannelAndEvent(String channelName, String eventName, final RealtimeCallback realtimeCallback) {
        if (channelName != null) {
            connectToPusher(null);
            Channel channel = subscribedChannels.get(channelName);
            if (channel == null) {
                if (channelName.startsWith("private-")) {
                    channel = pusher.subscribePrivate(channelName);
                } else {
                    channel = pusher.subscribe(channelName);
                }
                subscribedChannels.put(channelName, channel);
            }
            if (channelName.startsWith("private-")) {
                channel.bind(eventName, new PrivateChannelEventListener() {
                    @Override
                    public void onAuthenticationFailure(String message, Exception e) {
                    }

                    @Override
                    public void onSubscriptionSucceeded(String channelName) {
                    }

                    @Override
                    public void onEvent(String channelName, String eventName, String data) {
                        realtimeCallback.callback(data);
                    }
                });
            } else {
                channel.bind(eventName, new SubscriptionEventListener() {
                    @Override
                    public void onEvent(String channelName, String eventName, String data) {
                        realtimeCallback.callback(data);
                    }
                });
            }
        }
    }

    public void stopListeningToChannel(String channelName) {
        if (channelName != null) {
            pusher.unsubscribe(channelName);
            subscribedChannels.remove(channelName);
        }
    }

    public void sendTypingEvent(String channelName, int myParticipantNumber) {
        if (channelName != null) {
            PrivateChannel channel = (PrivateChannel) subscribedChannels.get(channelName);
            if (channel != null) {
                try {
                    channel.trigger("client-TYPING", "{\"participant_number\":" + myParticipantNumber + "}");
                } catch (IllegalStateException e) {
                }
            }
        }
    }

    public interface RealtimeCallback {
        void callback(String data);
    }
}