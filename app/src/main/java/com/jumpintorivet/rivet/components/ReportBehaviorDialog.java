package com.jumpintorivet.rivet.components;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.repositories.ReportBehaviorRepository;
import com.jumpintorivet.rivet.repositories.dtos.ReportBehaviorDTO;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportBehaviorDialog extends DialogFragment {
    private static final String CONVERSATION_ID = "conversationId";

    @Inject
    ReportBehaviorRepository reportBehaviorRepository;
    private long conversationId;

    public static ReportBehaviorDialog newInstance(long conversationId) {
        ReportBehaviorDialog reportBehaviorDialog = new ReportBehaviorDialog();
        Bundle arguments = new Bundle();
        arguments.putLong(CONVERSATION_ID, conversationId);
        reportBehaviorDialog.setArguments(arguments);
        return reportBehaviorDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.components_report_behavior_dialog, container, false);
        ((MyApplication) getActivity().getApplication()).inject(this);
        ButterKnife.bind(this, mainView);
        conversationId = getArguments().getLong(CONVERSATION_ID);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return mainView;
    }

    @OnClick(R.id.components_report_dialog_inappropriate_messages)
    public void inappropriateMessagesTapped() {
        if (conversationId != -1) {
            reportBehaviorRepository.reportConversation(new ReportBehaviorDTO(ReportBehaviorDTO.REPORT_BEHAVIOR_TYPE_MESSAGES), conversationId);
        }
        dismiss();
        showConfirmDialog();
    }

    @OnClick(R.id.components_report_dialog_targets_someone)
    public void targetsSomeoneTapped() {
        if (conversationId != -1) {
            reportBehaviorRepository.reportConversation(new ReportBehaviorDTO(ReportBehaviorDTO.REPORT_BEHAVIOR_TYPE_TARGETS_SOMEONE), conversationId);
        }
        dismiss();
        showConfirmDialog();
    }

    @OnClick(R.id.components_report_dialog_other)
    public void otherTapped() {
        if (conversationId != -1) {
            reportBehaviorRepository.reportConversation(new ReportBehaviorDTO(ReportBehaviorDTO.REPORT_BEHAVIOR_TYPE_OTHER), conversationId);
        }
        dismiss();
        showConfirmDialog();
    }

    @OnClick(R.id.components_report_dialog_cancel_button)
    public void cancelTapped() {
        dismiss();
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.Success)
                .setMessage(R.string.report_successfully_filed)
                .setPositiveButton(R.string.Gotcha, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}