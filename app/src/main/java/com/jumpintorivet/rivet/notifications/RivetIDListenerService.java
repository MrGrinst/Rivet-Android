package com.jumpintorivet.rivet.notifications;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class RivetIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, NotificationRegistrationService.class);
        startService(intent);
    }
}
