package com.flayvr.views;

import android.content.Context;
import android.content.res.Resources;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.views.itemview.EditMediaItemView;

public class GDEditMediaItemView extends EditMediaItemView
{

    public GDEditMediaItemView(Context context)
    {
        super(context, context.getResources().getDrawable(R.drawable.gd_radio_on_white), context.getResources().getDrawable(R.drawable.gd_radio_off_white));
    }

    protected int getLayout()
    {
        return R.layout.gallery_doctor_edit_item_view;
    }
}
