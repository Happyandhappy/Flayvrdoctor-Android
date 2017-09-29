package com.flayvr.screens.cards.carditems;

import android.content.Context;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.views.ExpandableHeightGridView;

public abstract class ItemListCard extends GalleryDoctorBaseCard
{

    protected final ExpandableHeightGridView mGrid = (ExpandableHeightGridView)findViewById(R.id.grid);

    public ItemListCard(Context context)
    {
        super(context);
        mGrid.setNumColumns(5);
        mGrid.setHorizontalSpacing((int)GeneralUtils.pxFromDp(getContext(), 8F));
        mGrid.setVerticalSpacing((int)GeneralUtils.pxFromDp(getContext(), 8F));
    }

    int getLayout()
    {
        return R.layout.gallery_doctor_card_list_photos;
    }
}
