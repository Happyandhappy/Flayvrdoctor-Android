package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.hist4j.AdaptiveHistogram;
import com.flayvr.myrollshared.learning.PhotoFeatures;
import com.flayvr.myrollshared.managers.GalleryDoctorServicesProgress;
import com.flayvr.myrollshared.utils.IntentActions;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.util.*;
import org.joda.time.DateTime;

public abstract class PhotoClassifierService extends IntentService
{

    public static final int BAD_BLURRY_PERCENTILE = 5;
    public static final double BAD_DARK_THRESHOLD = 0.10000000000000001D;
    public static final int BAD_SCORE_PERCENTILE = 5;
    private static final int BATCH_SIZE = 50;
    public static final int BEST_DIR_PERCENTILE = 70;
    public static final int BEST_PERCENTILE = 95;
    public static final int FOR_REVIEW_SCORE_PERCENTILE = 15;
    public static final int GOOD_ENOUGH_PERCENTILE = 60;
    private static final String TAG = PhotoClassifierService.class.getSimpleName();
    private Set boringFolders;
    private User myRollUser;
    private Map rulesMap;
    private Set screenShotsFolders;
    private Set tempFolders;

    public PhotoClassifierService()
    {
        super(PhotoClassifierService.class.getSimpleName());
    }

    private void analyzeClassificationAsBad(MediaItem mediaitem, List list, ClassifierThreshold classifierthreshold)
    {
        if(mediaitem.getBlurry() != null && mediaitem.getBlurry().doubleValue() < classifierthreshold.getBadBlurry().doubleValue())
        {
            list.add(markPhotoAsBadWithRule(mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType.TOO_BLURRY_FOR_BAD));
        }
        if(mediaitem.getDark() != null && mediaitem.getDark().doubleValue() < classifierthreshold.getBadDark().doubleValue())
        {
            list.add(markPhotoAsBadWithRule(mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType.TOO_DARK_FOR_BAD));
        }
        if(mediaitem.getScore() != null && mediaitem.getScore().doubleValue() >= 0.0D && mediaitem.getScore().doubleValue() < classifierthreshold.getBadScore().doubleValue())
        {
            list.add(markPhotoAsBadWithRule(mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType.LOW_SCORE_FOR_BAD));
        }
    }

    private void analyzeClassificationAsForReview(MediaItem mediaitem, List list, ClassifierThreshold classifierthreshold)
    {
        if(mediaitem.getScore() != null && mediaitem.getScore().doubleValue() >= 0.0D && mediaitem.getScore().doubleValue() < classifierthreshold.getForReviewScore().doubleValue())
        {
            list.add(markPhotoForReviewWithRule(mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType.LOW_SCORE_FOR_REVIEW));
        }
        if(screenShotsFolders.contains(mediaitem.getFolderId()))
        {
            list.add(markPhotoForReviewWithRule(mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType.SCREENSHOT));
        }
        if(boringFolders.contains(mediaitem.getFolderId()) && mediaitem.getScore().doubleValue() < classifierthreshold.getGoodEnoughScore().doubleValue())
        {
            list.add(markPhotoForReviewWithRule(mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType.IN_BORING_FOLDER_AND_NOT_GOOD_ENOUGH));
        }
        if(tempFolders.contains(mediaitem.getFolderId()))
        {
            list.add(markPhotoForReviewWithRule(mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType.IN_TEMP_FOLDER));
        }
        if(PhotoFeatures.isTakenAtWork(mediaitem, myRollUser) && mediaitem.getScore().doubleValue() < classifierthreshold.getGoodEnoughScore().doubleValue())
        {
            list.add(markPhotoForReviewWithRule(mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType.TAKEN_AT_WORK_AND_NOT_GOOD_ENOUGH));
        }
    }

    private static Map calcNumberOfPhotosPerRule(Collection collection)
    {
        HashMap hashmap = new HashMap();
        for(Iterator iterator = collection.iterator(); iterator.hasNext();)
        {
            MediaItem mediaitem = (MediaItem)iterator.next();
            if(mediaitem.getPhotoClassifierRules() != null)
            {
                Iterator iterator1 = mediaitem.getPhotoClassifierRules().iterator();
                while(iterator1.hasNext()) 
                {
                    ClassifierRulesToPhotos classifierrulestophotos = (ClassifierRulesToPhotos)iterator1.next();
                    if(classifierrulestophotos.getClassifierRule() != null)
                    {
                        ClassifierRule classifierrule = classifierrulestophotos.getClassifierRule();
                        if(hashmap.containsKey(classifierrule))
                        {
                            hashmap.put(classifierrule, Integer.valueOf(1 + ((Integer)hashmap.get(classifierrule)).intValue()));
                        } else
                        {
                            hashmap.put(classifierrule, Integer.valueOf(1));
                        }
                    }
                }
            }
        }

        return hashmap;
    }

    private ClassifierRulesToPhotos createRuleForPhoto(MediaItem mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType ruletype)
    {
        ClassifierRule classifierrule = (ClassifierRule)rulesMap.get(ruletype);
        if(classifierrule == null)
        {
            classifierrule = new ClassifierRule();
            classifierrule.setRuleTypeEnum(ruletype);
            classifierrule.setTotalPhotosDeleted(Integer.valueOf(0));
            classifierrule.setTotalPhotosKept(Integer.valueOf(0));
            DBManager.getInstance().getDaoSession().getClassifierRuleDao().insert(classifierrule);
            rulesMap.put(classifierrule.getRuleTypeEnum(), classifierrule);
        }
        ClassifierRulesToPhotos classifierrulestophotos = new ClassifierRulesToPhotos();
        classifierrulestophotos.setClassifierRule(classifierrule);
        classifierrulestophotos.setPhoto(mediaitem);
        return classifierrulestophotos;
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_CV_FINISHED);
        return arraylist;
    }

    private List getPhotos(int i)
    {
        int j = 0;
        QueryBuilder querybuilder = DaoHelper.getPhotos(i);
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.CvRan.eq(Boolean.valueOf(true)), new WhereCondition[0]);
        querybuilder.where(com.flayvr.myrollshared.data.MediaItemDao.Properties.LastTimeClassified.isNull(), new WhereCondition[0]);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty).limit(1000).offset(0);
        Query query = querybuilder.build();
        LinkedList linkedlist = new LinkedList();
        for(int k = 1000; k == '\u03E8';)
        {
            query.setOffset(j);
            List list = query.list();
            linkedlist.addAll(list);
            k = list.size();
            j += 1000;
        }

        return linkedlist;
    }

    private int getSource()
    {
        return !PicasaSessionManager.getInstance().hasUser() ? 1 : 2;
    }

    private ClassifierThreshold getThresholds(int i, List list)
    {
        ClassifierThreshold classifierthreshold;
        ClassifierThreshold classifierthreshold1;
        AdaptiveHistogram adaptivehistogram;
        AdaptiveHistogram adaptivehistogram1;
        AdaptiveHistogram adaptivehistogram2;
        Iterator iterator;
        MediaItem mediaitem;
        classifierthreshold = DaoHelper.getThreshold(i);
        if(classifierthreshold != null)
            return classifierthreshold;
        Log.i(TAG, "creating thresholds");
        classifierthreshold1 = new ClassifierThreshold();
        classifierthreshold1.setBadDark(Double.valueOf(0.10000000000000001D));
        adaptivehistogram = new AdaptiveHistogram();
        adaptivehistogram1 = new AdaptiveHistogram();
        adaptivehistogram2 = new AdaptiveHistogram();
        iterator = list.iterator();
        while(iterator.hasNext())
        {
            mediaitem = (MediaItem)iterator.next();
            Double double1 = mediaitem.getBlurry();
            if(double1 != null)
            {
                if(double1.doubleValue() >= 0.0D)
                    adaptivehistogram.addValue(double1.floatValue());
            }
            Double double2 = mediaitem.getScore();
            if(double2.doubleValue() >= 0.0D)
            {
                adaptivehistogram1.addValue(double2.floatValue());
                if(!PhotoFeatures.isFromWhatsapp(mediaitem))
                {
                    adaptivehistogram2.addValue(double2.floatValue());
                }
            }
        }
        classifierthreshold1.setBadBlurry(new Double(adaptivehistogram.getValueForPercentile(5)));
        classifierthreshold1.setBadScore(new Double(adaptivehistogram1.getValueForPercentile(5)));
        classifierthreshold1.setForReviewScore(new Double(adaptivehistogram1.getValueForPercentile(15)));
        classifierthreshold1.setGoodEnoughScore(new Double(adaptivehistogram1.getValueForPercentile(60)));
        classifierthreshold1.setBestScore(new Double(adaptivehistogram2.getValueForPercentile(95)));
        classifierthreshold1.setBestDirectoryScore(new Double(adaptivehistogram2.getValueForPercentile(70)));
        classifierthreshold1.setSource(Integer.valueOf(i));
        DBManager.getInstance().getDaoSession().getClassifierThresholdDao().insert(classifierthreshold1);
        classifierthreshold = classifierthreshold1;
        return classifierthreshold;
    }

    private ClassifierRulesToPhotos markPhotoAsBadWithRule(MediaItem mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType ruletype)
    {
        mediaitem.setIsBad(Boolean.valueOf(true));
        return createRuleForPhoto(mediaitem, ruletype);
    }

    private ClassifierRulesToPhotos markPhotoForReviewWithRule(MediaItem mediaitem, com.flayvr.myrollshared.data.ClassifierRule.RuleType ruletype)
    {
        if(!mediaitem.getIsBad().booleanValue())
        {
            mediaitem.setIsForReview(Boolean.valueOf(true));
        }
        return createRuleForPhoto(mediaitem, ruletype);
    }

    private List processPhotos(int i)
    {
        LinkedList linkedlist;
        List list;
        ClassifierThreshold classifierthreshold;
        LinkedList linkedlist1;
        int j;
        linkedlist = new LinkedList();
        list = getPhotos(i);
        Log.i(TAG, "total photos: "+list.size());
        if(list.size() > 0)
        {
            classifierthreshold = getThresholds(i, list);
            linkedlist1 = new LinkedList();
            j = 0;
            while(j < list.size())
            {
                waitIfNeeded();
                if(j % 50 == 0)
                    onUpdate(i, (float)j / (1.0F * (float)list.size()));
                MediaItem mediaitem = (MediaItem)list.get(j);
                mediaitem.setIsBad(Boolean.valueOf(false));
                mediaitem.setIsForReview(Boolean.valueOf(false));
                analyzeClassificationAsBad(mediaitem, linkedlist1, classifierthreshold);
                analyzeClassificationAsForReview(mediaitem, linkedlist1, classifierthreshold);
                mediaitem.setLastTimeClassified((new DateTime()).toDate());
                linkedlist.add(mediaitem);
                j++;
            }
            if(linkedlist.size() > 0)
            {
                DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(linkedlist);
                DBManager.getInstance().getDaoSession().getClassifierRulesToPhotosDao().insertInTx(linkedlist1);
            }
        }
        GalleryDoctorServicesProgress.classifierServiceFinished(getSource(), true);
        return linkedlist;
    }

    private void setDataForRules()
    {
        QueryBuilder querybuilder = DBManager.getInstance().getDaoSession().getFolderDao().queryBuilder();
        WhereCondition wherecondition = com.flayvr.myrollshared.data.FolderDao.Properties.Name.in(PhotoFeatures.SCREENSHOTS_FOLDERS_NAMES);
        WhereCondition wherecondition1 = com.flayvr.myrollshared.data.FolderDao.Properties.Name.in(PhotoFeatures.BORING_FOLDERS_NAMES);
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = com.flayvr.myrollshared.data.FolderDao.Properties.Name.in(PhotoFeatures.TEMP_FOLDERS_NAMES);
        querybuilder.whereOr(wherecondition, wherecondition1, awherecondition);
        List list = querybuilder.list();
        screenShotsFolders = new HashSet();
        boringFolders = new HashSet();
        tempFolders = new HashSet();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            Folder folder = (Folder)iterator.next();
            if(PhotoFeatures.SCREENSHOTS_FOLDERS_NAMES.contains(folder.getName()))
            {
                screenShotsFolders.add(folder.getId());
            } else
            if(PhotoFeatures.BORING_FOLDERS_NAMES.contains(folder.getName()))
            {
                if(folder.getNotDeletedMediaItemCount().longValue() < 5L)
                {
                    boringFolders.add(folder.getId());
                }
            } else
            if(PhotoFeatures.TEMP_FOLDERS_NAMES.contains(folder.getName()))
            {
                tempFolders.add(folder.getId());
            }
        } while(true);
        List list1 = DBManager.getInstance().getDaoSession().getClassifierRuleDao().loadAll();
        rulesMap = new HashMap();
        ClassifierRule classifierrule;
        for(Iterator iterator1 = list1.iterator(); iterator1.hasNext(); rulesMap.put(classifierrule.getRuleTypeEnum(), classifierrule))
        {
            classifierrule = (ClassifierRule)iterator1.next();
        }

        myRollUser = User.getMyRollUser();
    }

    public static void updateRulesForDeletedPhotosAsync(final Collection photos)
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                Map map = PhotoClassifierService.calcNumberOfPhotosPerRule(photos);
                for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
                {
                    ClassifierRule classifierrule = (ClassifierRule)iterator.next();
                    if(classifierrule.getTotalPhotosDeleted() != null)
                    {
                        classifierrule.setTotalPhotosDeleted(Integer.valueOf(classifierrule.getTotalPhotosDeleted().intValue() + ((Integer)map.get(classifierrule)).intValue()));
                    } else
                    {
                        classifierrule.setTotalPhotosDeleted((Integer)map.get(classifierrule));
                    }
                }

                DBManager.getInstance().getDaoSession().getClassifierRuleDao().updateInTx(map.keySet());
            }
        });
    }

    public static void updateRulesForKeptPhotosAsync(final Collection photos)
    {
        FlayvrApplication.runNetwork(new Runnable() {
            @Override
            public void run() {
                Map map = PhotoClassifierService.calcNumberOfPhotosPerRule(photos);
                for(Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
                {
                    ClassifierRule classifierrule = (ClassifierRule)iterator.next();
                    if(classifierrule.getTotalPhotosKept() != null)
                    {
                        classifierrule.setTotalPhotosKept(Integer.valueOf(classifierrule.getTotalPhotosKept().intValue() + ((Integer)map.get(classifierrule)).intValue()));
                    } else
                    {
                        classifierrule.setTotalPhotosKept((Integer)map.get(classifierrule));
                    }
                }

                DBManager.getInstance().getDaoSession().getClassifierRuleDao().updateInTx(map.keySet());
            }
        });
    }

    public abstract void onFinish(List list);

    protected void onHandleIntent(Intent intent)
    {
        Date date = new Date();
        setDataForRules();
        onStart();
        LinkedList linkedlist = new LinkedList();
        linkedlist.addAll(processPhotos(1));
        linkedlist.addAll(processPhotos(2));
        Log.i(TAG, (new StringBuilder()).append("mid-timing: ").append((new Date()).getTime() - date.getTime()).toString());
        sendBroadcast(new Intent(IntentActions.ACTION_CLASSIFIER_FINISHED));
        Log.i(TAG, "finished classifying photos");
        long l = (new Date()).getTime() - date.getTime();
        Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l).toString());
        onFinish(linkedlist);
    }

    public abstract void onStart();

    public abstract void onUpdate(int i, float f);

    public abstract void waitIfNeeded();
}
