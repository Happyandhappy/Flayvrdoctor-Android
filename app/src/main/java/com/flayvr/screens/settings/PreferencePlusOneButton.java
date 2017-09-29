package com.flayvr.screens.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.google.android.gms.plus.PlusOneButton;

public class PreferencePlusOneButton extends Preference
{

    private static final int PLUS_ONE_REQUEST_CODE = 0;
    private static final String TAG = "preference_plus_one_button";
    private PlusOneButton mPlusOneButton;
    private View view;

    public PreferencePlusOneButton(Context context)
    {
        super(context);
    }

    public PreferencePlusOneButton(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public PreferencePlusOneButton(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public void initPlusOneButton()
    {
        if(mPlusOneButton != null)
        {
            mPlusOneButton.initialize("https://play.google.com/store/apps/details?id=com.flayvr.doctor", 0);
        }
    }

    protected View onCreateView(final ViewGroup parent)
    {
        if(view == null)
        {
            view = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.google_plus_one_button, parent, false);
            mPlusOneButton = (PlusOneButton)view.findViewById(R.id.plus_one_button);
            mPlusOneButton.setOnPlusOneClickListener(new PlusOneButton.OnPlusOneClickListener() {
                @Override
                public void onPlusOneClick(Intent intent) {
                    Activity activity = (Activity)parent.getContext();
                    if(intent != null)
                    {
                        activity.startActivityForResult(intent, 0);
                    }
                    AnalyticsUtils.trackEventWithKISS("chose to plus one from settings");
                }
            });
            initPlusOneButton();
        }
        return view;
    }
}
