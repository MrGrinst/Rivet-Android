package com.jumpintorivet.rivet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import org.parceler.Parcel;

@Parcel(analyze = UserState.class, value = Parcel.Serialization.BEAN)
public class UserState extends SugarRecord<UserState> {
    @Expose
    @SerializedName("is_nearby_eligible")
    boolean isNearbyEligible;

    public boolean isNearbyEligible() {
        return isNearbyEligible;
    }

    public void setIsNearbyEligible(boolean isNearbyEligible) {
        this.isNearbyEligible = isNearbyEligible;
    }

    public static class UpdatedUserStateEvent {
        public final UserState userState;

        public UpdatedUserStateEvent(UserState userState) {
            this.userState = userState;
        }
    }
}
