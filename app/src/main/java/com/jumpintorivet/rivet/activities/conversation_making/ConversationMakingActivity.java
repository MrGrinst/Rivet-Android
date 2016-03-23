package com.jumpintorivet.rivet.activities.conversation_making;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.IconButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing.ParentConversationDetailsModal;
import com.jumpintorivet.rivet.application.Foreground;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.ReportBehaviorDialog;
import com.jumpintorivet.rivet.components.conversation_view.ConversationViewListAdapter;
import com.jumpintorivet.rivet.components.conversation_view.ParentConversationListRow;
import com.jumpintorivet.rivet.components.conversation_view.PrivacyMask;
import com.jumpintorivet.rivet.components.start_new_conversation_box.StartNewConversationView;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.ConversationDetails;
import com.jumpintorivet.rivet.models.Message;
import com.jumpintorivet.rivet.models.QueueStatus;
import com.jumpintorivet.rivet.notifications.NotificationRegistrationService;
import com.jumpintorivet.rivet.repositories.ConversationRepository;
import com.jumpintorivet.rivet.repositories.RequestManager;
import com.jumpintorivet.rivet.repositories.RivetCallback;
import com.jumpintorivet.rivet.repositories.dtos.SendMessageDTO;
import com.jumpintorivet.rivet.repositories.dtos.UpdateConversationDTO;
import com.jumpintorivet.rivet.repositories.dtos.UserTypedDTO;
import com.jumpintorivet.rivet.utils.AppState;
import com.jumpintorivet.rivet.utils.ConstUtil;
import com.jumpintorivet.rivet.utils.EventTrackingUtil;
import com.jumpintorivet.rivet.utils.JsonUtil;
import com.jumpintorivet.rivet.utils.P;
import com.jumpintorivet.rivet.utils.RealtimeUtil;
import com.jumpintorivet.rivet.utils.RivetPreferences;
import com.jumpintorivet.rivet.utils.SearchingHeartbeatGenerator;
import com.jumpintorivet.rivet.utils.StringUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit.Response;

public class ConversationMakingActivity extends Activity {
    public static final String PARENT_CONVERSATION_ID = "PARENT_CONVERSATION_ID";
    private static final String CALLBACK_TAG_CONVERSATION_MAKING = "CALLBACK_TAG_CONVERSATION_MAKING";

    @Bind(R.id.conversation_making_activity_toolbar)
    Toolbar toolbar;
    @Bind(R.id.conversation_making_activity_toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.conversation_making_activity_pre_conversing_view_start_new_conversation_frame)
    StartNewConversationView startNewConversationView;
    @Bind(R.id.conversation_making_activity_pre_conversing_view)
    RelativeLayout preConversingView;
    @Bind(R.id.conversation_making_activity_conversing_view)
    LinearLayout conversingView;
    @Bind(R.id.conversation_making_activity_conversing_view_list_view)
    ListView messageListView;
    @Bind(R.id.conversation_making_activity_conversing_view_send_button)
    Button sendButton;
    @Bind(R.id.conversation_making_activity_conversing_view_new_message_text)
    EditText messageTextView;
    @Bind(R.id.conversation_making_activity_slid_privacy_mask_tutorial)
    SlidPrivacyMaskTutorial slidPrivacyMaskTutorial;
    @Bind(R.id.conversation_making_activity_received_private_message_tutorial)
    ReceivedPrivateMessageTutorial receivedPrivateMessageTutorial;
    @Bind(R.id.conversation_making_activity_report_button)
    IconButton reportButton;
    @Bind(R.id.conversation_making_activity_end_conversation_button)
    IconButton endConversationButton;
    @Inject
    Bus bus;
    @Inject
    RequestManager requestManager;
    @Inject
    RealtimeUtil realtimeUtil;
    @Inject
    JsonUtil jsonUtil;
    @Inject
    StringUtil stringUtil;
    @Inject
    ConstUtil constUtil;
    @Inject
    ConversationRepository conversationRepository;
    @Inject
    RivetPreferences rivetPreferences;
    @Inject
    AppState appState;
    @Inject
    EventTrackingUtil eventTrackingUtil;
    @Inject
    SearchingHeartbeatGenerator heartbeatGenerator;
    private long parentConversationId;
    private ConversationViewListAdapter conversationViewListAdapter;
    private AlertDialog alertDialog;
    private Timer typingTimer;
    private PrivacyMask privacyMask;
    private Date lastTypingEventSent;
    private boolean keyboardIsOpen;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardOpenListener;


    //Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_making_activity);
        ((MyApplication) getApplication()).inject(this);
        ButterKnife.bind(this);
        parentConversationId = getIntent().getLongExtra(PARENT_CONVERSATION_ID, -1);
        privacyMask = new PrivacyMask(this);
        toolbar.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        conversationViewListAdapter = new ConversationViewListAdapter(getLayoutInflater(), appState.getActiveConversation(), null, true, privacyMask);
        messageListView.setAdapter(conversationViewListAdapter);
        messageListView.setBackgroundColor(Color.TRANSPARENT);
        sendButton.setEnabled(false);
        registerToReceivePushNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        resetModeToCurrentMode();
        loadUpdatedConversation();
        addConversationListeners();
        listenForKeyboardOpen();
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        privacyMask.setIsOnRight(!rivetPreferences.isPrivacyMaskOnLeft());
        if (parentConversationId > 0 && appState.getActiveConversation() == null && appState.getWaitForMatchChannel() == null) {
            startOrStopSearchingForConversation(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
        requestManager.cancelRequestsWithTag(CALLBACK_TAG_CONVERSATION_MAKING);
        stopListeningForKeyboardOpen();
        removeConversationListeners();
        invalidateAndNullTypingTimer();
        if (!appState.activeConversationIsNullOrInactive()) {
            appState.saveActiveConversation();
        }
        bus.post(new OmniBar.UpdateChatButtonEvent(false));
        messageTextView.clearFocus();
    }


    //One Time Setup
    private void registerToReceivePushNotifications() {
        Intent intent = new Intent(this, NotificationRegistrationService.class);
        startService(intent);
    }


    //Set Modes
    private void setModeToPreWaiting() {
        conversingView.setVisibility(View.GONE);
        preConversingView.setVisibility(View.VISIBLE);
        startNewConversationView.setIsSearching(false);
        privacyMask.setVisibility(View.INVISIBLE);
        privacyMask.setIsOnRight(true);
        rivetPreferences.setIsPrivacyMaskOnLeft(false);
        appState.setConversationMode(ConversationMode.PRE_WAITING);
        endConversationButton.setVisibility(View.GONE);
        reportButton.setVisibility(View.GONE);
        toolbarTitle.setText("");
    }

    private void setModeToWaiting() {
        conversingView.setVisibility(View.GONE);
        preConversingView.setVisibility(View.VISIBLE);
        startNewConversationView.setIsSearching(true);
        privacyMask.setVisibility(View.INVISIBLE);
        privacyMask.setIsOnRight(true);
        rivetPreferences.setIsPrivacyMaskOnLeft(false);
        endConversationButton.setVisibility(View.GONE);
        reportButton.setVisibility(View.GONE);
        toolbarTitle.setText("");
    }

    private void setModeToConversing() {
        if (appState.getActiveConversation() != null) {
            appState.setConversationMode(ConversationMode.CONVERSING);
            if (conversingView != null) {
                conversingView.setVisibility(View.VISIBLE);
                preConversingView.setVisibility(View.GONE);
                startNewConversationView.setIsSearching(false);
                privacyMask.setVisibility(View.VISIBLE);
                conversationViewListAdapter.setConversation(appState.getActiveConversation());
                conversationViewListAdapter.notifyDataSetChanged();
                messageTextView.setEnabled(true);
                if (appState.getActiveConversation().isActive()) {
                    endConversationButton.setVisibility(View.VISIBLE);
                    reportButton.setVisibility(View.VISIBLE);
                } else {
                    reportButton.setVisibility(View.VISIBLE);
                    endConversationButton.setVisibility(View.GONE);
                    messageTextView.setEnabled(false);
                }
                toolbarTitle.setText(R.string.your_conversation);
            }
        } else {
            setModeToPreWaiting();
        }
    }

    private void resetModeToCurrentMode() {
        if (appState.getConversationMode() == null || appState.getConversationMode() == ConversationMode.PRE_WAITING) {
            setModeToPreWaiting();
        } else if (appState.getConversationMode() == ConversationMode.WAITING) {
            setModeToWaiting();
        } else if (appState.getConversationMode() == ConversationMode.CONVERSING) {
            setModeToConversing();
        }
    }

    public void goBackToBarOnBottomActivity() {
        hideKeyboard();
        resetConversationVariablesIfInactive();
        finish();
    }


    //Privacy Mask Tutorial
    private void showReceivedPrivateMessageTutorial() {
        int padding = P.convert(8);
        privacyMask.startBounceAnimation();
        receivedPrivateMessageTutorial.setVisibility(View.VISIBLE);
        receivedPrivateMessageTutorial.setMask(new Rect(constUtil.getScreenWidth() - P.convert(16) - padding,
                getResources().getDimensionPixelSize(R.dimen.omni_bar_height) + constUtil.getStatusBarHeight() - padding,
                constUtil.getScreenWidth(),
                getResources().getDimensionPixelSize(R.dimen.omni_bar_height) + constUtil.getStatusBarHeight() + P.convert(50) + padding));
    }

    @Subscribe
    public void privacyMaskSlidLeft(PrivacyMask.PrivacyMaskSlidLeftEvent event) {
        if (!rivetPreferences.hasSlidPrivacyMaskLeft()) {
            slidPrivacyMaskTutorial.setVisibility(View.VISIBLE);
            receivedPrivateMessageTutorial.setVisibility(View.GONE);
            rivetPreferences.setHasSlidPrivacyMaskLeft(true);
            eventTrackingUtil.slidPrivacyMaskLeftForTheFirstTime();
        }
        rivetPreferences.setIsPrivacyMaskOnLeft(true);
    }

    @Subscribe
    public void privacyMaskSlidRight(PrivacyMask.PrivacyMaskSlidRightEvent event) {
        slidPrivacyMaskTutorial.setVisibility(View.GONE);
        rivetPreferences.setIsPrivacyMaskOnLeft(false);
    }


    //Send and Receive Messages
    private void messageReceived(Message message) {
        if (!appState.getActiveConversation().getMessages().contains(message)) {
            appState.getActiveConversation().getMessages().add(message);
            Collections.sort(appState.getActiveConversation().getMessages());
            if (!message.isFromMe(appState.getActiveConversation().getMyParticipantNumber())) {
                appState.getActiveConversation().setOtherUserTyping(false);
                invalidateAndNullTypingTimer();
            }
            conversationViewListAdapter.notifyDataSetChanged();
            scrollToBottomOfMessages(true);
        }
    }

    private void displayPrivacyTutorialIfNecessary() {
        boolean hasPrivateMessages = false;
        for (Message message : appState.getActiveConversation().getMessages()) {
            hasPrivateMessages = message.isPrivate();
            if (hasPrivateMessages) break;
        }
        if (hasPrivateMessages
                && !rivetPreferences.hasSlidPrivacyMaskLeft()
                && !receivedPrivateMessageTutorial.isShown()) {
            showReceivedPrivateMessageTutorial();
            hideKeyboard();
        }
    }

    private void bouncePrivacyTabIfNecessary() {
        if (appState.getActiveConversation().getMessages().size() >= 10
                && !rivetPreferences.hasSlidPrivacyMaskLeft()
                && !receivedPrivateMessageTutorial.isShown()
                && !privacyMask.isAnimatingBounce()) {
            privacyMask.startBounceAnimation();
        }
    }

    @OnClick(R.id.conversation_making_activity_conversing_view_send_button)
    public void sendMessage() {
        final SendMessageDTO message = new SendMessageDTO();
        message.setText(messageTextView.getText().toString());
        message.setIs_private(!privacyMask.isOnRight());
        conversationRepository.sendMessageWithText(appState.getActiveConversation().getConversationId(), message, new RivetCallback<Void>() {
            @Override
            public void success(Response response) {
            }

            @Override
            public void failure(Throwable throwable, Response response) {
                if (RequestManager.isConnectionIssue(throwable, response) && messageTextView != null) {
                    messageTextView.setText(message.getText());
                }
            }
        }, "FAKE_TAG");
        messageTextView.setText("");
    }


    //Match Found
    private void matchFoundLater(QueueStatus queueStatus) {
        queueStatus.getConversationDetails().setParentConversations(queueStatus.getParentConversations());
        matchFound(queueStatus.getConversationDetails());
        heartbeatGenerator.stopSendingHeartbeat();
    }

    private void matchFound(ConversationDetails conversationDetails) {
        eventTrackingUtil.conversationStarted();
        appState.setWaitForMatchChannel(null);
        appState.setActiveConversation(new Conversation(conversationDetails, null));
        appState.saveActiveConversation();
        setModeToConversing();
        if (!isDestroyed() && Foreground.get().isForeground()) {
            removeConversationListeners();
            addConversationListeners();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bus != null) {
                        bus.post(new ConversationStartedEvent());
                    }
                }
            }, 2000);
        }
    }


    //Conversation Listeners
    private void addConversationListeners() {
        if (appState.getWaitForMatchChannel() != null) {
            realtimeUtil.listenToChannelAndEvent(appState.getWaitForMatchChannel(), RealtimeUtil.MATCH_FOUND, new RealtimeUtil.RealtimeCallback() {
                @Override
                public void callback(final String data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            matchFoundLater(jsonUtil.convertStringToObject(data, QueueStatus.class));
                        }
                    });
                }
            });
        } else if (!appState.activeConversationIsNullOrInactive()) {
            realtimeUtil.listenToChannelAndEvent(appState.getActiveConversation().getChannel(), RealtimeUtil.MESSAGE, new RealtimeUtil.RealtimeCallback() {
                @Override
                public void callback(final String data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageReceived(jsonUtil.convertStringToObject(data, Message.class));
                        }
                    });
                }
            });
            realtimeUtil.listenToChannelAndEvent(appState.getActiveConversation().getChannel(), RealtimeUtil.CONVERSATION_END, new RealtimeUtil.RealtimeCallback() {
                @Override
                public void callback(final String data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            conversationEnded(jsonUtil.convertStringToObject(data, ConversationDetails.class));
                        }
                    });
                }
            });
            realtimeUtil.listenToChannelAndEvent(appState.getActiveConversation().getChannel(), RealtimeUtil.CONVERSATION_UPDATED, new RealtimeUtil.RealtimeCallback() {
                @Override
                public void callback(final String data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateConversation(jsonUtil.convertStringToObject(data, ConversationDetails.class));
                        }
                    });
                }
            });
            realtimeUtil.listenToChannelAndEvent(appState.getActiveConversation().getChannel(), RealtimeUtil.USER_TYPING, new RealtimeUtil.RealtimeCallback() {
                @Override
                public void callback(final String data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userTyped(jsonUtil.convertStringToObject(data, UserTypedDTO.class));
                        }
                    });
                }
            });
        } else if (appState.getActiveConversation() != null && !appState.getActiveConversation().isActive()) {
            addConversationUpdateListener();
        }
    }

    private void removeConversationListeners() {
        realtimeUtil.stopListeningToChannel(appState.getWaitForMatchChannel());
        if (appState.getActiveConversation() != null) {
            realtimeUtil.stopListeningToChannel(appState.getActiveConversation().getChannel());
        }
    }

    private void addConversationUpdateListener() {
        realtimeUtil.listenToChannelAndEvent(appState.getActiveConversation().getChannel(), RealtimeUtil.CONVERSATION_UPDATED, new RealtimeUtil.RealtimeCallback() {
            @Override
            public void callback(final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateConversation(jsonUtil.convertStringToObject(data, ConversationDetails.class));
                    }
                });
            }
        });
    }


    //Update Conversation
    private void loadUpdatedConversation() {
        if (appState.getActiveConversation() != null && appState.getActiveConversation().getConversationId() == 0) {
            removeConversationListeners();
            resetConversationVariables();
            setModeToPreWaiting();
        } else if (!appState.activeConversationIsNullOrInactive()) {
            conversationRepository.refreshConversation(appState.getActiveConversation(), new RivetCallback<Void>() {
                @Override
                public void success(Response response) {
                    if (!appState.getActiveConversation().isActive()) {
                        eventTrackingUtil.conversationEnded();
                    }
                    appState.getActiveConversation().setOtherUserTyping(false);
                    invalidateAndNullTypingTimer();
                    changeUIIfInactiveConversation();
                    conversationViewListAdapter.notifyDataSetChanged();
                    scrollToBottomOfMessages(false);
                }

                @Override
                public void failure(Throwable throwable, Response response) {
                    if (response != null && response.code() == 404) {
                        appState.getActiveConversation().setEndTime(new Date());
                        appState.getActiveConversation().setOtherUserTyping(false);
                        changeUIIfInactiveConversation();
                        conversationViewListAdapter.notifyDataSetChanged();
                        scrollToBottomOfMessages(true);
                    }

                }
            }, CALLBACK_TAG_CONVERSATION_MAKING);
        } else if (appState.getActiveConversation() == null && appState.getWaitForMatchChannel() != null) {
            conversationRepository.getQueueStatus(new RivetCallback<QueueStatus>() {
                @Override
                public void success(Response<QueueStatus> response) {
                    if (response.body().isMatchFound()) {
                        final Conversation conversation = new Conversation(response.body().getConversationDetails(), null);
                        conversationRepository.getMessagesForConversationSinceTime(conversation.getConversationId(), new Date(0), new RivetCallback<List<Message>>() {
                            @Override
                            public void success(Response<List<Message>> response) {
                                conversation.setMessages(response.body());
                                appState.setActiveConversation(conversation);
                                appState.setWaitForMatchChannel(null);
                                conversationViewListAdapter.notifyDataSetChanged();
                                eventTrackingUtil.conversationStarted();
                                scrollToBottomOfMessages(true);
                                setModeToConversing();
                                addConversationListeners();
                            }

                            @Override
                            public void failure(Throwable throwable, Response response1) {
                                if (response1 != null && response1.code() == 404) {
                                    heartbeatGenerator.stopSendingHeartbeat();
                                    removeConversationListeners();
                                    resetConversationVariables();
                                    hideKeyboard();
                                    setModeToPreWaiting();
                                    startNewConversationView.setIsSearching(false);
                                }
                            }
                        }, CALLBACK_TAG_CONVERSATION_MAKING);
                    } else if (!response.body().isInQueue()) {
                        heartbeatGenerator.stopSendingHeartbeat();
                        removeConversationListeners();
                        resetConversationVariables();
                        hideKeyboard();
                        setModeToPreWaiting();
                        startNewConversationView.setIsSearching(false);
                    } else {
                        heartbeatGenerator.startSendingHeartbeatRightAway();
                    }
                }

                @Override
                public void failure(Throwable throwable, Response<QueueStatus> response) {
                }
            }, CALLBACK_TAG_CONVERSATION_MAKING);
        }
    }

    private void updateConversation(ConversationDetails conversationDetails) {
        if (appState.getActiveConversation() != null) {
            appState.getActiveConversation().updateWithConversationDetailsAndMessages(conversationDetails, null);
            conversationViewListAdapter.notifyDataSetChanged();
        }
    }


    //Typing Events
    @OnTextChanged(R.id.conversation_making_activity_conversing_view_new_message_text)
    public void messageTextChanged() {
        if (!appState.activeConversationIsNullOrInactive() && (lastTypingEventSent == null || (new Date().getTime() - lastTypingEventSent.getTime()) > 3000)) {
            realtimeUtil.sendTypingEvent(appState.getActiveConversation().getChannel(), appState.getActiveConversation().getMyParticipantNumber());
            lastTypingEventSent = new Date();
        }
        if (messageTextView.getText().toString().replaceAll(" ", "").isEmpty()) {
            sendButton.setEnabled(false);
        } else {
            sendButton.setEnabled(true);
        }
    }

    private void userTyped(UserTypedDTO userTypedDTO) {
        if (userTypedDTO.getParticipantNumber() != appState.getActiveConversation().getMyParticipantNumber()) {
            invalidateAndNullTypingTimer();
            if (!appState.getActiveConversation().isOtherUserTyping()) {
                appState.getActiveConversation().setOtherUserTyping(true);
            }
            conversationViewListAdapter.notifyDataSetChanged();
            if (messageListView.getLastVisiblePosition() == messageListView.getAdapter().getCount() - 2) {
                scrollToBottomOfMessages(true);
            }
            typingTimer = new Timer();
            typingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopTypingIndicator();
                        }
                    });
                }
            }, 5000);
        }
    }

    private void stopTypingIndicator() {
        appState.getActiveConversation().setOtherUserTyping(false);
        conversationViewListAdapter.notifyDataSetChanged();
        scrollToBottomOfMessages(true);
    }

    private void invalidateAndNullTypingTimer() {
        if (typingTimer != null) {
            typingTimer.cancel();
        }
        typingTimer = null;
    }


    //Report Conversation
    @OnClick(R.id.conversation_making_activity_report_button)
    public void reportConversation() {
        eventTrackingUtil.reportButtonTapped();
        if (appState.getActiveConversation() != null) {
            ReportBehaviorDialog.newInstance(appState.getActiveConversation().getConversationId()).show(getFragmentManager(), "ReportBehaviorDialog");
        }
    }


    //Parent Conversation
    @Subscribe
    public void parentConversationTapped(ParentConversationListRow.ParentConversationTappedEvent event) {
        ParentConversationDetailsModal.newInstance(event.parentConversation).show(getFragmentManager(), "ParentConversationDetailsModal");
    }


    //User Ended Conversation
    private void conversationEnded(ConversationDetails conversationDetails) {
        appState.getActiveConversation().updateWithConversationDetailsAndMessages(conversationDetails, null);
        changeUIIfInactiveConversation();
        conversationViewListAdapter.notifyDataSetChanged();
        scrollToBottomOfMessages(true);
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        eventTrackingUtil.conversationEnded();
    }


    //Start New Conversation
    @Subscribe
    public void startOrStopSearchingForConversation(StartNewConversationView.StartOrStopSearchingForConversationEvent event) {
        if (appState.getConversationMode() == ConversationMode.WAITING && appState.getActiveConversation() == null) {
            eventTrackingUtil.stoppedSearching();
            setModeToPreWaiting();
            heartbeatGenerator.stopSendingHeartbeat();
            conversationRepository.stopSearching(null, "FAKE_TAG");
            appState.setWaitForMatchChannel(null);
        } else if (appState.activeConversationIsNullOrInactive()) {
            if (appState.getConversationMode() == ConversationMode.CONVERSING) {
                eventTrackingUtil.startedSearchingAfterConversationEnd();
            } else {
                eventTrackingUtil.startedSearchingFromWaitingView();
            }
            removeConversationListeners();
            resetConversationVariables();
            setModeToWaiting();
            if (parentConversationId > 0) {
                conversationRepository.startSearchingWithParentConversationId(parentConversationId, new RivetCallback<QueueStatus>() {
                    @Override
                    public void success(Response<QueueStatus> response) {
                        try {
                            if (response.body().isMatchFound()) {
                                matchFound(response.body().getConversationDetails());
                            } else {
                                appState.setWaitForMatchChannel(response.body().getChannel());
                                appState.setConversationMode(ConversationMode.WAITING);
                                heartbeatGenerator.startSendingHeartbeat();
                                if (!isDestroyed()) {
                                    addConversationListeners();
                                    checkAgainForMatch();
                                }
                            }
                        } catch (NoSuchMethodError e) {
                        }
                    }

                    @Override
                    public void failure(Throwable throwable, Response<QueueStatus> response) {
                    }
                }, "FAKE_TAG");
            } else {
                conversationRepository.startSearching(new RivetCallback<QueueStatus>() {
                    @Override
                    public void success(Response<QueueStatus> response) {
                        try {
                            if (response.body().isMatchFound()) {
                                matchFound(response.body().getConversationDetails());
                            } else {
                                appState.setWaitForMatchChannel(response.body().getChannel());
                                appState.setConversationMode(ConversationMode.WAITING);
                                heartbeatGenerator.startSendingHeartbeat();
                                if (!isDestroyed()) {
                                    addConversationListeners();
                                    checkAgainForMatch();
                                }
                            }
                        } catch (NoSuchMethodError e) {
                        }
                    }

                    @Override
                    public void failure(Throwable throwable, Response<QueueStatus> response) {
                    }
                }, "FAKE_TAG");
            }
        }
    }

    private void checkAgainForMatch() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (appState.activeConversationIsNullOrInactive()) {
                    conversationRepository.getQueueStatus(new RivetCallback<QueueStatus>() {
                        @Override
                        public void success(Response<QueueStatus> response) {
                            if (response.body().isMatchFound()) {
                                matchFound(response.body().getConversationDetails());
                            }
                        }

                        @Override
                        public void failure(Throwable throwable, Response<QueueStatus> response) {
                        }
                    }, CALLBACK_TAG_CONVERSATION_MAKING);
                }
            }
        }, 1000);
    }


    //End Conversation
    @OnClick(R.id.conversation_making_activity_end_conversation_button)
    public void endConversationButtonTapped() {
        new AlertDialog.Builder(this)
                .setTitle(stringUtil.getStringUsingResourceId(R.string.confirm_end_conversation_title))
                .setMessage(stringUtil.getStringUsingResourceId(R.string.confirm_end_conversation_body))
                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endConversation();
                    }
                })
                .setNegativeButton(R.string.No, null)
                .create().show();
    }

    public void endConversation() {
        conversationRepository.updateConversation(new UpdateConversationDTO("completed"), appState.getActiveConversation().getConversationId(), new RivetCallback<Void>() {
            @Override
            public void success(Response response) {
            }

            @Override
            public void failure(Throwable throwable, Response response) {
                removeConversationListeners();
                loadUpdatedConversation();
            }
        }, CALLBACK_TAG_CONVERSATION_MAKING);
    }


    //Reset Conversation
    private void resetConversationVariables() {
        appState.setActiveConversation(null);
        appState.setWaitForMatchChannel(null);
        conversationViewListAdapter.setConversation(null);
        conversationViewListAdapter.notifyDataSetChanged();
    }

    private void resetConversationVariablesIfInactive() {
        if (appState.getActiveConversation() != null && !appState.getActiveConversation().isActive() && appState.getWaitForMatchChannel() == null) {
            resetConversationVariables();
            appState.setConversationMode(ConversationMode.PRE_WAITING);
        }
    }

    private void changeUIIfInactiveConversation() {
        if (appState.activeConversationIsNullOrInactive()) {
            messageTextView.setEnabled(false);
            messageTextView.getText().clear();
            invalidateAndNullTypingTimer();
            reportButton.setVisibility(View.VISIBLE);
            endConversationButton.setVisibility(View.GONE);
        }
    }


    //Scrolling
    private void scrollToBottomOfMessages(final boolean animate) {
        messageListView.post(new Runnable() {
            @Override
            public void run() {
                messageListView.smoothScrollByOffset(1);
                if (android.os.Build.VERSION.SDK_INT >= 19 && animate) {
                    messageListView.smoothScrollToPosition(conversationViewListAdapter.getCount());
                } else {
                    messageListView.setSelection(conversationViewListAdapter.getCount());
                }
            }
        });
    }


    //Keyboard Methods
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
    }

    private void listenForKeyboardOpen() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        keyboardOpenListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 200) {
                    if (!keyboardIsOpen) { //This should detect the keyboard opening
                        keyboardIsOpen = true;
                        scrollToBottomOfMessages(true);
                    }
                } else if (keyboardIsOpen) {
                    keyboardIsOpen = false;
                }
            }
        };
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardOpenListener);
    }

    private void stopListeningForKeyboardOpen() {
        getWindow().getDecorView().findViewById(android.R.id.content).getViewTreeObserver().removeOnGlobalLayoutListener(keyboardOpenListener);
    }

    @Override
    public void onBackPressed() {
        goBackToBarOnBottomActivity();
    }

    @OnClick(R.id.conversation_making_activity_back)
    public void backButtonTapped() {
        onBackPressed();
    }

    public static class ConversationStartedEvent {
    }
}
