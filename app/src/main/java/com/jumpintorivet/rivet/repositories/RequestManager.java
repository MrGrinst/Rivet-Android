package com.jumpintorivet.rivet.repositories;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.LaunchSplashActivity;
import com.jumpintorivet.rivet.application.Foreground;
import com.jumpintorivet.rivet.utils.AuthenticationUtil;

import org.parceler.apache.commons.lang.exception.ExceptionUtils;
import org.parceler.apache.commons.lang.mutable.MutableBoolean;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class RequestManager {
    private Map<String, LinkedList<Subscription>> tagToSubscriptions;
    private MutableBoolean isHandlingServerConnectionError;
    private boolean handling403;
    private AuthenticationUtil authenticationUtil;

    @Inject
    public RequestManager(AuthenticationUtil authenticationUtil) {
        tagToSubscriptions = new HashMap<>();
        this.authenticationUtil = authenticationUtil;
        isHandlingServerConnectionError = new MutableBoolean(false);
    }

    public static boolean isConnectionIssue(Throwable throwable, Response response) {
        return throwable instanceof ConnectException
                || throwable instanceof SocketTimeoutException
                || throwable instanceof UnknownHostException
                || (response != null && response.code() == 503);
    }

    public <E> void subscribeToRequestWithCallbackAndTag(Observable<Response<E>> request, final RivetCallback<E> callback, String tag) {
        final Subscription subscription = request
                .retry(2)
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.computation())
                .subscribe(new Action1<Response<E>>() {
                    @Override
                    public void call(Response<E> response) {
                        if (response.isSuccess()) {
                            if (callback != null) {
                                try {
                                    callback.success(response);
                                } catch (NoSuchMethodError e) {
                                }
                            }
                        } else {
                            if (callback != null) {
                                handleError(callback, null, response);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (callback != null) {
                            handleError(callback, throwable, null);
                        }
                    }
                });
        LinkedList<Subscription> subscriptions = tagToSubscriptions.get(tag);
        if (subscriptions == null) {
            subscriptions = new LinkedList<>();
            tagToSubscriptions.put(tag, subscriptions);
        }
        subscriptions.add(subscription);
        request.doOnCompleted(new Action0() {
            @Override
            public void call() {
                subscription.unsubscribe();
            }
        });
    }

    private <T> void handleError(RivetCallback<T> callback, Throwable throwable, Response<T> response) {
        if (isConnectionIssue(throwable, response)
                && !isHandlingServerConnectionError.booleanValue()) {
            isHandlingServerConnectionError.setValue(true);
            final Activity currentActivity = Foreground.get().getCurrentActivity();
            if (currentActivity != null) {
                currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(currentActivity)
                                .setTitle(R.string.connection_error)
                                .setMessage(R.string.connection_error_message)
                                .setNegativeButton(R.string.Gotcha, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        isHandlingServerConnectionError.setValue(false);
                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        isHandlingServerConnectionError.setValue(false);
                                    }
                                }).create().show();
                    }
                });
            }
            try {
                callback.failure(throwable, response);
            } catch (NoSuchMethodError e) {
            }
        } else if (response != null && (response.code() == 403 || response.code() == 401)) {
            if (!handling403) {
                handling403 = true;
                authenticationUtil.resetCredentials();
                Activity currentActivity = Foreground.get().getCurrentActivity();
                if (currentActivity != null) {
                    Intent intent = new Intent(currentActivity.getApplicationContext(), LaunchSplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    currentActivity.startActivity(intent);
                    currentActivity.finish();
                }
                handling403 = false;
            }
        } else {
            try {
                callback.failure(throwable, response);
            } catch (NoSuchMethodError e) {
            }
        }
        if (!isConnectionIssue(throwable, response)
                && !(response != null && response.code() == 429)) {
            String stackTrace;
            String exceptionClass;
            String exceptionMessage;

            if (throwable != null) {
                stackTrace = ExceptionUtils.getFullStackTrace(throwable);
                exceptionClass = throwable.getCause() != null ? throwable.getCause().getClass().getCanonicalName() : "(null)";
                exceptionMessage = throwable.getMessage();
            } else {
                stackTrace = "";
                exceptionClass = "";
                exceptionMessage = "";
            }
            String url = response != null ? response.raw().request().urlString() : "(null)";
            String responseStatus = response != null ? response.code() + "" : "(null)";
            String httpMethod = response != null ? response.raw().request().method() : "(null)";
            Timber.e(throwable == null ? new Throwable() : throwable, "Request Error - Stack Trace: " + stackTrace + " Exception Class: '" + exceptionClass + "' Exception Message: '" + exceptionMessage + "' URL: '" + url + "' HTTP Method: '" + httpMethod + "' Response Status: " + responseStatus);
        }
    }

    public void cancelRequestsWithTag(String tag) {
        LinkedList<Subscription> subscriptions = tagToSubscriptions.get(tag);
        if (subscriptions != null) {
            while (!subscriptions.isEmpty()) {
                Subscription subscription = subscriptions.removeFirst();
                if (!subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
        }
    }
}
