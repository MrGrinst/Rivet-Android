package com.jumpintorivet.rivet.repositories;

import android.location.Location;

import com.jumpintorivet.rivet.models.UserProfile;
import com.jumpintorivet.rivet.models.UserState;
import com.jumpintorivet.rivet.repositories.dtos.LocationDTO;
import com.jumpintorivet.rivet.repositories.dtos.PushNotificationRegistrationDTO;
import com.jumpintorivet.rivet.repositories.dtos.UserDTO;
import com.jumpintorivet.rivet.repositories.internal.InternalUserRepository;
import com.jumpintorivet.rivet.utils.AppState;
import com.jumpintorivet.rivet.utils.AuthenticationUtil;
import com.jumpintorivet.rivet.utils.LocationUtil;
import com.jumpintorivet.rivet.utils.RivetPreferences;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Response;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

@Singleton
public class UserRepository {
    private InternalUserRepository internalUserRepository;
    private RequestManager requestManager;
    private LocationUtil locationUtil;
    private AuthenticationUtil authenticationUtil;
    private AppState appState;
    private Bus bus;

    @Inject
    public UserRepository(InternalUserRepository internalUserRepository, LocationUtil locationUtil, AuthenticationUtil authenticationUtil, RequestManager requestManager, AppState appState, Bus bus) {
        this.internalUserRepository = internalUserRepository;
        this.locationUtil = locationUtil;
        this.authenticationUtil = authenticationUtil;
        this.requestManager = requestManager;
        this.appState = appState;
        this.bus = bus;
    }

    public void registerUser(final Action1<Void> success, final Action1<Throwable> failure) {
        locationUtil.getLocation(new Action1<Location>() {
            @Override
            public void call(Location location) {
                Observable<Response<UserDTO>> observable = internalUserRepository.registerUser(new LocationDTO(location));
                requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<UserDTO>() {
                    @Override
                    public void success(Response<UserDTO> response) {
                        authenticationUtil.saveNewUser(response.body());
                        appState.setUserState(response.body().getUser_state());
                        bus.post(new UserState.UpdatedUserStateEvent(response.body().getUser_state()));
                        success.call(null);
                    }

                    @Override
                    public void failure(Throwable t, Response response) {
                        if (t != null) {
                            failure.call(t);
                        } else {
                            failure.call(new Exception("Error status code: " + response.code()));
                        }
                    }
                }, "FAKE_TAG");
            }
        }, new Action1<Exception>() {
            @Override
            public void call(Exception error) {
                failure.call(error);
            }
        });
    }

    public void updateLocation(final RivetCallback<UserState> callback, final String tag) {
        locationUtil.getLocation(new Action1<Location>() {
            @Override
            public void call(Location location) {
                Observable<Response<UserState>> observable = internalUserRepository.updateLocation(new LocationDTO(location));
                requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<UserState>() {
                    @Override
                    public void success(Response<UserState> response) {
                        appState.setUserState(response.body());
                        bus.post(new UserState.UpdatedUserStateEvent(response.body()));
                        callback.success(response);
                    }

                    @Override
                    public void failure(Throwable throwable, Response<UserState> response) {
                        callback.failure(throwable, response);
                    }
                }, tag);
            }
        }, new Action1<Exception>() {
            @Override
            public void call(Exception error) {
                Timber.e("Error getting location.");
            }
        });
    }

    public void updateAppState(final RivetCallback<UserState> callback, final String tag) {
        Observable<Response<UserState>> observable = internalUserRepository.getAppState();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<UserState>() {
            @Override
            public void success(Response<UserState> response) {
                appState.setUserState(response.body());
                bus.post(new UserState.UpdatedUserStateEvent(response.body()));
                callback.success(response);
            }

            @Override
            public void failure(Throwable throwable, Response<UserState> response) {
                callback.failure(throwable, response);
            }
        }, tag);
    }

    public void getUserProfile(final RivetCallback<UserProfile> callback, final String tag) {
        Observable<Response<UserProfile>> observable = internalUserRepository.getUserProfile();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    public void registerForPushNotifications(final PushNotificationRegistrationDTO dto, final RivetCallback<Void> callback, final String tag) {
        Observable<Response<Void>> observable = internalUserRepository.registerForPushNotifications(dto);
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }
}