package com.flayvr.myrollshared.views.itemview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.flayvr.doctor.R;
import com.nineoldandroids.view.ViewHelper;

public class EditMediaItemView extends MediaItemView
{

    protected boolean selected;
    private Drawable selectedDrawable;
    protected ImageView selection;
    private Drawable unselectedDrawable;

    public EditMediaItemView(Context context, Drawable drawable, Drawable drawable1)
    {
        super(context);
        selected = true;
        selectedDrawable = drawable;
        unselectedDrawable = drawable1;
    }

    protected int getLayout()
    {
        return R.layout.edit_item_view;
    }

    protected void init(Context context)
    {
        super.init(context);
        selection = (ImageView)view.findViewById(R.id.selection_frame);
        ViewHelper.setScaleX(videoFrame, 0.7F);
        ViewHelper.setScaleY(videoFrame, 0.7F);
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setIsSelected(boolean flag)
    {
        selected = flag;
        if(flag)
        {
            selection.setImageDrawable(selectedDrawable);
            return;
        } else
        {
            selection.setImageDrawable(unselectedDrawable);
            return;
        }
    }

    public void toggleSelect()
    {
        boolean flag;
        if(!selected)
            flag = true;
        else
            flag = false;
        setIsSelected(flag);
    }
}
