package com.jumpintorivet.rivet.repositories.internal;

import com.jumpintorivet.rivet.models.ConversationDetails;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.models.Message;
import com.jumpintorivet.rivet.models.QueueStatus;
import com.jumpintorivet.rivet.repositories.dtos.SendMessageDTO;
import com.jumpintorivet.rivet.repositories.dtos.UpdateConversationDTO;
import com.jumpintorivet.rivet.repositories.dtos.VoteDTO;

import java.util.List;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface InternalConversationRepository {
    @GET("conversation/featured")
    Observable<Response<List<ConversationSummary>>> getGlobalFeaturedConversations();

    @GET("conversation/nearby")
    Observable<Response<List<ConversationSummary>>> getNearbyFeaturedConversations();

    @GET("conversation/me")
    Observable<Response<List<ConversationSummary>>> getMyConversations();


    @GET("queue")
    Observable<Response<QueueStatus>> getQueueStatus();

    @PUT("queue")
    Observable<Response<QueueStatus>> startSearching();

    @PUT("queue/{parentConversationId}")
    Observable<Response<QueueStatus>> startSearching(@Path("parentConversationId") long parentConversationId);

    @PATCH("queue")
    Observable<Response<Void>> sendHeartbeat();

    @DELETE("queue")
    Observable<Response<Void>> stopSearching();

    @POST("conversation/{conversationId}/message")
    Observable<Response<Void>> sendMessageWithText(@Path("conversationId") long conversationId, @Body SendMessageDTO message);

    @GET("conversation/{conversationId}")
    Observable<Response<ConversationDetails>> getConversationDetailsById(@Path("conversationId") long conversationId);

    @GET("conversation/{conversationId}/message")
    Observable<Response<List<Message>>> getMessagesForConversationSinceTime(@Path("conversationId") long conversationId, @Query("time") String time);

    @PATCH("conversation/{conversationId}")
    Observable<Response<Void>> updateConversation(@Body UpdateConversationDTO updateConversationDTO, @Path("conversationId") long conversationId);
}
