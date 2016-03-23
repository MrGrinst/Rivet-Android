package com.jumpintorivet.rivet.repositories;

import android.os.AsyncTask;

import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.ConversationDetails;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.models.Message;
import com.jumpintorivet.rivet.models.QueueStatus;
import com.jumpintorivet.rivet.repositories.dtos.SendMessageDTO;
import com.jumpintorivet.rivet.repositories.dtos.UpdateConversationDTO;
import com.jumpintorivet.rivet.repositories.dtos.VoteDTO;
import com.jumpintorivet.rivet.repositories.internal.InternalConversationRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

@Singleton
public class ConversationRepository {
    private InternalConversationRepository internalConversationRepository;
    private RequestManager requestManager;
    private SimpleDateFormat simpleDateFormat;

    @Inject
    public ConversationRepository(InternalConversationRepository internalConversationRepository, RequestManager requestManager, SimpleDateFormat simpleDateFormat) {
        this.internalConversationRepository = internalConversationRepository;
        this.requestManager = requestManager;
        this.simpleDateFormat = simpleDateFormat;
    }

    public void getGlobalFeaturedConversationsAndCache(final RivetCallback<List<ConversationSummary>> callback, final String tag) {
        Observable<Response<List<ConversationSummary>>> observable = internalConversationRepository.getGlobalFeaturedConversations();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<List<ConversationSummary>>() {
            @Override
            public void success(Response<List<ConversationSummary>> response) {
                new ConversationCacheUpdate("FEATURED", response.body()).execute();
                if (callback != null) {
                    callback.success(response);
                }
            }

            @Override
            public void failure(Throwable throwable, Response<List<ConversationSummary>> response) {
                if (callback != null) {
                    callback.failure(throwable, response);
                }
            }
        }, tag);
    }

    public void getNearbyFeaturedConversationsAndCache(final RivetCallback<List<ConversationSummary>> callback, final String tag) {
        Observable<Response<List<ConversationSummary>>> observable = internalConversationRepository.getNearbyFeaturedConversations();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<List<ConversationSummary>>() {
            @Override
            public void success(Response<List<ConversationSummary>> response) {
                new ConversationCacheUpdate("NEARBY", response.body()).execute();
                if (callback != null) {
                    callback.success(response);
                }
            }

            @Override
            public void failure(Throwable throwable, Response<List<ConversationSummary>> response) {
                if (callback != null) {
                    callback.failure(throwable, response);
                }
            }
        }, tag);
    }

    public void getMyConversationsAndCache(final RivetCallback<List<ConversationSummary>> callback, final String tag) {
        Observable<Response<List<ConversationSummary>>> observable = internalConversationRepository.getMyConversations();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<List<ConversationSummary>>() {
            @Override
            public void success(Response<List<ConversationSummary>> response) {
                new ConversationCacheUpdate("MY_CONVERSATIONS", response.body()).execute();
                if (callback != null) {
                    callback.success(response);
                }
            }

            @Override
            public void failure(Throwable throwable, Response<List<ConversationSummary>> response) {
                if (callback != null) {
                    callback.failure(throwable, response);
                }
            }
        }, tag);
    }

    public void getQueueStatus(final RivetCallback<QueueStatus> callback, final String tag) {
        Observable<Response<QueueStatus>> observable = internalConversationRepository.getQueueStatus();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<QueueStatus>() {
            @Override
            public void success(Response<QueueStatus> response) {
                if (response.body().getConversationDetails() != null) {
                    response.body().getConversationDetails().setParentConversations(response.body().getParentConversations());
                }
                if (callback != null) {
                    callback.success(response);
                }
            }

            @Override
            public void failure(Throwable throwable, Response<QueueStatus> response) {
                if (callback != null) {
                    callback.failure(throwable, response);
                }
            }
        }, tag);
    }

    public void startSearching(final RivetCallback<QueueStatus> callback, final String tag) {
        Observable<Response<QueueStatus>> observable = internalConversationRepository.startSearching();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<QueueStatus>() {
            @Override
            public void success(Response<QueueStatus> response) {
                if (response.body().getConversationDetails() != null) {
                    response.body().getConversationDetails().setParentConversations(response.body().getParentConversations());
                }
                if (callback != null) {
                    callback.success(response);
                }
            }

            @Override
            public void failure(Throwable throwable, Response<QueueStatus> response) {
                if (callback != null) {
                    callback.failure(throwable, response);
                }
            }
        }, tag);
    }

    public void startSearchingWithParentConversationId(long parentConversationId, final RivetCallback<QueueStatus> callback, final String tag) {
        Observable<Response<QueueStatus>> observable = internalConversationRepository.startSearching(parentConversationId);
        requestManager.subscribeToRequestWithCallbackAndTag(observable, new RivetCallback<QueueStatus>() {
            @Override
            public void success(Response<QueueStatus> response) {
                if (response.body().getConversationDetails() != null) {
                    response.body().getConversationDetails().setParentConversations(response.body().getParentConversations());
                }
                if (callback != null) {
                    callback.success(response);
                }
            }

            @Override
            public void failure(Throwable throwable, Response<QueueStatus> response) {
                if (callback != null) {
                    callback.failure(throwable, response);
                }
            }
        }, tag);
    }

    public void sendHeartbeat(final RivetCallback<Void> callback, final String tag) {
        Observable<Response<Void>> observable = internalConversationRepository.sendHeartbeat();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    public void stopSearching(RivetCallback<Void> callback, final String tag) {
        Observable<Response<Void>> observable = internalConversationRepository.stopSearching();
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    public void sendMessageWithText(final long conversationId, final SendMessageDTO message, final RivetCallback<Void> callback, final String tag) {
        Observable<Response<Void>> observable = internalConversationRepository.sendMessageWithText(conversationId, message);
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    public void refreshConversation(final Conversation conversation, final RivetCallback<Void> callback, final String tag) {
        Message lastSeenMessage = conversation.getMessages().isEmpty() ? null : conversation.getMessages().get(conversation.getMessages().size() - 1);
        Date lastSeenMessageTime = lastSeenMessage == null ? new Date(0) : lastSeenMessage.getTimestamp();
        Observable<Response<ConversationDetails>> detailObservable = internalConversationRepository.getConversationDetailsById(conversation.getConversationId()).observeOn(AndroidSchedulers.mainThread());
        Observable<Response<List<Message>>> messagesObservable = internalConversationRepository.getMessagesForConversationSinceTime(conversation.getConversationId(), simpleDateFormat.format(lastSeenMessageTime)).observeOn(AndroidSchedulers.mainThread());
        Observable<Response<Void>> observable = Observable.combineLatest(detailObservable, messagesObservable, new Func2<Response<ConversationDetails>, Response<List<Message>>, Response<Void>>() {
            @Override
            public Response call(Response<ConversationDetails> conversationDetailsResponse, Response<List<Message>> listResponse) {
                conversation.updateWithConversationDetailsAndMessages(conversationDetailsResponse.body(), listResponse.body());
                return Response.success(null);
            }
        });
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    public void getWholeConversationById(final long conversationId, final RivetCallback<Conversation> callback, final String tag) {
        Observable<Response<ConversationDetails>> detailObservable = internalConversationRepository.getConversationDetailsById(conversationId).observeOn(AndroidSchedulers.mainThread());
        Observable<Response<List<Message>>> messagesObservable = internalConversationRepository.getMessagesForConversationSinceTime(conversationId, simpleDateFormat.format(new Date(0))).observeOn(AndroidSchedulers.mainThread());
        Observable<Response<Conversation>> observable = Observable.combineLatest(detailObservable, messagesObservable, new Func2<Response<ConversationDetails>, Response<List<Message>>, Response<Conversation>>() {
            @Override
            public Response<Conversation> call(Response<ConversationDetails> conversationDetailsResponse, Response<List<Message>> listResponse) {
                Conversation conversation = new Conversation(conversationDetailsResponse.body(), listResponse.body());
                return Response.success(conversation);
            }
        });
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    public void getConversationDetails(final long conversationId, final RivetCallback<ConversationDetails> callback, final String tag) {
        Observable<Response<ConversationDetails>> observable = internalConversationRepository.getConversationDetailsById(conversationId);
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    public void getMessagesForConversationSinceTime(final long conversationId, final Date time, final RivetCallback<List<Message>> callback, final String tag) {
        Observable<Response<List<Message>>> observable = internalConversationRepository.getMessagesForConversationSinceTime(conversationId, simpleDateFormat.format(time));
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    public void updateConversation(final UpdateConversationDTO updateConversationDTO, final long conversationId, final RivetCallback<Void> callback, final String tag) {
        Observable<Response<Void>> observable = internalConversationRepository.updateConversation(updateConversationDTO, conversationId);
        requestManager.subscribeToRequestWithCallbackAndTag(observable, callback, tag);
    }

    private static class ConversationCacheUpdate extends AsyncTask<Void, Void, Void> {
        private String listType;
        private List<ConversationSummary> conversations;

        public ConversationCacheUpdate(String listType, List<ConversationSummary> conversations) {
            this.listType = listType;
            this.conversations = conversations;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<ConversationSummary> allConversations = ConversationSummary.find(ConversationSummary.class, "list_type = ?", listType);
            for (ConversationSummary summary : allConversations) {
                summary.delete();
            }
            for (ConversationSummary conversationSummary : conversations) {
                conversationSummary.setListType(listType);
                conversationSummary.save();
            }
            return null;
        }
    }
}
