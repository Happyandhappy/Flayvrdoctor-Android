package com.flayvr.screens;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.view.*;
import android.widget.ImageView;
import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.fragments.MessageDialog;
import com.flayvr.myrollshared.services.GalleryBuilderService;
import com.flayvr.myrollshared.utils.AnalyticsUtils;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.flayvr.views.PhotoForReviewCard;
import com.flayvr.views.RotatableCardView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.sefford.circularprogressdrawable.CircularProgressDrawable;
import java.util.*;

public abstract class ForReviewFragment extends Fragment
{

    protected static final float DEGREES[] = {
        -9F, 3F, -3F, 9F
    };
    protected static final String SOURCE = "SOURCE";
    protected static final long UNDO_INTERVAL_DURATION = 20L;
    protected static final long UNDO_TIMEOUT_DURATION = 5000L;
    private PhotoForReviewCard cards[];
    private ViewGroup cardsFrame;
    private CardView hintCard;
    private int index;
    private boolean isAnimating;
    private boolean isProgressOut;
    private MediaItem lastItem;
    private long lastTime;
    private long mMediaItemCount;
    private List mMediaItemlist;
    private List mReviewedItemsForAnalytics;
    private int numberOfPhotosCleaned;
    private String reason;
    private long sizeOfPhotosCleaned;
    private int source;
    private long totalTime;
    private CircularProgressDrawable undoBtn;
    private View undoFrame;
    private boolean userMadeAction;

    public ForReviewFragment()
    {
        numberOfPhotosCleaned = 0;
        sizeOfPhotosCleaned = 0L;
        index = 0;
        userMadeAction = false;
        isAnimating = false;
        isProgressOut = false;
        lastTime = 0L;
        totalTime = 0L;
    }

    public void animateUndoIn()
    {
        lastTime = 0L;
        undoBtn.setProgress(0.0F);
        if(isProgressOut)
        {
            isAnimating = true;
            isProgressOut = true;
            ValueAnimator valueanimator = ValueAnimator.ofFloat(new float[] {
                0.0F, 1.0F
            });
            valueanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewHelper.setAlpha(undoFrame, animation.getAnimatedFraction());
                    ViewHelper.setScaleX(undoFrame, animation.getAnimatedFraction());
                    ViewHelper.setScaleY(undoFrame, animation.getAnimatedFraction());
                }
            });
            valueanimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isProgressOut = false;
                    isAnimating = false;
                    startUndoTimer();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueanimator.start();
        } else {
            isAnimating = false;
            startUndoTimer();
        }
    }

    public void animateUndoOut()
    {
        if(isProgressOut)
        {
            return;
        } else
        {
            isAnimating = true;
            isProgressOut = true;
            ValueAnimator valueanimator = ValueAnimator.ofFloat(new float[] {
                0.0F, 1.0F
            });
            valueanimator.addUpdateListener(new com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener(){
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewHelper.setAlpha(undoFrame, 1.0F - animation.getAnimatedFraction());
                    ViewHelper.setScaleX(undoFrame, 1.0F - animation.getAnimatedFraction() / 2.0F);
                    ViewHelper.setScaleY(undoFrame, 1.0F - animation.getAnimatedFraction() / 2.0F);
                }
            });
            valueanimator.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimating = false;
                }
            });
            valueanimator.start();
            return;
        }
    }

    public void deleteItem(MediaItem mediaitem)
    {
        if(mediaitem != null)
        {
            numberOfPhotosCleaned = 1 + numberOfPhotosCleaned;
            sizeOfPhotosCleaned = sizeOfPhotosCleaned + mediaitem.getFileSizeBytesSafe().longValue();
            if(numberOfPhotosCleaned >= 20 || (float)numberOfPhotosCleaned > 0.08F * (float)getmMediaItemCount())
            {
                reason = "deleted photos for review";
                userMadeAction = true;
            }
            ArrayList arraylist = new ArrayList();
            mediaitem.setWasDeletedByUser(Boolean.valueOf(true));
            mediaitem.setIsForReview(Boolean.valueOf(false));
            DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(new MediaItem[] {
                mediaitem
            });
            arraylist.add(mediaitem);
            GalleryBuilderService.deleteItems(arraylist);
            mReviewedItemsForAnalytics.add(mediaitem);
        }
    }

    public void deleteLastItem()
    {
        deleteItem(lastItem);
        lastItem = null;
    }

    float getCardDegree(PhotoForReviewCard photoforreviewcard)
    {
        int i = 0;
        int j = 0;
        for(; i < cards.length; i++)
        {
            if(photoforreviewcard == cards[i])
            {
                j = i;
            }
        }

        return DEGREES[j];
    }

    RotatableCardView getCardForCoordinates(float f, float f1)
    {
        RotatableCardView rotatablecardview = getTopCard();
        if(rotatablecardview != null && getHitRect(rotatablecardview).contains((int)f, (int)f1))
        {
            return rotatablecardview;
        } else
        {
            return null;
        }
    }

    public Rect getHitRect(View view)
    {
        Rect rect = new Rect();
        rect.left = view.getLeft();
        rect.right = view.getRight();
        rect.top = view.getTop();
        rect.bottom = view.getBottom();
        return rect;
    }

    public int getIndex()
    {
        return index;
    }

    protected abstract long getItemCount(int i);

    protected abstract List getItemList(int i);

    public int getNumberOfPhotosCleaned()
    {
        return numberOfPhotosCleaned;
    }

    public String getReason()
    {
        return reason;
    }

    public List getReviewedItemsForAnalytics()
    {
        return mReviewedItemsForAnalytics;
    }

    public long getSizeOfPhotosCleaned()
    {
        return sizeOfPhotosCleaned;
    }

    RotatableCardView getTopCard()
    {
        int i = -1 + cardsFrame.getChildCount();
label0:
        do
        {
label1:
            {
                if(i < 0)
                {
                    break label0;
                }
                Object obj = (RotatableCardView)cardsFrame.getChildAt(i);
                if(obj instanceof PhotoForReviewCard)
                {
                    obj = (PhotoForReviewCard)obj;
                    if(((PhotoForReviewCard) (obj)).GetIsAnimating())
                    {
                        break label1;
                    }
                }
                return ((RotatableCardView) (obj));
            }
            i--;
        } while(true);
        return null;
    }

    public boolean getUserMadeAction()
    {
        return userMadeAction;
    }

    public long getmMediaItemCount()
    {
        return mMediaItemCount;
    }

    public List getmMediaItemlist()
    {
        return mMediaItemlist;
    }

    public void keepItem(MediaItem mediaitem)
    {
        if(mediaitem != null)
        {
            mediaitem.setWasKeptByUser(Boolean.valueOf(true));
            mediaitem.setIsForReview(Boolean.valueOf(false));
            DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(new MediaItem[] {
                mediaitem
            });
            mReviewedItemsForAnalytics.add(mediaitem);
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        source = getArguments().getInt("SOURCE");
        mMediaItemCount = getItemCount(source);
        mMediaItemlist = getItemList(source);
        mReviewedItemsForAnalytics = new LinkedList();
        final ViewGroup view = (ViewGroup)layoutinflater.inflate(R.layout.gallery_doctor_photos_for_review_fragment, viewgroup, false);
        cardsFrame = (ViewGroup)view.findViewById(R.id.card_frame);
        view.bringChildToFront(cardsFrame);
        ImageView imageview = (ImageView)view.findViewById(R.id.undo);
        ImageView imageview1 = (ImageView)view.findViewById(R.id.undoImage);
        ImageView imageview2 = (ImageView)view.findViewById(R.id.yes_button);
        ImageView imageview3 = (ImageView)view.findViewById(R.id.no_button);
        undoFrame = view.findViewById(R.id.progressBarView);
        undoBtn = (new com.sefford.circularprogressdrawable.CircularProgressDrawable.Builder()).setRingWidth((int)GeneralUtils.pxFromDp(getActivity(), 2.0F)).setOutlineColor(getResources().getColor(R.color.md_grey_50)).setRingColor(getResources().getColor(R.color.md_grey_600)).setCenterColor(getResources().getColor(R.color.circular_undo_button)).create();
        undoBtn.setProgress(0.5F);
        imageview.setImageDrawable(undoBtn);
        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performUndo();
            }
        });
        imageview2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                RotatableCardView rotatablecardview = getTopCard();
                if(rotatablecardview != null && !isAnimating)
                {
                    setThrowAnimation(rotatablecardview, com.flayvr.views.PhotoForReviewCard.Direction.RIGHT);
                }
            }
        });
        imageview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotatableCardView rotatablecardview = getTopCard();
                if(rotatablecardview != null && !isAnimating)
                {
                    setThrowAnimation(rotatablecardview, com.flayvr.views.PhotoForReviewCard.Direction.LEFT);
                }
            }
        });
        cards = new PhotoForReviewCard[4];
        cards[0] = (PhotoForReviewCard)view.findViewById(R.id.card1);
        cards[1] = (PhotoForReviewCard)view.findViewById(R.id.card2);
        cards[2] = (PhotoForReviewCard)view.findViewById(R.id.card3);
        cards[3] = (PhotoForReviewCard)view.findViewById(R.id.card4);
        hintCard = (CardView)view.findViewById(R.id.hint_card);
        view.setOnTouchListener(new View.OnTouchListener() {
            RotatableCardView card;
            float startDegree;
            float startX;
            float startY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(isAnimating)
                    return false;
                if(card == null)
                    card = getCardForCoordinates(motionEvent.getX(), motionEvent.getY());
                if(card == null)
                    return false;
                if(motionEvent.getAction() == 0)
                {
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    startDegree = card.getDegree();
                    return true;
                }
                if(motionEvent.getAction() == 2)
                {
                    card.setOffsets(startDegree + (30F * (motionEvent.getX() - startX)) / (float)view.getWidth(), 1.2F * (motionEvent.getX() - startX), motionEvent.getY() - startY);
                    return true;
                }
                if(motionEvent.getAction() == 1)
                {
                    RotatableCardView rotatablecardview1 = card;
                    card = null;
                    if(rotatablecardview1.getDx() > (float)(rotatablecardview1.getWidth() / 3))
                    {
                        setThrowAnimation(rotatablecardview1, com.flayvr.views.PhotoForReviewCard.Direction.RIGHT);
                    } else
                    if(rotatablecardview1.getDx() < (float)(-rotatablecardview1.getWidth() / 3))
                    {
                        setThrowAnimation(rotatablecardview1, com.flayvr.views.PhotoForReviewCard.Direction.LEFT);
                    } else
                    {
                        setCenterAnimation(rotatablecardview1);
                    }
                    return true;
                }
                if(motionEvent.getAction() == 3)
                {
                    RotatableCardView rotatablecardview = card;
                    setCenterAnimation(rotatablecardview);
                }
                card = null;
                return false;
            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        return view;
    }

    public void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        int i = -1 + cards.length;
        while(i >= 0) 
        {
            if((long)i < mMediaItemCount)
            {
                cards[i].setOffsets(DEGREES[i], 0.0F, 0.0F);
                MediaItem mediaitem = (MediaItem)mMediaItemlist.get(index);
                setCard(cards[i], mediaitem);
                index = 1 + index;
            } else
            {
                cardsFrame.removeView(cards[i]);
            }
            i--;
        }
        if(PreferencesManager.isPhotosForReviewHintsShown())
        {
            cardsFrame.removeView(hintCard);
        } else
        {
            ViewHelper.setRotation(hintCard, DEGREES[2]);
            PreferencesManager.setIsPhotosForReviewHintsShown();
        }
        updateElevation(0.0F);
        animateUndoOut();
    }

    public void performUndo()
    {
        if(lastItem != null)
        {
            AnalyticsUtils.trackEventWithKISS("chose to undo deletion of photo for review");
            final PhotoForReviewCard card = (PhotoForReviewCard)cardsFrame.getChildAt(0);
            ViewHelper.setTranslationX(card, -1 * cardsFrame.getWidth());
            cardsFrame.bringChildToFront(card);
            updateElevation(1.0F);
            animateUndoOut();
            card.setItem(lastItem, getActivity().getContentResolver(), new PhotoForReviewCard.OnItemLoadListener(){
                @Override
                public void doAfterItemLoad() {
                    lastItem = null;
                    setCenterAnimation(card);
                    int _tmp = index--;
                    lastItem = null;
                }
            });
        }
    }

    public void resetReviewedItemsForAnalytics()
    {
        mReviewedItemsForAnalytics = new LinkedList();
    }

    void setCard(PhotoForReviewCard photoforreviewcard, MediaItem mediaitem)
    {
        photoforreviewcard.setItem(mediaitem, getActivity().getContentResolver());
    }

    void setCenterAnimation(final RotatableCardView cardView)
    {
        float af[] = new float[2];
        af[0] = cardView.getDegree();
        af[1] = 0.0F;
        ValueAnimator valueanimator = ValueAnimator.ofFloat(af);
        final float f = cardView.getDx();
        final float f1 = cardView.getDy();
        valueanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueanimator) {
                cardView.setOffsets(undoBtn.getProgress() * (1.0F - valueanimator.getAnimatedFraction()), f * (1.0F - valueanimator.getAnimatedFraction()), f1 * (1.0F - valueanimator.getAnimatedFraction()));
            }
        });
        valueanimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(cardView instanceof PhotoForReviewCard)
                    ((PhotoForReviewCard)cardView).setIsAnimating(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueanimator.start();
    }

    void setThrowAnimation(final RotatableCardView cardView, final com.flayvr.views.PhotoForReviewCard.Direction direction)
    {
        float alpha;
        isAnimating = true;
        float af[] = new float[2];
        af[0] = cardView.getDegree();
        af[1] = 0.0F;
        ValueAnimator valueanimator = ValueAnimator.ofFloat(af);
        final float translationX = cardView.getDx();
        final float translationY = cardView.getDy();
        final float degree = cardView.getDegree();
        deleteItem(lastItem);
        boolean flag = cardView instanceof PhotoForReviewCard;
        ImageView finalActionImage = null;
        if(flag)
        {
            PhotoForReviewCard photoforreviewcard = (PhotoForReviewCard)cardView;
            if(direction == com.flayvr.views.PhotoForReviewCard.Direction.LEFT)
                finalActionImage = photoforreviewcard.getDeleteActionImageView();
            else
                finalActionImage = photoforreviewcard.getKeepActionImageView();
        }
        alpha = 0.0F;
        if(finalActionImage != null)
            alpha = ViewHelper.getAlpha(finalActionImage);
        final ImageView final_finalActionImage = finalActionImage;
        final float final_alpha = alpha;
        valueanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueanimator)
            {
                if(direction != com.flayvr.views.PhotoForReviewCard.Direction.LEFT) {
                    cardView.setOffsets(degree * (1.0F + valueanimator.getAnimatedFraction()), translationX + 1.5F * valueanimator.getAnimatedFraction() * (float)cardView.getWidth(), translationY);
                    if(final_finalActionImage != null)
                    {
                        ViewHelper.setAlpha(final_finalActionImage, final_alpha * (1.0F + valueanimator.getAnimatedFraction()));
                    }
                }else {
                    cardView.setOffsets(degree * (1.0F + valueanimator.getAnimatedFraction()), translationX - 1.5F * valueanimator.getAnimatedFraction() * (float) cardView.getWidth(), translationY);
                    if (final_finalActionImage != null) {
                        ViewHelper.setAlpha(final_finalActionImage, final_alpha * (1.0F + valueanimator.getAnimatedFraction()));
                    }
                }
                undoBtn.setProgress(undoBtn.getProgress() * (1.0F - valueanimator.getAnimatedFraction()));
                updateElevation(valueanimator.getAnimatedFraction());
            }
        });
        valueanimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator)
            {
            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                isAnimating = false;
                if(cardView instanceof PhotoForReviewCard)
                {
                    final PhotoForReviewCard card = (PhotoForReviewCard)cardView;
                    cardView.post(new Runnable(){
                        @Override
                        public void run()
                        {
                            int i = 1;
                            MediaItem mediaitem;
                            int k;
                            final MediaItem lastItem = card.getItem();
                            if(direction == com.flayvr.views.PhotoForReviewCard.Direction.LEFT)
                            {
                                if(!PreferencesManager.isPhotosForReviewDeleteHintsShown())
                                {
                                    PreferencesManager.setIsPhotosForReviewDeleteHintsShown();
                                    MessageDialog messagedialog1 = new MessageDialog();
                                    Resources resources1 = getResources();
                                    if(source == i)
                                    {
                                        k = R.string.gallery_doctor_review_deletion_popup_text;
                                    } else
                                    {
                                        k = R.string.review_deletion_popup_text_cloud;
                                    }
                                    messagedialog1.setMsg(resources1.getString(k));
                                    messagedialog1.setTitle(getResources().getString(R.string.gallery_doctor_review_deletion_popup_heading));
                                    messagedialog1.setNegativeText(getResources().getString(R.string.cancel_label));
                                    messagedialog1.setNegetiveListener(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialoginterface, int l)
                                        {
                                            ViewHelper.setTranslationX(cardView, -1 * cardsFrame.getWidth());
                                            cardsFrame.bringChildToFront(card);
                                            updateElevation(1.0F);
                                            animateUndoOut();
                                            card.setItem(lastItem, getActivity().getContentResolver(), new PhotoForReviewCard.OnItemLoadListener() {
                                                @Override
                                                public void doAfterItemLoad() {
                                                    ForReviewFragment.this.lastItem = null;
                                                    setCenterAnimation(cardView);
                                                    index--;
                                                }
                                            });
                                        }
                                    });
                                    messagedialog1.setPositiveListener(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialoginterface, int l)
                                        {
                                            deleteItem(lastItem);
                                        }
                                    });
                                    messagedialog1.setPositiveText(getResources().getString(R.string.dialog_ok));
                                    messagedialog1.show(getFragmentManager(), "fsdf");
                                    i = 0;
                                } else
                                {
                                    ForReviewFragment.this.lastItem = lastItem;
                                }
                            } else {
                                ForReviewFragment.this.lastItem = null;
                                if(!PreferencesManager.isPhotosForReviewLikeHintsShown())
                                {
                                    PreferencesManager.setIsPhotosForReviewLikeHintsShown();
                                    MessageDialog messagedialog = new MessageDialog();
                                    Resources resources = getResources();
                                    int j;
                                    if(source == i)
                                    {
                                        j = R.string.gallery_doctor_review_acceptance_popup_text;
                                    } else
                                    {
                                        j = R.string.review_acceptance_popup_text_cloud;
                                    }
                                    messagedialog.setMsg(resources.getString(j));
                                    messagedialog.setTitle(getResources().getString(R.string.gallery_doctor_review_acceptance_popup_heading));
                                    messagedialog.setNegativeText(getResources().getString(R.string.cancel_label));
                                    messagedialog.setNegetiveListener(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialoginterface, int l)
                                        {
                                            ViewHelper.setTranslationX(cardView, cardsFrame.getWidth());
                                            cardsFrame.bringChildToFront(card);
                                            updateElevation(1.0F);
                                            animateUndoOut();
                                            card.setItem(lastItem, getActivity().getContentResolver(), new PhotoForReviewCard.OnItemLoadListener() {
                                                @Override
                                                public void doAfterItemLoad() {
                                                    ForReviewFragment.this.lastItem = null;
                                                    setCenterAnimation(cardView);
                                                    index--;
                                                }
                                            });
                                        }
                                    });
                                    messagedialog.setPositiveListener(new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialoginterface, int l)
                                        {
                                            keepItem(lastItem);
                                        }
                                    });
                                    messagedialog.setPositiveText(getResources().getString(R.string.dialog_ok));
                                    messagedialog.show(getFragmentManager(), "fsdf");
                                } else
                                {
                                    keepItem(lastItem);
                                }
                            }
                            card.setIsAnimating(false);
                            ViewCompat.setElevation(cardView, 5F);
                            if((long)index < mMediaItemCount)
                            {
                                GeneralUtils.sendViewToBack(cardView);
                                cardView.setOffsets(getCardDegree(card), 0.0F, 0.0F);
                                mediaitem = (MediaItem)mMediaItemlist.get(index);
                                setCard(card, mediaitem);
                                i = index++;
                                if(ForReviewFragment.this.lastItem != null && i != 0)
                                {
                                    animateUndoIn();
                                    return;
                                } else
                                {
                                    animateUndoOut();
                                    return;
                                }
                            }
                            cardsFrame.removeView(cardView);
                            if(cardsFrame.getChildCount() == 0)
                            {
                                animateUndoOut();
                                deleteLastItem();
                                getActivity().finish();
                                return;
                            }
                            if(lastItem != null && i != 0)
                                animateUndoIn();
                            else
                                animateUndoOut();
                        }
                    });
                } else {
                    cardView.post(new Runnable() {
                        @Override
                        public void run() {
                            cardsFrame.removeView(cardView);
                        }
                    });
                }
            }

            public void onAnimationRepeat(Animator animator)
            {
            }

            public void onAnimationStart(Animator animator)
            {
            }
        });
        if(cardView instanceof PhotoForReviewCard)
        {
            ((PhotoForReviewCard)cardView).setIsAnimating(true);
        }
        valueanimator.start();
    }

    public void startUndoTimer()
    {
        lastTime = System.currentTimeMillis();
        totalTime = 0L;
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                long l = System.currentTimeMillis();
                long l1 = l - lastTime;
                totalTime += l1;
                lastTime = l;
                undoBtn.setProgress((float)totalTime / 5000F);
                if(!isAnimating)
                {
                    if(totalTime >= 5000L)
                        animateUndoOut();
                    else
                        h.postDelayed(this, 20L);
                }
            }
        }, 20L);
    }

    public void updateElevation(float f)
    {
        for(int i = -1 + cardsFrame.getChildCount(); i >= 0; i--)
        {
            ViewCompat.setElevation((RotatableCardView)cardsFrame.getChildAt(i), 5F + 10F * (float)i + f * 10F); //wrong
        }

    }
}
