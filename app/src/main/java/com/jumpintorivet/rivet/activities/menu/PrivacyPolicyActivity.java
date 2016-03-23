package com.jumpintorivet.rivet.activities.menu;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrivacyPolicyActivity extends Activity {
    @Bind(R.id.menu_privacy_policy_activity_text)
    TextView privacyPolicyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_privacy_policy_activity);
        ButterKnife.bind(this);
        privacyPolicyText.setText(Html.fromHtml(getResources().getString(R.string.privacy_policy_body)));
    }

    @OnClick(R.id.menu_privacy_policy_activity_back_button)
    public void backButtonTapped() {
        finish();
    }
}
