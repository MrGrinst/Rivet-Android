package com.jumpintorivet.rivet.activities.menu;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsAndConditionsActivity extends Activity {
    @Bind(R.id.menu_terms_and_conditions_text)
    TextView termsAndConditionsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_terms_and_conditions);
        ButterKnife.bind(this);
        termsAndConditionsText.setText(Html.fromHtml(getResources().getString(R.string.terms_and_conditions_body)));
    }

    @OnClick(R.id.menu_terms_and_conditions_back_button)
    public void backButtonTapped() {
        finish();
    }
}
