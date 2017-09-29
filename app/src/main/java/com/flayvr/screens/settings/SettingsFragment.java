package com.flayvr.screens.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;
import android.support.v4.app.Fragment;
import android.support.v4.preference.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.util.GalleryDoctorUtils;

public class SettingsFragment extends android.support.v4.preference.PreferenceFragment
{

    private com.flayvr.screens.register.CloudSignInFragment.CloudSignInFragmentListener listener;
    private Preference picasaLogin;

    public SettingsFragment()
    {
    }

    public static Uri getAppLink(Context context)
    {
        return Uri.parse((new StringBuilder()).append("market://details?id=").append(context.getPackageName()).toString());
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        listener = (com.flayvr.screens.register.CloudSignInFragment.CloudSignInFragmentListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        addPreferencesFromResource(R.layout.gallery_doctor_preferences);
        final android.support.v4.app.FragmentActivity currentContext = (android.support.v4.app.FragmentActivity)getActivity();
        CheckBoxPreference checkboxpreference = (CheckBoxPreference)getPreferenceManager().findPreference("bad_photos_notif");
        checkboxpreference.setChecked(PreferencesManager.getBadPhotosNotification());
        checkboxpreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean boolean1 = (Boolean)o;
                PreferencesManager.setBadPhotosNotification(boolean1.booleanValue());
                if(boolean1.booleanValue())
                    AnalyticsUtils.trackEventWithKISS("turned on bad photos identified notification");
                else
                    AnalyticsUtils.trackEventWithKISS("turned off bad photos identified notification");
                return true;
            }
        });
        CheckBoxPreference checkboxpreference1 = (CheckBoxPreference)getPreferenceManager().findPreference("similar_photos_notif");
        checkboxpreference1.setChecked(PreferencesManager.getDuplicatePhotosNotification());
        checkboxpreference1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean boolean1 = (Boolean)o;
                PreferencesManager.setDuplicatePhotosNotification(boolean1.booleanValue());
                if(boolean1.booleanValue())
                {
                    AnalyticsUtils.trackEventWithKISS("turned on similar photos identified notification");
                } else
                {
                    AnalyticsUtils.trackEventWithKISS("turned off similar photos identified notification");
                }
                return true;
            }
        });
        CheckBoxPreference checkboxpreference2 = (CheckBoxPreference)getPreferenceManager().findPreference("large_number_of_photos_notif");
        checkboxpreference2.setChecked(PreferencesManager.getLargeNumberOfPhotosNotification());
        checkboxpreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean boolean1 = (Boolean)o;
                PreferencesManager.setLargeNumberOfPhotosNotification(boolean1.booleanValue());
                if(boolean1.booleanValue())
                    AnalyticsUtils.trackEventWithKISS("turned on large number of photos notification");
                else
                    AnalyticsUtils.trackEventWithKISS("turned off large number of photos notification");
                return true;
            }
        });
        CheckBoxPreference checkboxpreference3 = (CheckBoxPreference)getPreferenceManager().findPreference("weekend_notif");
        checkboxpreference3.setChecked(PreferencesManager.getWeekendNotification());
        checkboxpreference3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean boolean1 = (Boolean)o;
                PreferencesManager.setWeekendNotification(boolean1.booleanValue());
                if(boolean1.booleanValue())
                {
                    AnalyticsUtils.trackEventWithKISS("turned on weekend cleanup time notification");
                } else
                {
                    AnalyticsUtils.trackEventWithKISS("turned off weekend cleanup time notification");
                }
                return true;
            }
        });
        CheckBoxPreference checkboxpreference4 = (CheckBoxPreference)getPreferenceManager().findPreference("low_space_notif");
        checkboxpreference4.setChecked(PreferencesManager.getLowDiskPhotosNotification());
        checkboxpreference4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Boolean boolean1 = (Boolean)o;
                PreferencesManager.setLowDiskPhotosNotification(boolean1.booleanValue());
                if(boolean1.booleanValue())
                {
                    AnalyticsUtils.trackEventWithKISS("turned on low space notification");
                } else
                {
                    AnalyticsUtils.trackEventWithKISS("turned off low space notification");
                }
                return true;
            }
        });
        getPreferenceManager().findPreference("rate_us_preference").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AnalyticsUtils.trackEventWithKISS("chose to rate from settings");
                Intent intent = new Intent("android.intent.action.VIEW", SettingsFragment.getAppLink(currentContext));
                try
                {
                    startActivity(intent);
                }
                catch(ActivityNotFoundException activitynotfoundexception) { }
                return true;
            }
        });
        getPreferenceManager().findPreference("facebook_like").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AnalyticsUtils.trackEventWithKISS("chose to like us on facebook from settings");
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/myrollgallery"));
                startActivity(intent);
                return true;
            }
        });
        getPreferenceManager().findPreference("twitter_follow").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AnalyticsUtils.trackEventWithKISS("chose to follow us on twitter from settings");
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/myrollgallery"));
                startActivity(intent);
                return true;
            }
        });
        getPreferenceManager().findPreference("faq").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SettingsHelpActivity.class);
                startActivity(intent);
                return true;
            }
        });
        getPreferenceManager().findPreference("send_feedback").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return GalleryDoctorUtils.sendFeedback(getActivity());
            }
        });
        getPreferenceManager().findPreference("invite_friend").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", getText(R.string.gallery_doctor_invite_friend_subject));
                intent.putExtra("android.intent.extra.TEXT", String.format(getString(R.string.gallery_doctor_invite_friend_body), new Object[] {
                        "goo.gl/3JcR9a"
                }));
                try
                {
                    startActivity(Intent.createChooser(intent, getString(R.string.invite_a_friend_chooser_title)));
                }
                catch(ActivityNotFoundException activitynotfoundexception)
                {
                    Toast.makeText(getActivity(), getString(R.string.send_mail_chooser_error), Toast.LENGTH_SHORT).show();
                }
                AnalyticsUtils.trackEventWithKISS("chose to invite a friend from settings");
                return true;
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        ((PreferencePlusOneButton)findPreference("plus_one_button")).initPlusOneButton();
    }

    public void picasaStateChange(boolean flag)
    {
        if(flag)
        {
            Preference preference = picasaLogin;
            String s = getResources().getString(R.string.settings_picasa_login);
            Object aobj[] = new Object[1];
            aobj[0] = PicasaSessionManager.getInstance().getUserId();
            preference.setSummary(String.format(s, aobj));
        } else {
            picasaLogin.setSummary(R.string.settings_picasa_logout);
        }
    }

}
