package com.jumpintorivet.rivet.activities.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.jumpintorivet.rivet.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JumpIntoRivetActivity extends Activity {
    @Bind(R.id.menu_jump_into_rivet_frog_icon)
    ImageView frogIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_jump_into_rivet);
        ButterKnife.bind(this);
        frogIcon.setOnTouchListener(new View.OnTouchListener() {
            Handler handler = new Handler();
            int numberOfTaps = 0;
            long lastTapTimeMs = 0;
            long touchDownMs = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownMs = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacksAndMessages(null);
                        if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
                            numberOfTaps = 0;
                            lastTapTimeMs = 0;
                            break;
                        }
                        if (numberOfTaps > 0
                                && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                            numberOfTaps += 1;
                        } else {
                            numberOfTaps = 1;
                        }
                        lastTapTimeMs = System.currentTimeMillis();
                        if (numberOfTaps == 3) {
                            startActivity(new Intent(JumpIntoRivetActivity.this, SpecialThanksActivity.class));
                        }
                }
                return true;
            }
        });
    }

    @OnClick(R.id.menu_jump_into_rivet_back_button)
    public void backButtonTapped() {
        finish();
    }
}
