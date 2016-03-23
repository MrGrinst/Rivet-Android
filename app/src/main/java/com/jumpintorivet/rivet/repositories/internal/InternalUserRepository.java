package com.jumpintorivet.rivet.repositories.internal;

import com.jumpintorivet.rivet.models.UserProfile;
import com.jumpintorivet.rivet.models.UserState;
import com.jumpintorivet.rivet.repositories.dtos.LocationDTO;
import com.jumpintorivet.rivet.repositories.dtos.PushNotificationRegistrationDTO;
import com.jumpintorivet.rivet.repositories.dtos.UserDTO;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import rx.Observable;

public interface InternalUserRepository {
    @POST("register")
    Observable<Response<UserDTO>> registerUser(@Body LocationDTO locationDTO);

    @PUT("location")
    Observable<Response<UserState>> updateLocation(@Body LocationDTO location);

    @GET("profile")
    Observable<Response<UserProfile>> getUserProfile();

    @GET("state")
    Observable<Response<UserState>> getAppState();

    @PUT("push")
    Observable<Response<Void>> registerForPushNotifications(@Body PushNotificationRegistrationDTO dto);
}
