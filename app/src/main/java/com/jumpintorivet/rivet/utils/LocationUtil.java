package com.jumpintorivet.rivet.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.injection.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.functions.Action1;
import timber.log.Timber;

@Singleton
public class LocationUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private MyApplication myApplication;
    private Action1<Location> success;
    private Action1<Exception> failure;

    @Inject
    public LocationUtil(@ForApplication MyApplication myApplication) {
        this.myApplication = myApplication;
        if (!Build.BRAND.contains("generic")) {
            googleApiClient = new GoogleApiClient.Builder(myApplication)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    public void getLocation(Action1<Location> success, Action1<Exception> failure) {
        if (success != null) {
            this.success = success;
            this.failure = failure;
            LocationManager lm = (LocationManager) myApplication.getSystemService(Context.LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (!Build.BRAND.contains("generic") && !Build.DEVICE.contains("generic")) {
                    if (!googleApiClient.isConnected()) {
                        googleApiClient.connect();
                    }
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    if (lastLocation != null) {
                        success.call(lastLocation);
                        this.success = null;
                        this.failure = null;
                    } else if (!googleApiClient.isConnecting()) {
                        failure.call(new LocationFetchException());
                        this.success = null;
                        this.failure = null;
                    }
                } else {
                    Location location = new Location("fakeprovider");
//                    location.setLatitude(33.777);
//                    location.setLongitude(-84.391);
                    location.setLatitude(34.111);
                    location.setLongitude(-84.154);
                    success.call(location);
                    this.success = null;
                    this.failure = null;
                }
            } else {
                failure.call(new LocationFetchException());
                this.success = null;
                this.failure = null;
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation(success, failure);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.i("Connection to location services suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (failure != null) {
            failure.call(new ConnectionErrorException(connectionResult));
            success = null;
            failure = null;
        }
        Timber.e("Error connecting to location services: " + connectionResult.toString());
    }

    public static class LocationFetchException extends Exception {
    }

    public static class ConnectionErrorException extends Exception {
        public ConnectionResult connectionResult;

        public ConnectionErrorException(ConnectionResult connectionResult) {
            super(connectionResult.toString());
            this.connectionResult = connectionResult;
        }
    }
}
