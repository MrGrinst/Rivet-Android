package com.jumpintorivet.rivet.activities.menu;

import android.app.Activity;
import android.os.Bundle;

import com.jumpintorivet.rivet.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpecialThanksActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_special_thanks);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.menu_special_thanks_back_button)
    public void backButtonTapped() {
        finish();
    }
}
