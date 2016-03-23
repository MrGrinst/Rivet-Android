package com.jumpintorivet.rivet.activities.menu.my_conversations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list.ConversationListRow;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.LoadingView;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.repositories.ConversationCache;
import com.jumpintorivet.rivet.repositories.ConversationRepository;
import com.jumpintorivet.rivet.repositories.RequestManager;
import com.jumpintorivet.rivet.repositories.RivetCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit.Response;
import rx.functions.Action1;

public class MyConversationListActivity extends Activity {
    private static final String CALLBACK_TAG_MY_CONVERSATION_LIST = "CALLBACK_TAG_MY_CONVERSATION_LIST";

    @Bind(R.id.menu_my_conversations_list_view)
    ListView listView;
    @Bind(R.id.menu_my_conversations_empty_list_view)
    TextView emptyTextView;
    @Bind(R.id.menu_my_conversations_loading_view)
    LoadingView loadingView;
    @Inject
    RequestManager requestManager;
    @Inject
    ConversationRepository conversationRepository;
    @Inject
    ConversationCache conversationCache;
    private List<ConversationSummary> conversations;
    private ConversationListAdapter conversationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_my_conversation_list_activity);
        ButterKnife.bind(this);
        ((MyApplication) getApplication()).inject(this);
        conversations = new ArrayList<>();
        conversationListAdapter = new ConversationListAdapter(this, conversations);
        loadingView.setText(getResources().getString(R.string.loading_conversations));
        listView.setAdapter(conversationListAdapter);
        listView.setEmptyView(emptyTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCachedConversations();
        loadMyConversations();
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestManager.cancelRequestsWithTag(CALLBACK_TAG_MY_CONVERSATION_LIST);
    }

    private void loadCachedConversations() {
        conversationCache.getCachedMyConversations(this, new Action1<List<ConversationSummary>>() {
            @Override
            public void call(List<ConversationSummary> conversations) {
                conversationsLoaded(conversations);
            }
        });
    }

    private void loadMyConversations() {
        conversationRepository.getMyConversationsAndCache(new RivetCallback<List<ConversationSummary>>() {
            @Override
            public void success(Response<List<ConversationSummary>> response) {
                conversationsLoaded(response.body());
            }

            @Override
            public void failure(Throwable throwable, Response<List<ConversationSummary>> response) {
            }
        }, CALLBACK_TAG_MY_CONVERSATION_LIST);
    }

    private void conversationsLoaded(List<ConversationSummary> conversationSummaries) {
        this.conversations.clear();
        conversations.addAll(conversationSummaries);
        Collections.sort(conversations, ConversationSummary.NEW_COMPARATOR);
        loadingView.doneLoading();
        conversationListAdapter.notifyDataSetChanged();
    }

    @OnItemClick(R.id.menu_my_conversations_list_view)
    public void conversationSelected(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, MyConversationViewingActivity.class);
        intent.putExtra(MyConversationViewingActivity.CONVERSATION, conversationListAdapter.getItem(position));
        startActivity(intent);
    }

    @OnClick(R.id.menu_my_conversations_back_button)
    public void backButtonTapped() {
        finish();
    }

    private class ConversationListAdapter extends ArrayAdapter<ConversationSummary> {
        private Activity activity;

        public ConversationListAdapter(Activity activity, List<ConversationSummary> conversations) {
            super(activity, 0, conversations);
            this.activity = activity;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.bar_on_bottom_list_conversation_list_row, null);
            }
            ConversationListRow row = (ConversationListRow) convertView;
            row.setConversationSummary(getItem(position));
            return convertView;
        }
    }
}
