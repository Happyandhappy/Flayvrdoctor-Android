package com.flayvr.screens.cards;

import android.app.AlertDialog;
import android.content.*;
import android.content.pm.*;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.screens.dashboard.DialChartView;
import com.flayvr.screens.dashboard.GalleryDoctorDashboardFragment;
import com.flayvr.util.GalleryDoctorUtils;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.HashMap;

public class AccomplishmentDialog extends LinearLayout
{

    private AlertDialog dialog;
    private int endHealth;
    private int numberOfPhotos;
    private String reason;
    private long sizeOfPhotos;
    private int startHealth;

    public AccomplishmentDialog(Context context)
    {
        super(context);
    }

    public AccomplishmentDialog(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public AccomplishmentDialog(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public void animateHealthAndBar()
    {
        ValueAnimator valueanimator = ValueAnimator.ofFloat(new float[] {
            0.0F, 1.0F
        });
        View view = findViewById(R.id.health_dial);
        final DialChartView dial = (DialChartView)view.findViewById(R.id.dial);
        final TextView textview = (TextView)view.findViewById(R.id.gallery_health);
        final TextView textview1 = (TextView)view.findViewById(R.id.gallery_health_rank);
        final float f = (float)startHealth / 100F;
        valueanimator.addUpdateListener(new com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener(){
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dial.setPercentage(animation.getAnimatedFraction() * (f - startHealth) + startHealth);
                textview.setText((new StringBuilder()).append((int)((float)(endHealth - startHealth) * animation.getAnimatedFraction() + (float)startHealth)).append("%").toString());
                textview1.setText(GalleryDoctorDashboardFragment.getRankString((int)((float)(endHealth - startHealth) * animation.getAnimatedFraction() + (float)startHealth)));
            }
        });
        valueanimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueanimator.setDuration(1000L);
        valueanimator.start();
    }

    public AlertDialog getDialog()
    {
        return dialog;
    }

    public int getEndHealth()
    {
        return endHealth;
    }

    public int getNumberOfPhotos()
    {
        return numberOfPhotos;
    }

    public long getSizeOfPhotos()
    {
        return sizeOfPhotos;
    }

    public int getStartHealth()
    {
        return startHealth;
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        boolean flag = true;
        View view = findViewById(R.id.facebook);
        View view1;
        final ResolveInfo twitterInfo;
        if(view != null)
        {
            final ResolveInfo facebookInfo = GalleryDoctorUtils.findFacebookClient(getContext().getPackageManager());
            if(facebookInfo != null)
            {
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        HashMap hashmap = new HashMap();
                        hashmap.put("share accomplishment reason", reason);
                        startFromResolveInfo(facebookInfo);
                    }
                });
                flag = false;
            } else
            {
                ((ViewGroup)view.getParent()).removeView(view);
            }
        }
        twitterInfo = GalleryDoctorUtils.findTwitterClient(getContext().getPackageManager());
        view1 = findViewById(R.id.twitter);
        if(view1 != null)
        {
            if(twitterInfo != null)
            {
                view1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        HashMap hashmap = new HashMap();
                        hashmap.put("share accomplishment reason", reason);
                        startFromResolveInfo(twitterInfo);
                    }
                });
                flag = false;
            } else {
                ((ViewGroup)view1.getParent()).removeView(view1);
            }
        }
        if(flag)
        {
            ((TextView)findViewById(R.id.action_text)).setText(getResources().getString(R.string.share_menu_item_label));
        }
        findViewById(R.id.other).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                HashMap hashmap = new HashMap();
                hashmap.put("share accomplishment reason", reason);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", getResources().getText(R.string.gallery_doctor_invite_friend_subject));
                String s = getResources().getString(R.string.gallery_doctor_invite_friend_body_after_action);
                Object aobj[] = new Object[2];
                aobj[0] = Integer.valueOf(numberOfPhotos);
                aobj[1] = "goo.gl/Y5mnml";
                intent.putExtra("android.intent.extra.TEXT", String.format(s, aobj));
                getContext().startActivity(intent);
            }
        });
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(dialog != null)
                {
                    HashMap hashmap = new HashMap();
                    hashmap.put("share accomplishment reason", reason);
                    dialog.dismiss();
                }
            }
        });
    }

    public void setDialog(AlertDialog alertdialog)
    {
        dialog = alertdialog;
    }

    public void setEndHealth(int i)
    {
        endHealth = i;
    }

    public void setNumberOfPhotos(int i)
    {
        numberOfPhotos = i;
    }

    public void setReason(String s)
    {
        reason = s;
    }

    public void setSizeOfPhotos(long l)
    {
        sizeOfPhotos = l;
    }

    public void setStartHealth(int i)
    {
        startHealth = i;
    }

    void setSubheader(int i, long l)
    {
        numberOfPhotos = i;
        sizeOfPhotos = l;
        TextView textview = (TextView)findViewById(R.id.subheader);
        String s = getResources().getString(R.string.gallery_doctor_congrats_text);
        Object aobj[] = new Object[2];
        aobj[0] = GeneralUtils.humanReadableNumber(i);
        aobj[1] = GeneralUtils.humanReadableByteCountIntro(l);
        textview.setText(String.format(s, aobj));
    }

    void startFromResolveInfo(ResolveInfo resolveinfo)
    {
        ActivityInfo activityinfo = resolveinfo.activityInfo;
        ComponentName componentname = new ComponentName(activityinfo.applicationInfo.packageName, activityinfo.name);
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setComponent(componentname);
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", getResources().getText(R.string.gallery_doctor_invite_friend_subject));
        String s = getResources().getString(R.string.gallery_doctor_invite_friend_body_after_action);
        Object aobj[] = new Object[2];
        aobj[0] = Integer.valueOf(numberOfPhotos);
        aobj[1] = "goo.gl/Y5mnml";
        intent.putExtra("android.intent.extra.TEXT", String.format(s, aobj));
        getContext().startActivity(intent);
    }
}
