package com.jumpintorivet.rivet.activities.bar_on_bottom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list.FeaturedListFragment;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.sliding_tab.SlidingTabLayout;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.models.UserState;
import com.jumpintorivet.rivet.repositories.RequestManager;
import com.jumpintorivet.rivet.utils.AppState;
import com.jumpintorivet.rivet.utils.EventTrackingUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class ListFragment extends Fragment {
    private static final String CALLBACK_TAG_LIST_FRAGMENT = "CALLBACK_TAG_LIST_FRAGMENT";

    @Bind(R.id.bar_on_bottom_list_fragment_view_pager)
    ViewPager viewPager;
    @Bind(R.id.bar_on_bottom_list_fragment_sliding_tabs)
    SlidingTabLayout slidingTabLayout;
    @Inject
    Bus bus;
    @Inject
    RequestManager requestManager;
    @Inject
    EventTrackingUtil eventTrackingUtil;
    @Inject
    AppState appState;
    private int numberOfTabs;
    private String[] titles;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).inject(this);
        ViewGroup mainView = (ViewGroup) inflater.inflate(R.layout.bar_on_bottom_list_fragment, container, false);
        ButterKnife.bind(this, mainView);
        titles = new String[]{getResources().getString(R.string.Featured), getResources().getString(R.string.Nearby)};
        SlidingTabAdapter slidingTabAdapter = new SlidingTabAdapter(getChildFragmentManager());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(slidingTabAdapter);
        viewPager.addOnPageChangeListener(new ListPageChangeListener());
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
        appState.getUserState(new Action1<UserState>() {
            @Override
            public void call(UserState userState) {
                if (userState.isNearbyEligible()) {
                    showNearbyEligibleInterface();
                } else {
                    hideNearbyEligibleInterface();
                }
            }
        });
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
        bus.post(new OmniBar.UpdateChatButtonEvent(false));
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        requestManager.cancelRequestsWithTag(CALLBACK_TAG_LIST_FRAGMENT);
    }

    public int getSelectedListFragmentIndex() {
        return viewPager.getCurrentItem();
    }

    public void setSelectedListFragmentIndex(int index) {
        viewPager.setCurrentItem(index);
    }

    @Subscribe
    public void userStateUpdated(UserState.UpdatedUserStateEvent event) {
        if (event.userState.isNearbyEligible()) {
            showNearbyEligibleInterface();
        } else {
            hideNearbyEligibleInterface();
        }
    }

    private void showNearbyEligibleInterface() {
        slidingTabLayout.setVisibility(View.VISIBLE);
        numberOfTabs = 2;
        viewPager.getAdapter().notifyDataSetChanged();
        slidingTabLayout.setViewPager(viewPager);
    }

    private void hideNearbyEligibleInterface() {
        slidingTabLayout.setVisibility(View.GONE);
        numberOfTabs = 1;
        viewPager.getAdapter().notifyDataSetChanged();
        slidingTabLayout.setViewPager(viewPager);
    }

    private class SlidingTabAdapter extends FragmentPagerAdapter {
        public SlidingTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return numberOfTabs;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return FeaturedListFragment.getInstance(FeaturedListFragment.FeaturedListType.GLOBAL);
            } else {
                return FeaturedListFragment.getInstance(FeaturedListFragment.FeaturedListType.NEARBY);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof FeaturedListFragment) {
                if (((FeaturedListFragment) object).getType() == FeaturedListFragment.FeaturedListType.GLOBAL) {
                    return 0;
                } else if (((FeaturedListFragment) object).getType() == FeaturedListFragment.FeaturedListType.NEARBY && numberOfTabs == 2) {
                    return 1;
                }
            }
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    public static class ClickedOnConversationInListEvent {
        public final ConversationSummary conversationSummary;

        public ClickedOnConversationInListEvent(ConversationSummary conversationSummary) {
            this.conversationSummary = conversationSummary;
        }
    }

    public static class ClickedOnFeaturedConversationInListEvent {
        public final ConversationSummary conversationSummary;

        public ClickedOnFeaturedConversationInListEvent(ConversationSummary conversationSummary) {
            this.conversationSummary = conversationSummary;
        }
    }

    private class ListPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                eventTrackingUtil.featuredFilterSelected();
            } else {
                eventTrackingUtil.nearbyFilterSelected();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

}