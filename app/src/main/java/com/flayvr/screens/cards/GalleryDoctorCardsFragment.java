package com.flayvr.screens.cards;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;

import com.flayvr.doctor.R;
import com.flayvr.events.MediaItemsChangedEvent;
import com.flayvr.screens.cards.carditems.BadPhotosCard;
import com.flayvr.screens.cards.carditems.DuplicatePhotosCard;
import com.flayvr.screens.cards.carditems.GalleryDoctorBaseCard;
import com.flayvr.screens.cards.carditems.PhotosForReviewCard;
import com.flayvr.screens.cards.carditems.ScreenshotsCard;
import com.flayvr.screens.cards.carditems.VideosCard;
import com.flayvr.screens.cards.carditems.WhatsappReviewCard;
import com.flayvr.util.GalleryDoctorStatsUtils;
import de.greenrobot.event.EventBus;
import java.util.*;

public class GalleryDoctorCardsFragment extends Fragment
{

    private static final String SOURCE = "SOURCE";
    private GalleryDoctorCardsFragmentListener listener;
    private RecyclerView recList;
    private ReviewListAdapter reviewAdapter;
    private int source;

    public GalleryDoctorCardsFragment()
    {
    }

    public static GalleryDoctorCardsFragment newInstance(int i)
    {
        GalleryDoctorCardsFragment gallerydoctorcardsfragment = new GalleryDoctorCardsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("SOURCE", i);
        gallerydoctorcardsfragment.setArguments(bundle);
        return gallerydoctorcardsfragment;
    }

    private void updateCards()
    {
        com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat = GalleryDoctorStatsUtils.getMediaItemStat(source);
        List list = reviewAdapter.getCardsList();
        LinkedList linkedlist = new LinkedList();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            GalleryDoctorBaseCard gallerydoctorbasecard = (GalleryDoctorBaseCard)iterator.next();
            gallerydoctorbasecard.refreshCard(mediaitemstat);
            if(gallerydoctorbasecard.numberOfItems() == 0)
            {
                linkedlist.add(gallerydoctorbasecard);
            }
        } while(true);
        if(linkedlist.size() > 0)
        {
            list.removeAll(linkedlist);
            if(list.size() == 0)
            {
                getActivity().finish();
            }
        }
        reviewAdapter.notifyDataSetChanged();
    }

    public List getCards()
    {
        ArrayList arraylist = new ArrayList();
        com.flayvr.util.GalleryDoctorStatsUtils.MediaItemStat mediaitemstat = GalleryDoctorStatsUtils.getMediaItemStat(source);
        if(mediaitemstat.getTotalVideos() > 0L)
        {
            VideosCard videoscard = new VideosCard(getActivity(), mediaitemstat);
            videoscard.setAction(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.openVideos(source);
                }
            });
            arraylist.add(videoscard);
        }
        if(mediaitemstat.getBadPhotoCount() > 0L)
        {
            BadPhotosCard badphotoscard = new BadPhotosCard(getActivity(), mediaitemstat);
            badphotoscard.setAction(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.openBad(source);
                }
            });
            arraylist.add(badphotoscard);
        }
        if(mediaitemstat.getDuplicatePhotoCount() > 0L)
        {
            DuplicatePhotosCard duplicatephotoscard = new DuplicatePhotosCard(getActivity(), mediaitemstat);
            duplicatephotoscard.setAction(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.openDuplicate(source);
                }
            });
            arraylist.add(duplicatephotoscard);
        }
        if(mediaitemstat.getForReviewCount() > 0L)
        {
            PhotosForReviewCard photosforreviewcard = new PhotosForReviewCard(getActivity(), mediaitemstat);
            photosforreviewcard.setAction(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.openForReview(source);
                }
            });
            arraylist.add(photosforreviewcard);
        }
        if(mediaitemstat.getWhatsappPhotosCount() > 0L)
        {
            WhatsappReviewCard whatsappreviewcard = new WhatsappReviewCard(getActivity(), mediaitemstat);
            whatsappreviewcard.setAction(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.openWhatsapp(source);
                }
            });
            arraylist.add(whatsappreviewcard);
        }
        if(mediaitemstat.getScreenshotsCount() > 0L)
        {
            ScreenshotsCard screenshotscard = new ScreenshotsCard(getActivity(), mediaitemstat);
            screenshotscard.setAction(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.openScreenshots(source);
                }
            });
            arraylist.add(screenshotscard);
        }
        return arraylist;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        listener = (GalleryDoctorCardsFragmentListener)activity;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        EventBus.getDefault().register(this);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        View view = layoutinflater.inflate(R.layout.gallery_doctor_cards_fragment, viewgroup, false);
        recList = (RecyclerView)view.findViewById(R.id.cards);
        source = getArguments().getInt("SOURCE");
        reviewAdapter = new ReviewListAdapter(null);
        recList.setAdapter(reviewAdapter);
        recList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recList.addItemDecoration(new _cls1());
        List list = getCards();
        if(list.size() == 0)
        {
            getActivity().finish();
        }
        reviewAdapter.setCardsList(list);
        return view;
    }

    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(MediaItemsChangedEvent mediaitemschangedevent)
    {
        updateCards();
    }

    public void onResume()
    {
        super.onResume();
        updateCards();
    }

    public void scrollTo(GalleryDoctorCardsActivity.CARDS_TYPE cards_type)
    {
        int i = -1;

        switch(cards_type){
            default:
                break;
            case BAD:
                i = reviewAdapter.getCardIndex(BadPhotosCard.class);
                break;
            case DUPLICATE:
                i = reviewAdapter.getCardIndex(DuplicatePhotosCard.class);
                break;
            case FOR_REVIEW:
                i = reviewAdapter.getCardIndex(PhotosForReviewCard.class);
                break;
            case WHATSAPP:
                i = reviewAdapter.getCardIndex(WhatsappReviewCard.class);
                break;
            case SCREENSHOTS:
                i = reviewAdapter.getCardIndex(ScreenshotsCard.class);
                break;
            case VIDEOS:
                i = reviewAdapter.getCardIndex(VideosCard.class);
                break;
        }
        if(i >= 0)
            recList.scrollToPosition(i);
    }

    private class ReviewListAdapter extends android.support.v7.widget.RecyclerView.Adapter
    {

        private List cardsList;
        private Map type;

        public int getCardIndex(Class class1)
        {
            Iterator iterator = cardsList.iterator();
            for(int i = 0; iterator.hasNext(); i++)
            {
                if(((GalleryDoctorBaseCard)iterator.next()).getClass() == class1)
                {
                    return i;
                }
            }

            return -1;
        }

        public List getCardsList()
        {
            return cardsList;
        }

        public int getItemCount()
        {
            if(cardsList != null)
            {
                return cardsList.size();
            } else
            {
                return 0;
            }
        }

        public int getItemViewType(int i)
        {
            return ((GalleryDoctorBaseCard)cardsList.get(i)).getType();
        }

        public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
        {
            ((GalleryDoctorBaseCard)viewholder.itemView).bindView();
        }

        public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
        {
            return new android.support.v7.widget.RecyclerView.ViewHolder((View)type.get(Integer.valueOf(i))){};
        }

        public void setCardsList(List list)
        {
            cardsList = list;
            type.clear();
            GalleryDoctorBaseCard gallerydoctorbasecard;
            for(Iterator iterator = list.iterator(); iterator.hasNext(); type.put(Integer.valueOf(gallerydoctorbasecard.getType()), gallerydoctorbasecard))
            {
                gallerydoctorbasecard = (GalleryDoctorBaseCard)iterator.next();
            }

        }

        private ReviewListAdapter()
        {
            super();
            type = new HashMap();
        }

        ReviewListAdapter(_cls1 _pcls1)
        {
            this();
        }
    }

    public interface GalleryDoctorCardsFragmentListener
    {
        public abstract void openBad(int i);

        public abstract void openDuplicate(int i);

        public abstract void openForReview(int i);

        public abstract void openScreenshots(int i);

        public abstract void openVideos(int i);

        public abstract void openWhatsapp(int i);
    }


    private class _cls1 extends android.support.v7.widget.RecyclerView.ItemDecoration
    {
        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerview, android.support.v7.widget.RecyclerView.State state)
        {
            int i = getResources().getDimensionPixelSize(R.dimen.review_list_spacing);
            if(recyclerview.getChildPosition(view) == 0);
            rect.left = i;
            rect.right = i;
        }

        _cls1()
        {
            super();
        }
    }

}
