package com.jumpintorivet.rivet.repositories;

import com.jumpintorivet.rivet.repositories.dtos.ReportBehaviorDTO;
import com.jumpintorivet.rivet.repositories.internal.InternalReportBehaviorRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Response;
import rx.Observable;

@Singleton
public class ReportBehaviorRepository {
    private InternalReportBehaviorRepository internalReportBehaviorRepository;
    private RequestManager requestManager;

    @Inject
    public ReportBehaviorRepository(InternalReportBehaviorRepository internalReportBehaviorRepository, RequestManager requestManager) {
        this.requestManager = requestManager;
        this.internalReportBehaviorRepository = internalReportBehaviorRepository;
    }

    public void reportConversation(final ReportBehaviorDTO reportBehaviorDTO, final long conversationId) {
        Observable<Response<Void>> observable = internalReportBehaviorRepository.reportConversation(reportBehaviorDTO, conversationId);
        requestManager.subscribeToRequestWithCallbackAndTag(observable, null, "FAKE_TAG");
    }
}
