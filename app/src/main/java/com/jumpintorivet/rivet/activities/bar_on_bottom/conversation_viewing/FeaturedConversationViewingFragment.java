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
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.conversation_view.ConversationViewListAdapter;
import com.jumpintorivet.rivet.components.conversation_view.PrivacyMask;
import com.jumpintorivet.rivet.components.start_new_conversation_box.StartNewConversationView;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.ConversationSummary;
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

public class FeaturedConversationViewingFragment extends Fragment {
    private static final String CONVERSATION = "conversation";
    private static final String CALLBACK_TAG_FEATURED_CONVERSATION_VIEWING = "CALLBACK_TAG_FEATURED_CONVERSATION_VIEWING";

    @Bind(R.id.bar_on_bottom_featured_conversation_viewing_list)
    ListView conversationListView;
    @Inject
    SharingImageCreator sharingImageCreator;
    @Inject
    RequestManager requestManager;
    @Inject
    ConversationRepository conversationRepository;
    @Inject
    Bus bus;
    @Inject
    EventTrackingUtil eventTrackingUtil;
    private ConversationSummary conversationSummary;
    private Conversation conversation;
    private ConversationViewListAdapter conversationViewListAdapter;
    private PrivacyMask privacyMask;

    public static FeaturedConversationViewingFragment newInstance(ConversationSummary conversationSummary) {
        FeaturedConversationViewingFragment fragment = new FeaturedConversationViewingFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(CONVERSATION, conversationSummary);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).inject(this);
        ViewGroup mainView = (ViewGroup) inflater.inflate(R.layout.bar_on_bottom_featured_conversation_viewing, container, false);
        ButterKnife.bind(this, mainView);
        privacyMask = new PrivacyMask(getActivity());
        conversationSummary = (ConversationSummary) getArguments().getSerializable(CONVERSATION);
        conversationViewListAdapter = new ConversationViewListAdapter(getActivity().getLayoutInflater(), conversation, conversationSummary, false, privacyMask);
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
        bus.post(new OmniBar.ShowFeaturedConversationViewingButtons());
        bus.post(new OmniBar.UpdateChatButtonEvent(true));
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        requestManager.cancelRequestsWithTag(CALLBACK_TAG_FEATURED_CONVERSATION_VIEWING);
        bus.post(new OmniBar.HideFeaturedConversationViewingButtons());
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
        }, CALLBACK_TAG_FEATURED_CONVERSATION_VIEWING);
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


    //Talk About This
    @Subscribe
    public void talkAboutThis(StartNewConversationView.StartOrStopSearchingForConversationEvent event) {
        bus.post(new OmniBar.ChatButtonTappedEvent());
    }

    private void conversationLoaded(Conversation conversation) {
        this.conversation = conversation;
        if (conversationListView != null) {
            conversationSummary.setHeadline(conversation.getHeadline());
            conversationSummary.setDescription(conversation.getDescription());
            conversationSummary.setPictureUrl(conversation.getPictureUrl());
            conversationViewListAdapter.setConversation(conversation);
            Collections.sort(conversation.getMessages());
            conversationViewListAdapter.notifyDataSetChanged();
        }
    }
}
