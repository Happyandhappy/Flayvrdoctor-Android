package com.flayvr.myrollshared.views;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.utils.SharedPreferencesManager;

import java.util.HashMap;

public abstract class RateUsPopup extends LinearLayout
{
    private AlertDialog dialog;
    private String reason;

    public RateUsPopup(Context context)
    {
        super(context);
    }

    public RateUsPopup(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public RateUsPopup(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    protected abstract TextView getAction();

    public AlertDialog getDialog()
    {
        return dialog;
    }

    protected abstract TextView getResultText();

    protected abstract LinearLayout getStars();

    protected abstract Toolbar getToolbar();

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        final TextView action = getAction();
        final TextView resultText = getResultText();
        final LinearLayout stars = getStars();
        for(int i = 0; i < stars.getChildCount(); i++)
        {
            final int index = i;
            ((ImageView)stars.getChildAt(index)).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view)
                {
                    int i = 0;
                    while(i < stars.getChildCount())
                    {
                        ImageView imageview = (ImageView)stars.getChildAt(i);
                        if(i <= index)
                        {
                            imageview.setImageDrawable(getResources().getDrawable(R.drawable.rate_star_full));
                        } else
                        {
                            imageview.setImageDrawable(getResources().getDrawable(R.drawable.rate_star_stroke));
                        }
                        imageview.setOnClickListener(null);
                        i++;
                    }
                    action.setVisibility(View.VISIBLE);
                    resultText.setVisibility(View.VISIBLE);
                    if(index >= 3)
                    {
                        HashMap hashmap = new HashMap();
                        hashmap.put("rate us stars given", (new StringBuilder()).append(1 + index).append("").toString());
                        AnalyticsUtils.trackEventWithKISS("rated 4-5 stars", hashmap, true);
                        resultText.setText(getContext().getString(R.string.rate_us_awesome));
                        action.setText(getContext().getString(R.string.rate_us_on_google_play));
                        action.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view1) {
                                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse((new StringBuilder()).append("market://details?id=").append(getContext().getPackageName()).toString()));
                                HashMap hashmap2;
                                try
                                {
                                    getContext().startActivity(intent);
                                }
                                catch(ActivityNotFoundException activitynotfoundexception)
                                {
                                    getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse((new StringBuilder()).append("http://play.google.com/store/apps/details?id=").append(getContext().getPackageName()).toString())));
                                }
                                hashmap2 = new HashMap();
                                hashmap2.put("rate us reason", reason);
                                AnalyticsUtils.trackEventWithKISS("rated us on google play on rate us popup", hashmap2, true);
                                SharedPreferencesManager.setRateUsPopupDone();
                                dialog.dismiss();
                            }
                        });
                    } else {
                        HashMap hashmap1 = new HashMap();
                        hashmap1.put("rate us stars given", (new StringBuilder()).append(1 + index).append("").toString());
                        AnalyticsUtils.trackEventWithKISS("rated 1-3 stars", hashmap1, true);
                        resultText.setText(getContext().getString(R.string.rate_us_darn_it));
                        action.setText(getContext().getString(R.string.send_us_feedback));
                        action.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view1)
                            {
                                HashMap hashmap2 = new HashMap();
                                hashmap2.put("rate us reason", reason);
                                AnalyticsUtils.trackEventWithKISS("sent feedback on rate us popup", hashmap2, true);
                                sendFeedback();
                                SharedPreferencesManager.setRateUsPopupSentFeedback();
                            }
                        });
                    }
                }
            });
        }

        Toolbar toolbar = getToolbar();
        toolbar.inflateMenu(R.menu.rate_us_popup_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuitem)
            {
                if(menuitem.getItemId() == R.id.remind) {
                    SharedPreferencesManager.setRemindRateUsPopup();
                    HashMap hashmap1 = new HashMap();
                    hashmap1.put("rate us reason", reason);
                    AnalyticsUtils.trackEventWithKISS("chose to postpone rate us popup", hashmap1, true);
                    dialog.dismiss();
                } else if(menuitem.getItemId() == R.id.never) {
                    SharedPreferencesManager.setNeverShowRateUsPopup();
                    HashMap hashmap = new HashMap();
                    hashmap.put("rate us reason", reason);
                    AnalyticsUtils.trackEventWithKISS("chose to not show again rate us popup", hashmap, true);
                    dialog.dismiss();
                }
                return false;
            }
        });
    }

    protected abstract void sendFeedback();

    public void setDialog(AlertDialog alertdialog)
    {
        dialog = alertdialog;
    }

    public void setReason(String s)
    {
        reason = s;
    }
}
