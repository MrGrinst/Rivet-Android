package com.jumpintorivet.rivet.repositories.dtos;

import com.google.gson.annotations.Expose;

public class ReportBehaviorDTO {
    public static final String REPORT_BEHAVIOR_TYPE_MESSAGES = "messages";
    public static final String REPORT_BEHAVIOR_TYPE_TARGETS_SOMEONE = "targets_someone";
    public static final String REPORT_BEHAVIOR_TYPE_OTHER = "other";

    @Expose
    String behavior_type;

    public ReportBehaviorDTO(String behavior_type) {
        this.behavior_type = behavior_type;
    }

    public String getBehavior_type() {
        return behavior_type;
    }

    public void setBehavior_type(String behavior_type) {
        this.behavior_type = behavior_type;
    }
}
