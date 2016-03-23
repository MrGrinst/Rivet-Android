package com.jumpintorivet.rivet.utils;

import android.os.CountDownTimer;

import com.jumpintorivet.rivet.repositories.ConversationRepository;
import com.jumpintorivet.rivet.repositories.RivetCallback;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Response;

@Singleton
public class SearchingHeartbeatGenerator {
    private static final int HEARTBEAT_INTERVAL = (60 * 5 * 1000) - (7 * 1000);
    private ConversationRepository conversationRepository;
    private CountDownTimer timer;
    private Date lastHeartbeatSent;

    @Inject
    public SearchingHeartbeatGenerator(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public void startSendingHeartbeat() {
        if (timer == null) {
            timer = new CountDownTimer(HEARTBEAT_INTERVAL, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    sendHeartbeat();
                    if (timer != null) {
                        timer.start();
                    }
                }
            };
            timer.start();
        }
    }

    public void startSendingHeartbeatRightAway() {
        if (timer == null) {
            sendHeartbeat();
            startSendingHeartbeat();
        }
    }

    private void sendHeartbeat() {
        if (lastHeartbeatSent == null || new Date().getTime() - lastHeartbeatSent.getTime() <= HEARTBEAT_INTERVAL + 500) {
            lastHeartbeatSent = new Date();
            conversationRepository.sendHeartbeat(new RivetCallback<Void>() {
                @Override
                public void success(Response response) {
                }

                @Override
                public void failure(Throwable throwable, Response response) {
                    stopSendingHeartbeat();
                }
            }, "FAKE_TAG");
        } else {
            stopSendingHeartbeat();
        }
    }

    public void stopSendingHeartbeat() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        lastHeartbeatSent = null;
    }
}