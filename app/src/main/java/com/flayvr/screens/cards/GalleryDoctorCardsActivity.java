package com.flayvr.screens.cards;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.utils.SharedPreferencesManager;
import com.flayvr.myrollshared.views.RateUsPopup;
import com.flayvr.screens.bad.GalleryDoctorBadPhotosActivity;
import com.flayvr.screens.duplicates.GalleryDoctorDuplicatePhotosActivity;
import com.flayvr.screens.review.PhotosForReviewActivity;
import com.flayvr.screens.screenshots.ScreenshotsActivity;
import com.flayvr.screens.videos.LongVideosActivity;
import com.flayvr.screens.whatsapp.WhatsappReviewActivity;
import com.flayvr.util.GalleryDoctorDefaultActivity;
import com.flayvr.util.GalleryDoctorStatsUtils;
import java.util.HashMap;

public class GalleryDoctorCardsActivity extends GalleryDoctorDefaultActivity
    implements GalleryDoctorCardsFragment.GalleryDoctorCardsFragmentListener
{
    public enum CARDS_TYPE {
        BAD("BAD", 0, "bad"),
        DUPLICATE("DUPLICATE", 1, "duplicates"),
        FOR_REVIEW("FOR_REVIEW", 2, "for review"),
        VIDEOS("VIDEOS", 3, "whatsapp photos"),
        WHATSAPP("WHATSAPP", 4, "screenshots"),
        SCREENSHOTS("SCREENSHOTS", 5, "long videos");
        private final String str;

        public String toString() {
            return str;
        }

        CARDS_TYPE(String s, int i, String s1) {
            str = s1;
        }
    }
    public static final String CARD_SELECTED = "CARD_SELECTED";
    public static final String ITEMS_SOURCE = "ITEMS_SOURCE";
    public static final int SCREEN_REQUEST_CODE = 1000;
    public static final String USER_DID_ACTION_ANALYTICS_EXPLANATION_EXTRA = "USER_DID_ACTION_ANALYTICS_EXPLANATION_EXTRA";
    public static final String USER_DID_ACTION_EXTRA = "USER_DID_ACTION_EXTRA";
    public static final String USER_NUMBER_OF_PHOTOS_DELETED = "USER_NUMBER_OF_PHOTOS_DELETED";
    public static final String USER_SIZE_OF_PHOTOS_DELETED = "USER_SIZE_OF_PHOTOS_DELETED";
    private CARDS_TYPE card;
    private int firstHealth;
    private GalleryDoctorCardsFragment fragment;
    private int source;

    public GalleryDoctorCardsActivity()
    {
    }

    private void showRateUsPopup(String s, HashMap hashmap)
    {
        AnalyticsUtils.trackEventWithKISS("received rate us popup", hashmap, true);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        RateUsPopup rateuspopup = (RateUsPopup)((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.gallery_doctor_rate_us_popup, null);
        builder.setView(rateuspopup);
        AlertDialog alertdialog = builder.create();
        rateuspopup.setDialog(alertdialog);
        rateuspopup.setReason(s);
        alertdialog.show();
        SharedPreferencesManager.setRateUsPopupShown();
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        int k;
        long l;
        String s;
        HashMap hashmap;
label0:
        {
            super.onActivityResult(i, j, intent);
            if(i == 1000 && intent != null)
            {
                boolean flag = intent.getBooleanExtra("USER_DID_ACTION_EXTRA", false);
                k = intent.getIntExtra("USER_NUMBER_OF_PHOTOS_DELETED", 0);
                l = intent.getLongExtra("USER_SIZE_OF_PHOTOS_DELETED", 0L);
                s = intent.getStringExtra("USER_DID_ACTION_ANALYTICS_EXPLANATION_EXTRA");
                if(flag && k > 1)
                {
                    hashmap = new HashMap();
                    hashmap.put("share accomplishment reason", s);
                    if(PreferencesManager.getNumberOfTimesAccomplishmentShown() != 1 || SharedPreferencesManager.getNeverShowRateUsPopup() || SharedPreferencesManager.getRemindRateUsPopup() || SharedPreferencesManager.getRateUsPopupShown() || SharedPreferencesManager.getRateUsPopupDone() || SharedPreferencesManager.getRateUsPopupSentFeedback())
                    {
                        break label0;
                    }
                    showRateUsPopup(s, hashmap);
                }
            }
            return;
        }
        AnalyticsUtils.trackEventWithKISS("received share accomplishment popup", hashmap, true);
        int i1 = GalleryDoctorStatsUtils.getGalleryHealth(GalleryDoctorStatsUtils.getMediaItemStat(source), GalleryDoctorStatsUtils.getDriveStat(source));
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        AccomplishmentDialog accomplishmentdialog = (AccomplishmentDialog)((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.gallery_doctor_share_accomplishment_dialog, null);
        accomplishmentdialog.setStartHealth(firstHealth);
        accomplishmentdialog.setEndHealth(i1);
        accomplishmentdialog.setNumberOfPhotos(k);
        accomplishmentdialog.setSizeOfPhotos(l);
        builder.setView(accomplishmentdialog);
        AlertDialog alertdialog = builder.create();
        accomplishmentdialog.setDialog(alertdialog);
        accomplishmentdialog.setReason(s);
        firstHealth = i1;
        accomplishmentdialog.setSubheader(k, l);
        alertdialog.show();
        accomplishmentdialog.animateHealthAndBar();
        PreferencesManager.setNumberOfTimesAccomplishmentShown(1 + PreferencesManager.getNumberOfTimesAccomplishmentShown());
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(getResources().getBoolean(R.bool.gd_dashboard_portrait_only))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(bundle == null)
        {
            bundle = getIntent().getExtras();
        }
        source = bundle.getInt("ITEMS_SOURCE");
        card = (CARDS_TYPE)bundle.get("CARD_SELECTED");
        firstHealth = GalleryDoctorStatsUtils.getGalleryHealth(GalleryDoctorStatsUtils.getMediaItemStat(source), GalleryDoctorStatsUtils.getDriveStat(source));
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment = GalleryDoctorCardsFragment.newInstance(source);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        AnalyticsUtils.trackEventWithKISS("viewed gd cards");
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch(menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case android.R.id.home: 
            onBackPressed();
            break;
        }
        return true;
    }

    protected void onStart()
    {
        super.onStart();
        if(card != null)
        {
            fragment.scrollTo(card);
            card = null;
        }
    }

    public void openBad(int i)
    {
        Intent intent = new Intent(this, GalleryDoctorBadPhotosActivity.class);
        intent.putExtra("ITEMS_SOURCE", i);
        startActivityForResult(intent, 1000);
    }

    public void openDuplicate(int i)
    {
        Intent intent = new Intent(this, GalleryDoctorDuplicatePhotosActivity.class);
        intent.putExtra("ITEMS_SOURCE", i);
        startActivityForResult(intent, 1000);
    }

    public void openForReview(int i)
    {
        Intent intent = new Intent(this, PhotosForReviewActivity.class);
        intent.putExtra("ITEMS_SOURCE", i);
        startActivityForResult(intent, 1000);
    }

    public void openScreenshots(int i)
    {
        Intent intent = new Intent(this, ScreenshotsActivity.class);
        intent.putExtra("ITEMS_SOURCE", i);
        startActivityForResult(intent, 1000);
    }

    public void openVideos(int i)
    {
        Intent intent = new Intent(this, LongVideosActivity.class);
        intent.putExtra("ITEMS_SOURCE", i);
        startActivityForResult(intent, 1000);
    }

    public void openWhatsapp(int i)
    {
        Intent intent = new Intent(this, WhatsappReviewActivity.class);
        intent.putExtra("ITEMS_SOURCE", i);
        startActivityForResult(intent, 1000);
    }
}
