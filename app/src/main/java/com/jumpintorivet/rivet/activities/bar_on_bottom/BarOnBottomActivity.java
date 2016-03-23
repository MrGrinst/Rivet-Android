package com.jumpintorivet.rivet.activities.bar_on_bottom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.github.mrengineer13.snackbar.SnackBar;
import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing.ConversationViewingFragment;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing.FeaturedConversationViewingFragment;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing.ParentConversationDetailsModal;
import com.jumpintorivet.rivet.activities.conversation_making.ConversationMakingActivity;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.conversation_view.ParentConversationListRow;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.ConversationDetails;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.notifications.RivetGCMListenerService;
import com.jumpintorivet.rivet.repositories.ConversationRepository;
import com.jumpintorivet.rivet.repositories.RivetCallback;
import com.jumpintorivet.rivet.utils.EventTrackingUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import retrofit.Response;

public class BarOnBottomActivity extends AppCompatActivity {
    private static final String CALLBACK_TAG_BAR_ON_BOTTOM_ACTIVITY = "CALLBACK_TAG_BAR_ON_BOTTOM_ACTIVITY";
    public static final String URL_INTENT_CONVERSATION_ID = "URL_INTENT_CONVERSATION_ID";
    public static final String URL_INTENT_IS_FEATURED = "URL_INTENT_IS_FEATURED";

    @Inject
    Bus bus;
    @Inject
    EventTrackingUtil eventTrackingUtil;
    @Inject
    ConversationRepository conversationRepository;
    private Fragment currentFragment;
    private long currentFeaturedConversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).inject(this);
        setContentView(R.layout.bar_on_bottom_activity);
        ButterKnife.bind(this);
        currentFragment = new ListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.bar_on_bottom_activity_fragment_container, currentFragment, "ListFragment")
                .commit();
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getInt(URL_INTENT_CONVERSATION_ID, -1) != -1) {
            String isFeatured = getIntent().getExtras().getString(URL_INTENT_IS_FEATURED, "unknown");
            if (isFeatured.equals("unknown")) {
                conversationRepository.getConversationDetails(getIntent().getExtras().getInt(URL_INTENT_CONVERSATION_ID), new RivetCallback<ConversationDetails>() {
                    @Override
                    public void success(Response<ConversationDetails> response) {
                        transitionToConversationViaLink(getIntent().getExtras().getInt(URL_INTENT_CONVERSATION_ID), response.body().isFeatured());
                    }

                    @Override
                    public void failure(Throwable throwable, Response<ConversationDetails> response) {
                        transitionToConversationViaLink(getIntent().getExtras().getInt(URL_INTENT_CONVERSATION_ID), false);
                    }
                }, CALLBACK_TAG_BAR_ON_BOTTOM_ACTIVITY);
            } else if (isFeatured.equals("true")) {
                transitionToConversationViaLink(getIntent().getExtras().getInt(URL_INTENT_CONVERSATION_ID), true);
            } else {
                transitionToConversationViaLink(getIntent().getExtras().getInt(URL_INTENT_CONVERSATION_ID), false);
            }
        }
    }

    private void transitionToConversationViaLink(long conversationId, boolean isFeatured) {
        ConversationSummary summary = new ConversationSummary();
        summary.setConversationId(conversationId);
        if (isFeatured) {
            transitionToFeaturedConversationViewing(summary);
        } else {
            transitionToConversationViewing(summary);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void goToConversationMakingActivity(OmniBar.ChatButtonTappedEvent event) {
        Intent intent = new Intent(this, ConversationMakingActivity.class);
        if (currentFragment instanceof FeaturedConversationViewingFragment) {
            intent.putExtra(ConversationMakingActivity.PARENT_CONVERSATION_ID, currentFeaturedConversationId);
        }
        startActivity(intent);
    }

    @Subscribe
    public void clickedOnConversationInList(ListFragment.ClickedOnConversationInListEvent event) {
        transitionToConversationViewing(event.conversationSummary);
    }

    @Subscribe
    public void clickedOnFeaturedConversationInList(ListFragment.ClickedOnFeaturedConversationInListEvent event) {
        transitionToFeaturedConversationViewing(event.conversationSummary);
    }

    @Subscribe
    public void toolbarBackButtonTapped(ToolbarBackButtonTappedEvent event) {
        onBackPressed();
    }

    @Subscribe
    public void closeButtonTapped(OmniBar.CloseButtonTappedEvent event) {
        transitionToList();
    }

    @Subscribe
    public void notificationReceived(RivetGCMListenerService.NotificationReceivedEvent event) {
        new SnackBar.Builder(this)
                .withMessage(event.getBody())
                .withDuration(SnackBar.MED_SNACK)
                .withBackgroundColorId(R.color.rivet_orange)
                .withTextColorId(R.color.rivet_off_black)
                .withActionMessageId(R.string.view)
                .withOnClickListener(new SnackBar.OnMessageClickListener() {
                    @Override
                    public void onMessageClick(Parcelable parcelable) {
                        goToConversationMakingActivity(null);
                    }
                })
                .show();
    }

    @Subscribe
    public void parentConversationTapped(ParentConversationListRow.ParentConversationTappedEvent event) {
        ParentConversationDetailsModal.newInstance(event.parentConversation).show(getFragmentManager(), "ParentConversationDetailsModal");
    }

    private void transitionToConversationViewing(ConversationSummary conversationSummary) {
        eventTrackingUtil.selectedConversationFromListView(conversationSummary.getConversationId());
        currentFragment = ConversationViewingFragment.newInstance(conversationSummary);
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.bar_on_bottom_activity_fragment_container, currentFragment, "ConversationViewingFragment")
                .addToBackStack("ConversationViewingFragment")
                .commit();
    }

    private void transitionToFeaturedConversationViewing(ConversationSummary conversationSummary) {
        eventTrackingUtil.selectedFeaturedConversationFromListView(conversationSummary.getConversationId());
        currentFragment = FeaturedConversationViewingFragment.newInstance(conversationSummary);
        currentFeaturedConversationId = conversationSummary.getConversationId();
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.bar_on_bottom_activity_fragment_container, currentFragment, "FeaturedConversationViewingFragment")
                .addToBackStack("FeaturedConversationViewingFragment")
                .commit();
    }

    private void transitionToList() {
        getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        currentFragment = getSupportFragmentManager().findFragmentByTag("ListFragment");
    }

    @Override
    public void onBackPressed() {
        if (currentFragment instanceof ListFragment && ((ListFragment) currentFragment).getSelectedListFragmentIndex() > 0) {
            ((ListFragment) currentFragment).setSelectedListFragmentIndex(0);
        } else {
            super.onBackPressed();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            currentFragment = getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName());
        } else {
            currentFragment = getSupportFragmentManager().findFragmentByTag("ListFragment");
        }
    }

    public static class ToolbarBackButtonTappedEvent {
    }
}
