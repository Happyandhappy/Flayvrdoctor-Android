package com.flayvr.screens.register;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.events.PicasaLoginEvent;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.utils.IntentActions;
import com.flayvr.util.GalleryDoctorDefaultActivity;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

public abstract class AbstractCloudSignInActivity extends GalleryDoctorDefaultActivity
{

    private static final String TAG = AbstractCloudSignInActivity.class.getSimpleName();
    private final int PICK_PICASA_ACCOUNT_REQUEST = 1;
    private final int REQUEST_PICASA_AUTHENTICATE = 2;
    private String account;

    public AbstractCloudSignInActivity()
    {
    }

    public void dropboxLogin()
    {
    }

    protected abstract String getSource();

    protected void onActivityResult(int i, int j, Intent intent)
    {
        switch (i) {
            default:
                break;
            case 1:
                if(j == -1)
                {
                    account = intent.getStringExtra("authAccount");
                    setToken();
                }
                break;
            case 2:
                setToken();
                break;
        }
    }

    public void picasaLogin()
    {
        startActivityForResult(AccountPicker.newChooseAccountIntent(null, null, new String[] {
            "com.google"
        }, false, null, null, null, null), 1);
        HashMap hashmap = new HashMap();
        hashmap.put("picasa connection source", getSource());
        AnalyticsUtils.trackEventWithKISS("chose to connect to picasa", hashmap);
    }

    protected void setToken()
    {
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                String s;
                Intent intent1;
                HashMap hashmap;
                try
                {
                    s = GoogleAuthUtil.getToken(AbstractCloudSignInActivity.this, account, "lh2", null);
                    if(s == null)
                        return;
                    PicasaSessionManager.getInstance().login(account, s);
                    EventBus.getDefault().post(new PicasaLoginEvent());
                    intent1 = new Intent(IntentActions.ACTION_PICASA_SESSIOM_CHANGED);
                    FlayvrApplication.getAppContext().sendBroadcast(intent1);
                    hashmap = new HashMap();
                    hashmap.put("picasa connection source", getSource());
                    AnalyticsUtils.trackEventWithKISS("connected to picasa", hashmap);
                }
                catch(GooglePlayServicesAvailabilityException googleplayservicesavailabilityexception)
                {
                    GooglePlayServicesUtil.getErrorDialog(googleplayservicesavailabilityexception.getConnectionStatusCode(), AbstractCloudSignInActivity.this, 2).show();
                }
                catch(UserRecoverableAuthException userrecoverableauthexception)
                {
                    Intent intent = userrecoverableauthexception.getIntent();
                    startActivityForResult(intent, 2);
                }
                catch(GoogleAuthException googleauthexception)
                {
                    Log.e(AbstractCloudSignInActivity.TAG, (new StringBuilder()).append("Unrecoverable authentication exception: ").append(googleauthexception.getMessage()).toString(), googleauthexception);
                    Toast.makeText(AbstractCloudSignInActivity.this, getString(R.string.picasa_login_error), Toast.LENGTH_LONG).show();
                }
                catch(IOException ioexception)
                {
                    Log.i(AbstractCloudSignInActivity.TAG, (new StringBuilder()).append("transient error encountered: ").append(ioexception.getMessage()).toString());
                    Toast.makeText(AbstractCloudSignInActivity.this, getString(R.string.picasa_login_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void skipRegister()
    {
    }
}
