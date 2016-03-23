package com.jumpintorivet.rivet.repositories.internal;

import com.jumpintorivet.rivet.repositories.dtos.ReportBehaviorDTO;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface InternalReportBehaviorRepository {
    @POST("conversation/{conversationId}/report")
    Observable<Response<Void>> reportConversation(@Body ReportBehaviorDTO reportBehaviorDTO, @Path("conversationId") long conversationId);
}
