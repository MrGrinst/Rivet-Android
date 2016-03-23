package com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.conversation_view.MessageListRow;
import com.jumpintorivet.rivet.components.conversation_view.PrivacyMask;
import com.jumpintorivet.rivet.models.Message;
import com.jumpintorivet.rivet.models.ParentConversation;
import com.jumpintorivet.rivet.repositories.ConversationRepository;
import com.jumpintorivet.rivet.repositories.RivetCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Response;

public class ParentConversationDetailsModal extends DialogFragment {
    private static final String CALLBACK_TAG_PARENT_CONVERSATION_DETAILS_MODAL = "CALLBACK_TAG_PARENT_CONVERSATION_DETAILS_MODAL";
    private static final String CONVERSATION = "CONVERSATION";

    @Bind(R.id.bar_on_bottom_parent_conversation_details_modal_list)
    ListView listView;
    @Inject
    ConversationRepository conversationRepository;

    private ParentConversation parentConversation;
    private ParentConversationDetailsModalListAdapter adapter;
    private List<Message> messages = new ArrayList<>();

    public static ParentConversationDetailsModal newInstance(ParentConversation parentConversation) {
        ParentConversationDetailsModal modal = new ParentConversationDetailsModal();
        Bundle args = new Bundle();
        args.putSerializable(CONVERSATION, parentConversation);
        modal.setArguments(args);
        return modal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.bar_on_bottom_parent_conversation_details_modal, container, false);
        ((MyApplication) getActivity().getApplication()).inject(this);
        ButterKnife.bind(this, mainView);
        parentConversation = (ParentConversation) getArguments().getSerializable(CONVERSATION);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        adapter = new ParentConversationDetailsModalListAdapter(getActivity(), parentConversation, messages);
        listView.setAdapter(adapter);
        loadMessages();
        return mainView;
    }

    private void loadMessages() {
        conversationRepository.getMessagesForConversationSinceTime(parentConversation.getConversationId(), new Date(0), new RivetCallback<List<Message>>() {
            @Override
            public void success(Response<List<Message>> response) {
                messages.clear();
                messages.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(Throwable throwable, Response<List<Message>> response) {
            }
        }, CALLBACK_TAG_PARENT_CONVERSATION_DETAILS_MODAL);
    }

    private static class ParentConversationDetailsModalListAdapter extends BaseAdapter {
        private static final int HEADER_TYPE = 0;
        private static final int MESSAGE_TYPE = 1;

        private LayoutInflater inflater;
        private ParentConversation parentConversation;
        private List<Message> messages;
        private PrivacyMask privacyMask;

        public ParentConversationDetailsModalListAdapter(Activity context, ParentConversation parentConversation, List<Message> messages) {
            this.inflater = context.getLayoutInflater();
            this.parentConversation = parentConversation;
            this.messages = messages;
            privacyMask = new PrivacyMask(context);
        }

        @Override
        public int getCount() {
            return messages.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            switch (type) {
                case HEADER_TYPE:
                    if (convertView == null || !(convertView instanceof ParentConversationDetailsModalHeader)) {
                        convertView = inflater.inflate(R.layout.bar_on_bottom_parent_conversation_details_modal_header, null);
                    }
                    ((ParentConversationDetailsModalHeader) convertView).setParentConversation(parentConversation);
                    break;
                case MESSAGE_TYPE:
                    if (convertView == null || !(convertView instanceof MessageListRow)) {
                        convertView = inflater.inflate(R.layout.components_conversation_view_message_list_row, null);
                    }
                    MessageListRow cmlr = (MessageListRow) convertView;
                    Message message = messages.get(position - 1);
                    cmlr.setMessage(message, message.isOnLeftWithMyParticipantNumber(-1), false);
                    privacyMask.addMaskToCell(cmlr);
                    break;
            }
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER_TYPE;
            } else {
                return MESSAGE_TYPE;
            }
        }
    }
}
