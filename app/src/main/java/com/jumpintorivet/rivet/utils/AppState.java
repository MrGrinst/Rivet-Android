package com.jumpintorivet.rivet.utils;

import android.os.AsyncTask;

import com.jumpintorivet.rivet.activities.conversation_making.ConversationMode;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.UserProfile;
import com.jumpintorivet.rivet.models.UserState;
import com.orm.SugarRecord;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.functions.Action1;

@Singleton
public class AppState {
    @Inject
    RivetPreferences rivetPreferences;
    private long userId;
    private String authToken;
    private ConversationMode conversationMode;
    private String waitForMatchChannel;
    private Conversation activeConversation;
    private UserProfile userProfile;
    private UserState userState;
    private Date lastListViewRefresh;
    private boolean justOpenedApp;

    @Inject
    public AppState() {
        userId = -1;
    }

    public long getUserId() {
        if (userId == -1) {
            userId = rivetPreferences.getUserId();
        }
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        rivetPreferences.setUserId(userId);
    }

    public String getAuthToken() {
        if (authToken == null) {
            authToken = rivetPreferences.getAuthToken();
        }
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        rivetPreferences.setAuthToken(authToken);
    }

    public ConversationMode getConversationMode() {
        if (conversationMode == null) {
            String mode = rivetPreferences.getConversationMode();
            if (mode != null) {
                if (mode.equals(ConversationMode.PRE_WAITING.toString())) {
                    conversationMode = ConversationMode.PRE_WAITING;
                } else if (mode.equals(ConversationMode.WAITING.toString())) {
                    conversationMode = ConversationMode.WAITING;
                } else if (mode.equals(ConversationMode.CONVERSING.toString())) {
                    conversationMode = ConversationMode.CONVERSING;
                }
            }
        }
        return conversationMode;
    }

    public void setConversationMode(ConversationMode conversationMode) {
        this.conversationMode = conversationMode;
        if (conversationMode != null) {
            rivetPreferences.setConversationMode(conversationMode.toString());
        }
    }

    public String getWaitForMatchChannel() {
        if (waitForMatchChannel == null) {
            waitForMatchChannel = rivetPreferences.getWaitForMatchChannel();
        }
        return waitForMatchChannel;
    }

    public void setWaitForMatchChannel(String waitForMatchChannel) {
        this.waitForMatchChannel = waitForMatchChannel;
        rivetPreferences.setWaitForMatchChannel(waitForMatchChannel);
    }

    public Conversation getActiveConversation() {
        if (activeConversation == null) {
            long conversationId = rivetPreferences.getActiveConversationId();
            if (conversationId != -1) {
                List<Conversation> conversations = Conversation.find(Conversation.class, "conversation_id = ?", "" + conversationId);
                if (conversations.size() > 0) {
                    activeConversation = conversations.get(0);
                } else {
                    rivetPreferences.setActiveConversationId(-1);
                }
            }
        }
        return activeConversation;
    }

    public void setActiveConversation(Conversation activeConversation) {
        if (activeConversation != null) {
            rivetPreferences.setActiveConversationId(activeConversation.getConversationId());
        } else {
            if (this.activeConversation != null && this.activeConversation.getId() != null) {
                this.activeConversation.delete();
            }
            rivetPreferences.setActiveConversationId(-1);
        }
        this.activeConversation = activeConversation;
    }

    public void saveActiveConversation() {
        new SaveActiveConversation(activeConversation).execute();
    }

    public boolean activeConversationIsNullOrInactive() {
        return getActiveConversation() == null || !getActiveConversation().isActive();
    }

    public void getUserProfile(final Action1<UserProfile> callback) {
        if (userProfile == null) {
            new GetSingularEntityAsync<>(new Action1<UserProfile>() {
                @Override
                public void call(UserProfile object) {
                    userProfile = object;
                    callback.call(object);
                }
            }, UserProfile.class).execute();
        } else {
            callback.call(userProfile);
        }
    }

    public void setUserProfile(final UserProfile newUserProfile) {
        getUserProfile(new Action1<UserProfile>() {
            @Override
            public void call(final UserProfile oldUserProfile) {
                userProfile = oldUserProfile;
                userProfile.setConversationsScore(newUserProfile.getConversationsScore());
                userProfile.setConversationCount(newUserProfile.getConversationCount());
                userProfile.setMedianMessagesPerConversation(newUserProfile.getMedianMessagesPerConversation());
                userProfile.setSecondsInConversation(newUserProfile.getSecondsInConversation());
                new SaveSingularEntityAsync(userProfile).execute();
            }
        });
    }

    public void setUserState(final UserState newUserState) {
        getUserState(new Action1<UserState>() {
            @Override
            public void call(UserState oldUserState) {
                userState = oldUserState;
                userState.setIsNearbyEligible(newUserState.isNearbyEligible());
                new SaveSingularEntityAsync(userState).execute();
            }
        });
    }

    public void getUserState(final Action1<UserState> callback) {
        if (userState == null) {
            new GetSingularEntityAsync<>(new Action1<UserState>() {
                @Override
                public void call(UserState object) {
                    userState = object;
                    callback.call(object);
                }
            }, UserState.class).execute();
        } else {
            callback.call(userState);
        }
    }

    public boolean justOpenedApp() {
        return justOpenedApp;
    }

    public void setJustOpenedApp(boolean justOpenedApp) {
        this.justOpenedApp = justOpenedApp;
    }

    public int timeSinceLastListViewRefresh() {
        if (lastListViewRefresh == null) {
            return Integer.MAX_VALUE;
        }
        return (int) (new Date().getTime() - lastListViewRefresh.getTime()) / 1000;
    }

    public void setTimestampOfLastListViewRefresh(Date timestamp) {
        lastListViewRefresh = timestamp;
    }

    private static class SaveActiveConversation extends AsyncTask<Void, Void, Void> {
        private Conversation conversation;

        public SaveActiveConversation(Conversation conversation) {
            this.conversation = conversation;
        }

        @Override
        protected Void doInBackground(Void... params) {
            conversation.save();
            return null;
        }
    }

    private static class SaveSingularEntityAsync extends AsyncTask<Void, Void, Void> {
        private SugarRecord entity;

        public SaveSingularEntityAsync(SugarRecord entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            entity.save();
            return null;
        }
    }

    private static class GetSingularEntityAsync<E extends SugarRecord> extends AsyncTask<Void, Void, Void> {
        private Action1<E> callback;
        private Class<E> entityClass;

        public GetSingularEntityAsync(Action1<E> callback, Class<E> entityClass) {
            this.callback = callback;
            this.entityClass = entityClass;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Iterator<E> entities = SugarRecord.findAll(entityClass);
            try {
                if (entities.hasNext()) {
                    callback.call(entities.next());
                } else {
                    callback.call(entityClass.newInstance());
                }
            } catch (Exception e) { }
            return null;
        }
    }
}
