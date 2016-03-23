package com.jumpintorivet.rivet.injection;

import com.jumpintorivet.rivet.activities.LaunchSplashActivity;
import com.jumpintorivet.rivet.activities.ProfileActivity;
import com.jumpintorivet.rivet.activities.bar_on_bottom.BarOnBottomActivity;
import com.jumpintorivet.rivet.activities.bar_on_bottom.ListFragment;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list.ConversationListRow;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list.EmptyFeaturedListCell;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list.FeaturedListCell;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list.FeaturedListFragment;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing.ConversationViewingFragment;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing.FeaturedConversationViewingFragment;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing.ParentConversationDetailsModal;
import com.jumpintorivet.rivet.activities.conversation_making.ConversationMakingActivity;
import com.jumpintorivet.rivet.activities.menu.MenuActivity;
import com.jumpintorivet.rivet.activities.menu.my_conversations.MyConversationListActivity;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.SquareWithCenterText;
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.ReportBehaviorDialog;
import com.jumpintorivet.rivet.components.conversation_view.ConversationEndedListRow;
import com.jumpintorivet.rivet.components.conversation_view.FeaturedConversationFooterRow;
import com.jumpintorivet.rivet.components.conversation_view.FeaturedConversationHeaderRow;
import com.jumpintorivet.rivet.components.conversation_view.MessageListRow;
import com.jumpintorivet.rivet.components.conversation_view.ParentConversationListRow;
import com.jumpintorivet.rivet.components.conversation_view.PrivacyMask;
import com.jumpintorivet.rivet.components.sliding_tab.SlidingTabLayout;
import com.jumpintorivet.rivet.components.start_new_conversation_box.RoundedSquareView;
import com.jumpintorivet.rivet.components.start_new_conversation_box.StartNewConversationView;
import com.jumpintorivet.rivet.notifications.NotificationRegistrationService;
import com.jumpintorivet.rivet.notifications.RivetGCMListenerService;
import com.jumpintorivet.rivet.utils.NetworkConnectivityReceiver;

import dagger.Module;

@Module(
        injects = {
                ListFragment.class,
                BarOnBottomActivity.class,
                ConversationMakingActivity.class,
                ConversationViewingFragment.class,
                MessageListRow.class,
                SquareWithCenterText.class,
                ConversationEndedListRow.class,
                RoundedSquareView.class,
                ParentConversationListRow.class,
                StartNewConversationView.class,
                MyApplication.class,
                LaunchSplashActivity.class,
                NotificationRegistrationService.class,
                RivetGCMListenerService.class,
                PrivacyMask.class,
                ConversationListRow.class,
                MenuActivity.class,
                MyConversationListActivity.class,
                ReportBehaviorDialog.class,
                NetworkConnectivityReceiver.class,
                SlidingTabLayout.class,
                ProfileActivity.class,
                OmniBar.class,
                FeaturedListFragment.class,
                FeaturedListCell.class,
                FeaturedConversationViewingFragment.class,
                FeaturedConversationFooterRow.class,
                FeaturedConversationHeaderRow.class,
                ParentConversationDetailsModal.class,
                EmptyFeaturedListCell.class
        },
        complete = false,
        library = true
)
public class ClassInjectionList {
}
