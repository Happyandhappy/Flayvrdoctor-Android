package com.flayvr.screens.duplicates;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.MediaItem;
import com.flayvr.views.GDEditMediaItemView;

public class DuplicateEditMediaItemView extends GDEditMediaItemView
{

    public DuplicateEditMediaItemView(Context context)
    {
        super(context);
    }

    protected int getLayout()
    {
        return R.layout.edit_item_view_duplicate;
    }

    public void setIsBest(boolean flag)
    {
        View view = findViewById(R.id.selection_box);
        View view1 = findViewById(R.id.isbest);
        View view2 = findViewById(R.id.bottombar);
        if(flag)
        {
            view1.setVisibility(View.VISIBLE);
            view2.setBackgroundColor(getResources().getColor(R.color.black_overlay));
            view.setBackgroundColor(getResources().getColor(R.color.black_overlay));
            return;
        } else
        {
            view1.setVisibility(View.INVISIBLE);
            view2.setBackgroundColor(getResources().getColor(R.color.transparent));
            view.setBackgroundColor(getResources().getColor(R.color.transparent));
            return;
        }
    }

    public void setItem(MediaItem mediaitem)
    {
        super.setItem(mediaitem);
    }
}
