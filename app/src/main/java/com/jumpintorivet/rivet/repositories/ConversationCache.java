package com.jumpintorivet.rivet.repositories;

import android.app.Activity;
import android.os.AsyncTask;

import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.utils.AsyncDatabaseCall;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.functions.Action1;

@Singleton
public class ConversationCache {

    @Inject
    public ConversationCache() {
    }

    public void getCachedGlobalFeaturedConversations(final Activity activity, final Action1<List<ConversationSummary>> callback) {
        new AsyncDatabaseCall<>(ConversationSummary.class, "list_type = 'FEATURED'", null, new Action1<List<ConversationSummary>>() {
            @Override
            public void call(final List<ConversationSummary> conversations) {
                if (callback != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.call(conversations);
                        }
                    });
                }
            }
        }).execute();
    }

    public void getCachedNearbyFeaturedConversations(final Activity activity, final Action1<List<ConversationSummary>> callback) {
        new AsyncDatabaseCall<>(ConversationSummary.class, "list_type = 'NEARBY'", null, new Action1<List<ConversationSummary>>() {
            @Override
            public void call(final List<ConversationSummary> conversations) {
                if (callback != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.call(conversations);
                        }
                    });
                }
            }
        }).execute();
    }

    public void getCachedMyConversations(final Activity activity, final Action1<List<ConversationSummary>> callback) {
        new AsyncDatabaseCall<>(ConversationSummary.class, "list_type = 'MY_CONVERSATIONS'", null, new Action1<List<ConversationSummary>>() {
            @Override
            public void call(final List<ConversationSummary> conversations) {
                if (callback != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.call(conversations);
                        }
                    });
                }
            }
        }).execute();
    }

    public void updateConversationWithMyVoteValue(final long conversationId, final int myVoteValue, final int score) {
        new UpdateCachedConversationWithMyVote(conversationId, myVoteValue, score).execute();
    }

    private static class UpdateCachedConversationWithMyVote extends AsyncTask<Void, Void, Void> {
        private long conversationId;
        private int myVoteValue;
        private int score;

        public UpdateCachedConversationWithMyVote(long conversationId, int myVoteValue, int score) {
            this.conversationId = conversationId;
            this.myVoteValue = myVoteValue;
            this.score = score;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<ConversationSummary> conversationSummaries = ConversationSummary.find(ConversationSummary.class, "conversation_id = ?", conversationId + "");
            for (ConversationSummary conversationSummary : conversationSummaries) {
                conversationSummary.setScore(score);
                conversationSummary.setMyCurrentVote(myVoteValue);
            }
            ConversationSummary.saveInTx(conversationSummaries);
            return null;
        }
    }
}
