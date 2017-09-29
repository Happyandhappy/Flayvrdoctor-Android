package com.flayvr.screens.dashboard;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.*;
import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.events.PicasaLoginEvent;
import com.flayvr.myrollshared.events.PicasaLogoutEvent;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.screens.intro.GDIntroActivity;
import com.flayvr.screens.register.CloudSignInActivity;
import com.flayvr.screens.settings.ScanningFolderCustomizeFragment;
import com.flayvr.screens.settings.SettingsActivity;
import com.flayvr.util.GalleryDoctorDefaultActivity;
import de.greenrobot.event.EventBus;
import java.util.HashMap;
import java.util.Set;

public class GalleryDoctorDashboardActivity extends GalleryDoctorDefaultActivity
    implements com.flayvr.myrollshared.fragments.FoldersCustomizeFragment.FoldersCustomizeFragmentListener, GalleryDoctorDashboardFragment.GalleryDoctorDashboardFragmentListener
{

    private static final int INTRO_RESULT = 3000;
    public static final int LOGIN_RESULT = 3001;
    public static final String NOTIFICATION_SOURCE = "NOTIFICATION_SOURCE";
    private GalleryDoctorDashboardFragment fragment;

    public GalleryDoctorDashboardActivity()
    {
    }

    private void setScreenTitle(int i)
    {
        if(i == 2)
        {
            setTitle(R.string.dashboard_title_cloud);
            return;
        }
        if(PicasaSessionManager.getInstance().hasUser())
        {
            setTitle(R.string.dashboard_title_local_login);
            return;
        } else
        {
            setTitle(R.string.dashboard_title_local);
            return;
        }
    }

    protected void initFragment(int i)
    {
        setScreenTitle(i);
        fragment = GalleryDoctorDashboardFragment.newInstance(i);
        FragmentTransaction fragmenttransaction = getSupportFragmentManager().beginTransaction();
        fragmenttransaction.replace(android.R.id.content, fragment);
        fragmenttransaction.commitAllowingStateLoss();
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        if(i == LOGIN_RESULT && PicasaSessionManager.getInstance().hasUser())
        {
            initFragment(2);
        }
        super.onActivityResult(i, j, intent);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(getResources().getBoolean(R.bool.gd_dashboard_portrait_only))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(!PreferencesManager.isFirstSessionHandled())
        {
            startActivityForResult(new Intent(getBaseContext(), GDIntroActivity.class), INTRO_RESULT);
            PreferencesManager.setFirstSessionHandled();
        }
        getSupportActionBar().setElevation(0.0F);
        HashMap hashmap = new HashMap();
        String s;
        String s1;
        if(GalleryDoctorServicesProgress.didClassifierServiceFinish(1) && GalleryDoctorServicesProgress.didCVServiceFinish(1) && GalleryDoctorServicesProgress.didDuplicatesServiceFinish(1))
            s = "scanning is done";
        else
            s = "scanning is running";
        hashmap.put("dashboard status", s);
        if(GalleryDoctorServicesProgress.didClassifierServiceFinish(2) && GalleryDoctorServicesProgress.didCVServiceFinish(2) && GalleryDoctorServicesProgress.didDuplicatesServiceFinish(2))
            s1 = "scanning is done picasa";
        else
            s1 = "scanning is running picasa";
        hashmap.put("dashboard status picasa", s1);
        AnalyticsUtils.trackEventWithKISS("viewed dashboard", hashmap, true);
        initFragment(1);
        if(getIntent() != null && getIntent().hasExtra("NOTIFICATION_SOURCE"))
        {
            AnalyticsUtils.trackEventWithKISS(String.format("opened %s notification", new Object[] {
                getIntent().getStringExtra("NOTIFICATION_SOURCE")
            }));
            AnalyticsUtils.trackEventWithKISS("opened notification");
        }
        EventBus.getDefault().register(this);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.gallery_doctor_dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PicasaLoginEvent picasaloginevent)
    {
        if(fragment != null)
            fragment.updateLoginStatus();
    }

    public void onEvent(PicasaLogoutEvent picasalogoutevent)
    {
        if(fragment != null)
        {
            if(fragment.getSource() != 2)
                fragment.updateLoginStatus();
            else
                initFragment(1);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        switch(menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);
        case android.R.id.home:
            onBackPressed();
            return true;
        case R.id.settings:
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
            return true;
        case R.id.customize_folders:
            (new ScanningFolderCustomizeFragment()).show(getSupportFragmentManager(), "custom_folders");
            return true;
        }
    }

    public void refreshAllFolder(Set set)
    {
        fragment.updateFragment();
    }

    public void toggleSource(int i)
    {
        int j = 1;
        if(i == j)
            j = 2;
        if(j == 2 && !PicasaSessionManager.getInstance().hasUser())
            startActivityForResult(new Intent(getBaseContext(), CloudSignInActivity.class), LOGIN_RESULT);
        else
            initFragment(j);
    }
}
