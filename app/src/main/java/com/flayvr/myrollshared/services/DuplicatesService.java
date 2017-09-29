package com.flayvr.myrollshared.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.processing.CVFeatures;
import com.flayvr.myrollshared.utils.IntentActions;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.*;
import java.util.*;
import org.opencv.core.Mat;

public abstract class DuplicatesService extends IntentService
{

    private static final int BATCH_SIZE = 50;
    private static final double MIN_TIME_DIFFERENCE_FOR_HIGH_THRESHOLD_IN_MILLIS = 20000D;
    private static final double MIN_TIME_DIFFERENCE_FOR_LOW_THRESHOLD_IN_MILLIS = 10000D;
    private static final double SIMILARITY_SCORE_HIGH_THRESHOLD = 0.94999999999999996D;
    private static final double SIMILARITY_SCORE_LOW_THRESHOLD = 0.84999999999999998D;
    private static final double SIMILARITY_SCORE_THRESHOLD_FOR_SUBSET = 0.80000000000000004D;
    private static final String TAG = DuplicatesService.class.getSimpleName();
    private CVFeatures cvFeatures;
    private boolean didUpdate;

    public DuplicatesService()
    {
        super(DuplicatesService.class.getSimpleName());
        cvFeatures = CVFeatures.getInstance();
    }

    private Set createNewDuplicatesSetAndStartNewOne(Set set)
    {
        if(set.size() > 0)
        {
            createNewDuplicatesSetInDB(set);
            didUpdate = true;
        }
        return new HashSet();
    }

    private void createNewDuplicatesSetInDB(Set set)
    {
        DBManager dbmanager = DBManager.getInstance();
        HashSet hashset = new HashSet();
        for(Iterator iterator = set.iterator(); iterator.hasNext(); hashset.add(((MediaItem)iterator.next()).getId())) { }
        List list = dbmanager.getDaoSession().getDuplicatesSetsToPhotosDao().queryBuilder().where(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.PhotoId.in(hashset), new WhereCondition[0]).build().list();
        if(list == null || list.size() == 0)
        {
            DuplicatesSet duplicatesset = new DuplicatesSet();
            dbmanager.getDaoSession().getDuplicatesSetDao().insert(duplicatesset);
            DuplicatesSetsToPhotos duplicatessetstophotos;
            for(Iterator iterator1 = set.iterator(); iterator1.hasNext(); dbmanager.getDaoSession().getDuplicatesSetsToPhotosDao().insert(duplicatessetstophotos))
            {
                MediaItem mediaitem = (MediaItem)iterator1.next();
                duplicatessetstophotos = new DuplicatesSetsToPhotos();
                duplicatessetstophotos.setDuplicatesSet(duplicatesset);
                duplicatessetstophotos.setPhoto(mediaitem);
            }

        } else
        {
            DuplicatesSet duplicatesset1 = ((DuplicatesSetsToPhotos)list.get(0)).getDuplicatesSet();
            if(duplicatesset1 != null)
            {
                duplicatesset1.resetDuplicatesSetPhotos();
                Iterator iterator2 = set.iterator();
                do
                {
                    if(!iterator2.hasNext())
                    {
                        break;
                    }
                    MediaItem mediaitem1 = (MediaItem)iterator2.next();
                    if(!duplicatesset1.getSortedDuplicatesSetPhotos().contains(mediaitem1))
                    {
                        DuplicatesSetsToPhotos duplicatessetstophotos1 = new DuplicatesSetsToPhotos();
                        duplicatessetstophotos1.setDuplicatesSet(duplicatesset1);
                        duplicatessetstophotos1.setPhoto(mediaitem1);
                        dbmanager.getDaoSession().getDuplicatesSetsToPhotosDao().insert(duplicatessetstophotos1);
                    }
                } while(true);
                duplicatesset1.update();
            }
        }
    }

    public static void deleteSetsForDeletedPhotos(Collection collection)
    {
        HashSet hashset = new HashSet();
        LinkedList linkedlist = new LinkedList();
        for(Iterator iterator = collection.iterator(); iterator.hasNext();)
        {
            List list = ((MediaItem)iterator.next()).getPhotoDuplicatesSets();
            linkedlist.addAll(list);
            Iterator iterator1 = list.iterator();
            while(iterator1.hasNext()) 
            {
                hashset.add(((DuplicatesSetsToPhotos)iterator1.next()).getDuplicatesSet());
            }
        }

        deleteSetsForDeletedPhotos(((Collection) (linkedlist)), ((Collection) (hashset)));
    }

    public static void deleteSetsForDeletedPhotos(Collection collection, Collection collection1)
    {
        DBManager.getInstance().getDaoSession().getDuplicatesSetsToPhotosDao().deleteInTx(collection);
        Iterator iterator = collection1.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            DuplicatesSet duplicatesset = (DuplicatesSet)iterator.next();
            if(duplicatesset != null)
            {
                duplicatesset.resetDuplicatesSetPhotos();
                if(duplicatesset.getDuplicatesSetPhotos() == null || duplicatesset.getDuplicatesSetPhotos().size() <= 1)
                {
                    if(duplicatesset.getDuplicatesSetPhotos() != null && duplicatesset.getDuplicatesSetPhotos().size() == 1)
                    {
                        ((DuplicatesSetsToPhotos)duplicatesset.getDuplicatesSetPhotos().get(0)).delete();
                    }
                    duplicatesset.delete();
                }
            }
        } while(true);
    }

    public static List getIntentFilter()
    {
        ArrayList arraylist = new ArrayList();
        arraylist.add(IntentActions.ACTION_CLASSIFIER_FINISHED);
        return arraylist;
    }

    private void handleAnalyzedPhoto(Set set, MediaItem mediaitem)
    {
        mediaitem.setWasAnalyzedForDuplicates(Boolean.valueOf(true));
        set.add(mediaitem);
    }

    private Pair isSimilar(Mat mat, Mat mat1, long l)
    {
        double d = cvFeatures.getSimilarityScore(mat, mat1);
        boolean flag;
        Mat mat2;
        Mat mat3;
        Mat mat4;
        Mat mat5;
        Mat mat6;
        Mat mat7;
        if(d > 0.94999999999999996D || (double)l < 10000D && d > 0.84999999999999998D)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if(flag)
        {
            Mat mat8 = mat.submat(0, mat.rows() / 2, 0, mat.cols() / 2);
            Mat mat9 = mat1.submat(0, mat1.rows() / 2, 0, mat1.cols() / 2);
            if(cvFeatures.getSimilarityScore(mat8, mat9) > 0.80000000000000004D)
            {
                flag = true;
            } else
            {
                flag = false;
            }
        }
        if(flag)
        {
            mat6 = mat.submat(0, mat.rows() / 2, mat.cols() / 2, mat.cols());
            mat7 = mat1.submat(0, mat1.rows() / 2, mat1.cols() / 2, mat1.cols());
            if(cvFeatures.getSimilarityScore(mat6, mat7) > 0.80000000000000004D)
            {
                flag = true;
            } else
            {
                flag = false;
            }
        }
        if(flag)
        {
            mat4 = mat.submat(mat.rows() / 2, mat.rows(), 0, mat.cols() / 2);
            mat5 = mat1.submat(mat1.rows() / 2, mat1.rows(), 0, mat1.cols() / 2);
            if(cvFeatures.getSimilarityScore(mat4, mat5) > 0.80000000000000004D)
            {
                flag = true;
            } else
            {
                flag = false;
            }
        }
        if(flag)
        {
            mat2 = mat.submat(mat.rows() / 2, mat.rows(), mat.cols() / 2, mat.cols());
            mat3 = mat1.submat(mat1.rows() / 2, mat1.rows(), mat1.cols() / 2, mat1.cols());
            if(cvFeatures.getSimilarityScore(mat2, mat3) > 0.80000000000000004D)
            {
                flag = true;
            } else
            {
                flag = false;
            }
        }
        return new Pair(Boolean.valueOf(flag), Double.valueOf(d));
    }

    private void processImagesFromSource(int i)
    {
        LinkedList linkedlist;
        Object obj;
        Object obj1;
        byte byte0;
        int l;
        Object obj2;
        Set set;
        float f;
        MediaItem mediaitem;
        MediaItem mediaitem1;
        long l1;
        Set set1;
        Set set2;
        Mat mat1;
        Mat mat2;
        int j1;
        Set set4;
        Set set5;
        Set set6;
        Set set7;
        Object obj3;
        int i1;
        Mat mat;
        Set set3;
        Object obj4;
        Date date = new Date((new Date()).getTime() - 0x240c8400L);
        QueryBuilder querybuilder = DaoHelper.getPhotos(i);
        WhereCondition wherecondition = com.flayvr.myrollshared.data.MediaItemDao.Properties.WasAnalyzedForDuplicates.isNull();
        WhereCondition wherecondition1 = com.flayvr.myrollshared.data.MediaItemDao.Properties.WasAnalyzedForDuplicates.eq(Boolean.valueOf(false));
        WhereCondition awherecondition[] = new WhereCondition[1];
        awherecondition[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date.gt(date);
        querybuilder.where(querybuilder.or(wherecondition, wherecondition1, awherecondition), new WhereCondition[0]);
        Property aproperty[] = new Property[1];
        aproperty[0] = com.flayvr.myrollshared.data.MediaItemDao.Properties.Date;
        querybuilder.orderDesc(aproperty).limit(1000).offset(0);
        Query query = querybuilder.build();
        linkedlist = new LinkedList();
        int j = 1000;
        for(int k = 0; j == '\u03E8'; k += 1000)
        {
            query.setOffset(k);
            List list = query.list();
            linkedlist.addAll(list);
            j = list.size();
        }
        Log.w(TAG, (new StringBuilder()).append("total photos: ").append(linkedlist.size()).toString());
        obj = new HashSet();
        didUpdate = false;
        HashSet hashset = new HashSet();
        obj1 = null;
        byte0 = -1;
        l = 0;
        obj2 = hashset;
        while(l < -1 + linkedlist.size()) {
            waitIfNeeded();
            if (l % 50 == 0) {
                set = updateAnalyzedPhotos((Set) obj2);
                obj3 = set;
                f = l;
                onUpdate(f / (1.0F * (float) linkedlist.size()));
            } else {
                obj3 = obj2;
            }
            mediaitem = (MediaItem) linkedlist.get(l);
            mediaitem1 = (MediaItem) linkedlist.get(l + 1);
            if (mediaitem.getWasAnalyzedForDuplicates() == null || !mediaitem.getWasAnalyzedForDuplicates().booleanValue() || mediaitem1.getWasAnalyzedForDuplicates() == null || !mediaitem1.getWasAnalyzedForDuplicates().booleanValue()) {
                l1 = mediaitem.getDate().getTime() - mediaitem1.getDate().getTime();
                if ((double) l1 <= 20000D) {
                    if (byte0 != l || obj1 == null)
                        mat1 = cvFeatures.readImageMatrix(mediaitem, shouldDownloadRemoteItems());
                    else
                        mat1 = (Mat)obj1;
                    mat2 = cvFeatures.readImageMatrix(mediaitem1, shouldDownloadRemoteItems());
                    j1 = l + 1;
                    if (mat1 == null || mat2 == null) {
                        Log.w(TAG, "matrixes are null");
                        i1 = j1;
                        mat = mat2;
                    } else {
                        if (!((Boolean) isSimilar(mat1, mat2, l1).first).booleanValue()) {
                            if (((Set) (obj)).size() <= 0) {
                                set3 = ((Set) (obj3));
                                obj4 = obj;
                            } else {
                                set4 = updateAnalyzedPhotos(((Set) (obj3)));
                                set3 = set4;
                                set5 = createNewDuplicatesSetAndStartNewOne(((Set) (obj)));
                                obj4 = set5;
                            }
                        } else {
                            ((Set) (obj)).add(mediaitem);
                            ((Set) (obj)).add(mediaitem1);
                            set3 = ((Set) (obj3));
                            obj4 = obj;
                        }
                        handleAnalyzedPhoto(set3, mediaitem);
                        if (l + 1 == -1 + linkedlist.size())
                            handleAnalyzedPhoto(set3, mediaitem1);
                        i1 = j1;
                        mat = mat2;
                        obj = obj4;
                        obj3 = set3;
                    }
                } else {
                    handleAnalyzedPhoto(((Set) (obj3)), mediaitem);
                    if (l + 1 == -1 + linkedlist.size()) {
                        handleAnalyzedPhoto(((Set) (obj3)), mediaitem1);
                    }
                    if (((Set) (obj)).size() <= 0) {
                        i1 = byte0;
                        mat = (Mat)obj1;
                    } else {
                        set1 = updateAnalyzedPhotos(((Set) (obj3)));
                        obj3 = set1;
                        set2 = createNewDuplicatesSetAndStartNewOne(((Set) (obj)));
                        obj = set2;
                        i1 = byte0;
                        mat = (Mat)obj1;
                    }
                }
            } else {
                if (((Set) (obj)).size() <= 0) {
                    i1 = byte0;
                    mat = (Mat)obj1;
                } else {
                    set6 = updateAnalyzedPhotos(((Set) (obj3)));
                    obj3 = set6;
                    set7 = createNewDuplicatesSetAndStartNewOne(((Set) (obj)));
                    obj = set7;
                    i1 = byte0;
                    mat = (Mat)obj1;
                }
            }
            l++;
            obj1 = mat;
            byte0 = (byte)i1;
            obj2 = obj3;
        }
        updateAnalyzedPhotos(((Set) (obj2)));
    }

    private Set updateAnalyzedPhotos(Set set)
    {
        if(set.size() > 0)
        {
            DBManager.getInstance().getDaoSession().getMediaItemDao().updateInTx(set);
        }
        return new HashSet();
    }

    public abstract void onFinish();

    protected void onHandleIntent(Intent intent)
    {
        if(CVFeatures.getInstance().isCvInitFailed())
        {
            Log.w(TAG, "failed to load open cv, exiting");
        }
        Date date = new Date();
        Log.i(TAG, (new StringBuilder()).append("start ").append(date.getTime()).toString());
        onStart();
        Log.i(TAG, "scanning local photos");
        processImagesFromSource(1);
        Log.i(TAG, "scanning picasa photos");
        processImagesFromSource(2);
        Log.i(TAG, "finished finding duplicates");
        long l = (new Date()).getTime() - date.getTime();
        Log.i(TAG, (new StringBuilder()).append("timing: done ").append(l).toString());
        onFinish();
        sendBroadcast(new Intent(IntentActions.ACTION_DUPLICATES_FINISHED));
    }

    public abstract void onStart();

    public abstract void onUpdate(float f);

    protected boolean shouldDownloadRemoteItems()
    {
        return false;
    }

    public abstract void waitIfNeeded();

}
