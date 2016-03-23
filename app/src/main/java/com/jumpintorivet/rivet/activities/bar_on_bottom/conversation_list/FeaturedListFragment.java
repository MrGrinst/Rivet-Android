package com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.models.UserState;
import com.jumpintorivet.rivet.repositories.ConversationCache;
import com.jumpintorivet.rivet.repositories.ConversationRepository;
import com.jumpintorivet.rivet.repositories.RequestManager;
import com.jumpintorivet.rivet.repositories.RivetCallback;
import com.jumpintorivet.rivet.repositories.UserRepository;
import com.jumpintorivet.rivet.utils.AppState;
import com.jumpintorivet.rivet.utils.ConstUtil;
import com.jumpintorivet.rivet.utils.P;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;
import rx.functions.Action1;

public class FeaturedListFragment extends Fragment {
    private static final int AUTO_REFRESH_INTERVAL_SECONDS = 60 * 10;
    private static final String CALLBACK_TAG_FEATURED_LIST_FRAGMENT = "FEATURED_LIST_FRAGMENT";

    @Bind(R.id.bar_on_bottom_list_featured_conversation_list_fragment_view_pager)
    ViewPager viewPager;
    @Inject
    RequestManager requestManager;
    @Inject
    ConversationRepository conversationRepository;
    @Inject
    ConversationCache conversationCache;
    @Inject
    UserRepository userRepository;
    @Inject
    AppState appState;
    @Inject
    Bus bus;
    @Inject
    ConstUtil constUtil;
    private FeaturedConversationViewPagerAdapter adapter;
    private List<ConversationSummary> conversations = new ArrayList<>();
    private FeaturedListType type;

    public static FeaturedListFragment getInstance(FeaturedListType type) {
        Bundle extras = new Bundle();
        extras.putSerializable("TYPE", type);
        FeaturedListFragment fragment = new FeaturedListFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MyApplication) getActivity().getApplication()).inject(this);
        View mainView = inflater.inflate(R.layout.bar_on_bottom_list_featured_conversation_list_fragment, container, false);
        type = (FeaturedListType) getArguments().getSerializable("TYPE");
        ButterKnife.bind(this, mainView);
        adapter = new FeaturedConversationViewPagerAdapter(getActivity(), conversations);
        viewPager.setAdapter(adapter);
        viewPager.setPageMargin(P.convert(20));
        int viewPagerPadding = (int) (Math.max(P.convert(70), constUtil.getScreenWidth() - P.convert(320)) / 2.0);
        viewPager.setPadding(viewPagerPadding, 0, viewPagerPadding, 0);
        loadCachedConversations();
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
        if (appState.justOpenedApp()) {
            updateLocation();
            appState.setJustOpenedApp(false);
        } else if (appState.timeSinceLastListViewRefresh() > AUTO_REFRESH_INTERVAL_SECONDS) {
            loadFreshConversations(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        requestManager.cancelRequestsWithTag(CALLBACK_TAG_FEATURED_LIST_FRAGMENT);
    }

    private void updateLocation() {
        userRepository.updateLocation(new RivetCallback<UserState>() {
            @Override
            public void success(Response response) {
                loadFreshConversations(true);
            }

            @Override
            public void failure(Throwable throwable, Response response) {
                loadFreshConversations(true);
            }
        }, "FAKE_TAG");
    }

    private void loadCachedConversations() {
        if (type == FeaturedListType.GLOBAL) {
            conversationCache.getCachedGlobalFeaturedConversations(getActivity(), new Action1<List<ConversationSummary>>() {
                @Override
                public void call(List<ConversationSummary> conversations) {
                    conversationsLoaded(conversations);
                }
            });
        } else if (type == FeaturedListType.NEARBY) {
            conversationCache.getCachedNearbyFeaturedConversations(getActivity(), new Action1<List<ConversationSummary>>() {
                @Override
                public void call(List<ConversationSummary> conversations) {
                    conversationsLoaded(conversations);
                }
            });
        }
    }

    private void loadFreshConversations(boolean refreshAll) {
        final boolean globalType = type == FeaturedListType.GLOBAL;
        final boolean nearbyType = type == FeaturedListType.NEARBY;
        if (globalType || refreshAll) {
            conversationRepository.getGlobalFeaturedConversationsAndCache(new RivetCallback<List<ConversationSummary>>() {
                @Override
                public void success(Response<List<ConversationSummary>> response) {
                    if (globalType) {
                        conversationsLoaded(response.body());
                    }
                }

                @Override
                public void failure(Throwable throwable, Response<List<ConversationSummary>> response) {
                }
            }, CALLBACK_TAG_FEATURED_LIST_FRAGMENT);
        }
        if (nearbyType || refreshAll) {
            conversationRepository.getNearbyFeaturedConversationsAndCache(new RivetCallback<List<ConversationSummary>>() {
                @Override
                public void success(Response<List<ConversationSummary>> response) {
                    if (nearbyType) {
                        conversationsLoaded(response.body());
                    }
                }

                @Override
                public void failure(Throwable throwable, Response<List<ConversationSummary>> response) {
                }
            }, CALLBACK_TAG_FEATURED_LIST_FRAGMENT);
        }
        if (refreshAll) {
            appState.setTimestampOfLastListViewRefresh(new Date());
        }
    }

    private void conversationsLoaded(List<ConversationSummary> conversationSummaries) {
        //TODO: remove this null pointer check once network stuff is fixed
        if (viewPager != null) {
            conversations.clear();
            conversations.addAll(conversationSummaries);
            Collections.sort(conversations, ConversationSummary.NEW_COMPARATOR);
            adapter.notifyDataSetChanged();
        }
    }

    private class FeaturedConversationViewPagerAdapter extends PagerAdapter {
        private Activity activity;
        private List<ConversationSummary> conversations;
        private Stack<FeaturedListCell> recycledViews = new Stack<>();

        public FeaturedConversationViewPagerAdapter(Activity activity, List<ConversationSummary> conversations) {
            this.activity = activity;
            this.conversations = conversations;
        }

        @Override
        public int getCount() {
            if (conversations.size() == 0) {
                return 1;
            }
            return conversations.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (conversations.size() == 0) {
                EmptyFeaturedListCell cell = (EmptyFeaturedListCell) activity.getLayoutInflater().inflate(R.layout.bar_on_bottom_list_featured_conversation_empty_list_fragment_cell, null);
                container.addView(cell);
                if (type == FeaturedListType.GLOBAL) {
                    cell.setText(R.string.no_featured_conversations_global);
                } else if (type == FeaturedListType.NEARBY) {
                    cell.setText(R.string.no_featured_conversations_around_you);
                }
                return cell;
            } else {
                FeaturedListCell cell;
                if (!recycledViews.isEmpty()) {
                    cell = recycledViews.pop();
                } else {
                    cell = (FeaturedListCell) activity.getLayoutInflater().inflate(R.layout.bar_on_bottom_list_featured_conversation_list_fragment_cell, null);
                }
                cell.setConversation(conversations.get(position));
                container.addView(cell);
                return cell;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            if (object instanceof FeaturedListCell) {
                recycledViews.push((FeaturedListCell) object);
            }
        }

        @Override
        public float getPageWidth(int position) {
            return 1;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public FeaturedListType getType() {
        return type;
    }

    public static enum FeaturedListType {
        GLOBAL,
        NEARBY
    }
}
