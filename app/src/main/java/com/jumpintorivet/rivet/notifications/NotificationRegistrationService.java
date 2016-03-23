package com.jumpintorivet.rivet.notifications;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.repositories.RivetCallback;
import com.jumpintorivet.rivet.repositories.UserRepository;
import com.jumpintorivet.rivet.repositories.dtos.PushNotificationRegistrationDTO;

import javax.inject.Inject;

import retrofit.Response;
import timber.log.Timber;

public class NotificationRegistrationService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String DEVICE_TYPE = "android-new";

    @Inject
    UserRepository userRepository;

    public NotificationRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ((MyApplication) this.getApplicationContext()).inject(this);
        try {
            synchronized (TAG) {
                String token = InstanceID.getInstance(this).getToken(getString(R.string.gcm_project_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                sendTokenToServer(token);
            }
        } catch (Exception e) {
            Timber.e(e, "Failed to complete token refresh");
        }
    }

    private void sendTokenToServer(String token) {
        userRepository.registerForPushNotifications(new PushNotificationRegistrationDTO(token, DEVICE_TYPE), new RivetCallback<Void>() {
            @Override
            public void success(Response response) {
                stopSelf();
            }

            @Override
            public void failure(Throwable throwable, Response response) {
                stopSelf();
            }
        }, "FAKE_TAG");
    }
}
