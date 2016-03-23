package com.jumpintorivet.rivet.activities.menu;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.jumpintorivet.rivet.BuildConfig;
import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.utils.RivetPreferences;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuActivity extends Activity {
    @Bind(R.id.menu_activity_reset_tutorial_row)
    LinearLayout resetTutorialRow;
    @Bind(R.id.menu_activity_reset_tutorial_row_divider)
    View resetTutorialRowDivider;
    @Inject
    RivetPreferences rivetPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        ((MyApplication) getApplication()).inject(this);
        ButterKnife.bind(this);
        if (BuildConfig.FLAVOR.equals("production")) {
            resetTutorialRow.setVisibility(View.GONE);
            resetTutorialRowDivider.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.menu_activity_toolbar_exit_button)
    public void closeMenu() {
        finish();
    }

    @OnClick(R.id.menu_activity_reset_tutorial_row)
    public void resetTutorial() {
        rivetPreferences.setHasSlidPrivacyMaskLeft(false);
        closeMenu();
    }

    @OnClick(R.id.menu_activity_share_with_friends)
    public void shareWithFriends() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_with_friends_text));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @OnClick(R.id.menu_activity_contact_us_support)
    public void contactUsSupport() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jumpintorivet.com/support"));
        startActivity(browserIntent);
    }

    @OnClick(R.id.menu_activity_rate_on_the_play_store)
    public void rateOnThePlayStore() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @OnClick(R.id.menu_activity_like_on_facebook)
    public void likeOnFacebook() {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/431429777004747")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Rivet-431429777004747/timeline/")));
        }
    }

    @OnClick(R.id.menu_activity_follow_on_twitter)
    public void followOnTwitter() {
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=TheRivetApp")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/TheRivetApp")));
        }
    }

    @OnClick(R.id.menu_activity_follow_on_instagram)
    public void followOnInstagram() {
        try {
            Uri uri = Uri.parse("http://instagram.com/_u/therivetapp");
            Intent instagram = new Intent(Intent.ACTION_VIEW, uri);
            instagram.setPackage("com.instagram.android");
            startActivity(instagram);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/therivetapp")));
        }
    }

    @OnClick(R.id.menu_activity_privacy_policy_row)
    public void openPrivacyPolicy() {
        startActivity(new Intent(this, PrivacyPolicyActivity.class));
    }

    @OnClick(R.id.menu_activity_terms_and_conditions)
    public void openTermsAndConditions() {
        startActivity(new Intent(this, TermsAndConditionsActivity.class));
    }

    @OnClick(R.id.menu_activity_jump_into_rivet)
    public void openJumpIntoRivet() {
        startActivity(new Intent(this, JumpIntoRivetActivity.class));
    }
}
