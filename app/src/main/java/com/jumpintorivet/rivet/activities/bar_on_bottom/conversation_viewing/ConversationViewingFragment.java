package com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.LoadingView;
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.conversation_view.ConversationViewListAdapter;
import com.jumpintorivet.rivet.components.conversation_view.ParentConversationListRow;
import com.jumpintorivet.rivet.components.conversation_view.PrivacyMask;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.repositories.ConversationCache;
import com.jumpintorivet.rivet.repositories.ConversationRepository;
import com.jumpintorivet.rivet.repositories.RequestManager;
import com.jumpintorivet.rivet.repositories.RivetCallback;
import com.jumpintorivet.rivet.utils.EventTrackingUtil;
import com.jumpintorivet.rivet.utils.SharingImageCreator;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Collections;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;
import timber.log.Timber;

public class ConversationViewingFragment extends Fragment {
    private static final String CONVERSATION = "conversation";
    private static final String CALLBACK_TAG_CONVERSATION_VIEWING = "CALLBACK_TAG_CONVERSATION_VIEWING";

    @Bind(R.id.bar_on_bottom_conversation_viewing_list_view)
    ListView conversationListView;
    @Bind(R.id.bar_on_bottom_conversation_viewing_loading_view)
    LoadingView loadingView;
    @Inject
    SharingImageCreator sharingImageCreator;
    @Inject
    RequestManager requestManager;
    @Inject
    ConversationRepository conversationRepository;
    @Inject
    ConversationCache conversationCache;
    @Inject
    Bus bus;
    @Inject
    EventTrackingUtil eventTrackingUtil;
    private Conversation conversation;
    private ConversationSummary conversationSummary;
    private ConversationViewListAdapter conversationViewListAdapter;
    private PrivacyMask privacyMask;
    private ViewGroup mainView;

    public static ConversationViewingFragment newInstance(ConversationSummary conversationSummary) {
        ConversationViewingFragment fragment = new ConversationViewingFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(CONVERSATION, conversationSummary);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).inject(this);
        mainView = (ViewGroup) inflater.inflate(R.layout.bar_on_bottom_conversation_viewing_fragment, container, false);
        ButterKnife.bind(this, mainView);
        conversationSummary = (ConversationSummary) getArguments().getSerializable(CONVERSATION);
        privacyMask = new PrivacyMask(getActivity());
        loadingView.setText(getResources().getString(R.string.loading_conversation));
        conversationViewListAdapter = new ConversationViewListAdapter(getActivity().getLayoutInflater(), conversation, null, false, privacyMask);
        conversationListView.setAdapter(conversationViewListAdapter);
        loadConversation();
        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        conversationViewListAdapter.notifyDataSetChanged();
        bus.post(new OmniBar.ShowConversationViewingButtons());
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        requestManager.cancelRequestsWithTag(CALLBACK_TAG_CONVERSATION_VIEWING);
        bus.post(new OmniBar.HideConversationViewingButtons());
    }

    private void loadConversation() {
        conversationRepository.getWholeConversationById(conversationSummary.getConversationId(), new RivetCallback<Conversation>() {
            @Override
            public void success(Response<Conversation> response) {
                conversationLoaded(response.body());
            }

            @Override
            public void failure(Throwable throwable, Response<Conversation> response) {
            }
        }, CALLBACK_TAG_CONVERSATION_VIEWING);
    }


    //Share Conversation
    @Subscribe
    public void shareConversation(OmniBar.ShowShareDialogEvent event) {
        eventTrackingUtil.shareButtonTapped();
        if (conversation != null) {
            try {
                Uri uri = sharingImageCreator.createImageFromDescriptionAndHeadline(conversation.getDescription(), conversation.getHeadline(), conversation.isFeatured());
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "http://share.jumpintorivet.com/#conversation/" + conversation.getConversationId() * 17 + "/featured/" + (conversation.isFeatured() ? "true" : "false"));
                shareIntent.setType("image/*");
                startActivity(shareIntent);
            } catch (Exception e) {
                Timber.e(e, "Error saving share image to disk");
            }
        }
    }


    private void conversationLoaded(Conversation conversation) {
        this.conversation = conversation;
        if (loadingView != null) {
            loadingView.doneLoading();
            conversationViewListAdapter.setConversation(conversation);
            Collections.sort(conversation.getMessages());
            conversationViewListAdapter.notifyDataSetChanged();
        }
    }

    //Parent Conversation
    @Subscribe
    public void parentConversationTapped(ParentConversationListRow.ParentConversationTappedEvent event) {
        ParentConversationDetailsModal.newInstance(event.parentConversation).show(getActivity().getFragmentManager(), "ParentConversationDetailsModal");
    }
}