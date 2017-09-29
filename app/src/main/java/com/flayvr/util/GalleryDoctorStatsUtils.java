package com.flayvr.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.flayvr.application.PreferencesManager;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.cloud.PicasaSessionManager;
import com.flayvr.myrollshared.data.*;
import com.flayvr.myrollshared.utils.GeneralUtils;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.UserEntry;
import de.greenrobot.dao.Property;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class GalleryDoctorStatsUtils
{

    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");
    private static final String TAG = GalleryDoctorStatsUtils.class.getSimpleName();
    private static DriveStat localStat = null;
    private static DriveStat picasaStat = null;
    private static final ExecutorService service = Executors.newFixedThreadPool(1);

    public GalleryDoctorStatsUtils()
    {
    }

    public static Future getDriveStat(int i)
    {
        if(i == 1)
        {
            return service.submit(new Callable() {
                @Override
                public DriveStat call()
                {
                    return GalleryDoctorStatsUtils.getLocalStat();
                }
            });
        }
        if(i == 2)
        {
            return service.submit(new Callable() {
                @Override
                public DriveStat call()
                {
                    if(GalleryDoctorStatsUtils.picasaStat == null)
                    {
                        GalleryDoctorStatsUtils.picasaStat = GalleryDoctorStatsUtils.getPicasaStat();
                    } else
                    {
                        FlayvrApplication.runAction(new Runnable(){
                            @Override
                            public void run() {
                                GalleryDoctorStatsUtils.picasaStat = GalleryDoctorStatsUtils.getPicasaStat();
                            }
                        });
                    }
                    return GalleryDoctorStatsUtils.picasaStat;
                }
            });
        } else
        {
            return null;
        }
    }

    public static int getGalleryHealth(MediaItemStat mediaitemstat, Future future)
    {
        int i = 100;
        if(mediaitemstat.getTotalPhotos() > 0L)
        {
            i = (int)((double)(int)((double)(int)((double)(int)((double)(int)((double)i - Math.min((100D * (double)mediaitemstat.getBadPhotoCount()) / (double)mediaitemstat.getTotalPhotos(), 25D)) - Math.min((100D * (double)mediaitemstat.getDuplicatePhotoCount()) / (double)mediaitemstat.getTotalPhotos(), 25D)) - Math.min((100D * (double)mediaitemstat.getForReviewCount()) / (double)mediaitemstat.getTotalPhotos(), 15D)) - Math.min((100D * (double)mediaitemstat.getWhatsappPhotosCount()) / (double)mediaitemstat.getTotalPhotos(), 10D)) - Math.min((100D * (double)mediaitemstat.getScreenshotsCount()) / (double)mediaitemstat.getTotalPhotos(), 5D));
        }
        DriveStat drivestat;
        if(future != null && future.isDone())
        {
            try
            {
                drivestat = (DriveStat)future.get();
            }
            catch(Exception exception)
            {
                Log.e(TAG, exception.getMessage(), exception);
                drivestat = null;
            }
        } else
        if(localStat != null)
        {
            drivestat = localStat;
        } else
        {
            drivestat = null;
        }
        if(drivestat != null)
        {
            return i - getGalleryHealthPointsForFreeSpace(drivestat);
        } else
        {
            return i;
        }
    }

    private static int getGalleryHealthPointsForFreeSpace(DriveStat drivestat)
    {
        long l = drivestat.getFreeSpace();
        if(l < 200L)
        {
            return 20;
        }
        if(l < 500L)
        {
            return 19;
        }
        if(l < 1000L)
        {
            return 18;
        }
        if(l < 2000L)
        {
            return 15;
        }
        if(l < 4000L)
        {
            return 10;
        }
        return l >= 8000L ? 0 : 5;
    }

    private static DriveStat getLocalStat()
    {
        String as[] = getStorageDirectories();
        DriveStat drivestat = new DriveStat();
        int i = as.length;
        for(int j = 0; j < i; j++)
        {
            File file = new File(as[j]);
            drivestat.setFreeSpace(drivestat.getFreeSpace() + file.getUsableSpace());
            drivestat.setTotalSpace(drivestat.getTotalSpace() + file.getTotalSpace());
        }

        localStat = drivestat;
        return drivestat;
    }

    public static MediaItemStat getMediaItemStat(int i)
    {
        MediaItemStat mediaitemstat = new MediaItemStat();
        mediaitemstat.setSource(i);
        Set set = PreferencesManager.getExcludeAllFolder();
        if(set.size() == 0)
        {
            set.add(Long.valueOf(-1L));
        }
        MediaItemDao mediaitemdao = DBManager.getInstance().getDaoSession().getMediaItemDao();
        String s = com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName;
        String as[] = new String[1 + set.size()];
        as[0] = (new StringBuilder()).append(i).append("").toString();
        Iterator iterator = set.iterator();
        for(int j = 1; iterator.hasNext(); j++)
        {
            as[j] = ((Long)iterator.next()).toString();
        }

        SQLiteDatabase sqlitedatabase = mediaitemdao.getDatabase();
        String s1 = mediaitemdao.getTablename();
        String as1[] = new String[3];
        as1[0] = s;
        as1[1] = (new StringBuilder()).append("SUM(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(") AS ").append("sum").toString();
        as1[2] = (new StringBuilder()).append("COUNT(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(") AS ").append("count").toString();
        Cursor cursor = sqlitedatabase.query(s1, as1, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(makePlaceholders(set.size())).append(")").toString(), as, s, null, null);
        cursor.moveToFirst();
        int k = cursor.getColumnIndex(s);
        int l = cursor.getColumnIndex("sum");
        int i1 = cursor.getColumnIndex("count");
        while(!cursor.isAfterLast()) 
        {
            if(cursor.getInt(k) == 1)
            {
                mediaitemstat.setTotalPhotos(cursor.getLong(i1));
                mediaitemstat.setSizeOfPhotos(cursor.getLong(l));
            } else
            if(cursor.getInt(k) == 2)
            {
                mediaitemstat.setTotalVideos(cursor.getInt(i1));
                mediaitemstat.setSizeOfVideos(cursor.getLong(l));
            }
            cursor.moveToNext();
        }
        cursor.close();
        SQLiteDatabase sqlitedatabase1 = mediaitemdao.getDatabase();
        String s2 = mediaitemdao.getTablename();
        String as2[] = new String[1];
        as2[0] = (new StringBuilder()).append("AVG(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(") AS ").append("avg").toString();
        Cursor cursor1 = sqlitedatabase1.query(s2, as2, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" IN (SELECT ").append(com.flayvr.myrollshared.data.FolderDao.Properties.Id.columnName).append(" FROM ").append("FOLDER").append(" WHERE ").append(com.flayvr.myrollshared.data.FolderDao.Properties.IsCamera.columnName).append(" = 1)").toString(), new String[] {
            "1"
        }, null, null, null);
        cursor1.moveToFirst();
        mediaitemstat.setAvgPhotoSize(cursor1.getLong(0));
        cursor1.close();
        String as3[] = new String[2 + set.size()];
        as3[0] = "1";
        as3[1] = (new StringBuilder()).append(i).append("").toString();
        Iterator iterator1 = set.iterator();
        for(int j1 = 2; iterator1.hasNext(); j1++)
        {
            as3[j1] = ((Long)iterator1.next()).toString();
        }

        SQLiteDatabase sqlitedatabase2 = mediaitemdao.getDatabase();
        String s3 = mediaitemdao.getTablename();
        String as4[] = new String[1];
        as4[0] = (new StringBuilder()).append("COUNT(*) as badPhotos, SUM(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(")").toString();
        Cursor cursor2 = sqlitedatabase2.query(s3, as4, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsBad.columnName).append(" = 1 AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" = 0 ) AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" = 0 ) AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(makePlaceholders(set.size())).append(")").toString(), as3, null, null, null);
        cursor2.moveToFirst();
        mediaitemstat.setBadPhotoCount(cursor2.getLong(0));
        mediaitemstat.setBadPhotoSize(cursor2.getLong(1));
        cursor2.close();
        SQLiteDatabase sqlitedatabase3 = mediaitemdao.getDatabase();
        String s4 = mediaitemdao.getTablename();
        String as5[] = new String[1];
        as5[0] = (new StringBuilder()).append("COUNT(*) as pfr, SUM(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(")").toString();
        Cursor cursor3 = sqlitedatabase3.query(s4, as5, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.IsForReview.columnName).append(" = 1 AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" = 0 ) AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" = 0 ) AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(makePlaceholders(set.size())).append(")").toString(), as3, null, null, null);
        cursor3.moveToFirst();
        mediaitemstat.setForReviewCount(cursor3.getLong(0));
        mediaitemstat.setForReviewSize(cursor3.getLong(1));
        cursor3.close();
        SQLiteDatabase sqlitedatabase4 = mediaitemdao.getDatabase();
        String s5 = mediaitemdao.getTablename();
        String as6[] = new String[1];
        as6[0] = (new StringBuilder()).append("COUNT(*) as dup, SUM(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(")").toString();
        Cursor cursor4 = sqlitedatabase4.query(s5, as6, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" = 0 ) AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" = 0 ) AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName).append(" IN (SELECT ").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.PhotoId.columnName).append(" FROM ").append("DUPLICATES_SETS_TO_PHOTOS").append(") AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(makePlaceholders(set.size())).append(")").toString(), as3, null, null, null);
        cursor4.moveToFirst();
        mediaitemstat.setDuplicatePhotoCount(cursor4.getLong(0));
        long l1 = cursor4.getLong(1);
        cursor4.close();
        SQLiteDatabase sqlitedatabase5 = mediaitemdao.getDatabase();
        String s6 = mediaitemdao.getTablename();
        String as7[] = new String[1];
        as7[0] = (new StringBuilder()).append("COUNT(*) as dup, SUM(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(")").toString();
        Cursor cursor5 = sqlitedatabase5.query(s6, as7, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" = 0 ) AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" = 0 ) AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Id.columnName).append(" IN (SELECT MAX(").append(com.flayvr.myrollshared.data.DuplicatesSetsToPhotosDao.Properties.PhotoId.columnName).append(") FROM ").append("DUPLICATES_SETS_TO_PHOTOS").append(") AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" NOT IN (").append(makePlaceholders(set.size())).append(")").toString(), as3, null, null, null);
        cursor5.moveToFirst();
        mediaitemstat.setDuplicatePhotoSize(l1 - cursor5.getLong(1));
        cursor5.close();
        HashSet hashset = new HashSet(GeneralUtils.getWhatsappFolderIds());
        hashset.removeAll(set);
        HashSet hashset1;
        if(hashset.size() > 0)
        {
            String as10[] = new String[2 + hashset.size()];
            as10[0] = "1";
            as10[1] = (new StringBuilder()).append(i).append("").toString();
            Iterator iterator3 = hashset.iterator();
            for(int i2 = 2; iterator3.hasNext(); i2++)
            {
                as10[i2] = ((Long)iterator3.next()).toString();
            }

            SQLiteDatabase sqlitedatabase7 = mediaitemdao.getDatabase();
            String s8 = mediaitemdao.getTablename();
            String as11[] = new String[1];
            as11[0] = (new StringBuilder()).append("COUNT(*) as pfr, SUM(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(")").toString();
            Cursor cursor7 = sqlitedatabase7.query(s8, as11, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" = 0 ) AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" = 0 ) AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" IN (").append(makePlaceholders(hashset.size())).append(")").toString(), as10, null, null, null);
            cursor7.moveToFirst();
            mediaitemstat.setWhatsappPhotosCount(cursor7.getLong(0));
            mediaitemstat.setWhatsappPhotosSize(cursor7.getLong(1));
            cursor3.close();
        } else
        {
            mediaitemstat.setWhatsappPhotosCount(0L);
            mediaitemstat.setWhatsappPhotosSize(0L);
        }
        hashset1 = new HashSet(GeneralUtils.getScreenshotFoldersIds());
        hashset1.removeAll(set);
        if(hashset1.size() > 0)
        {
            String as8[] = new String[2 + hashset1.size()];
            as8[0] = "1";
            as8[1] = (new StringBuilder()).append(i).append("").toString();
            Iterator iterator2 = hashset1.iterator();
            for(int k1 = 2; iterator2.hasNext(); k1++)
            {
                as8[k1] = ((Long)iterator2.next()).toString();
            }

            SQLiteDatabase sqlitedatabase6 = mediaitemdao.getDatabase();
            String s7 = mediaitemdao.getTablename();
            String as9[] = new String[1];
            as9[0] = (new StringBuilder()).append("COUNT(*) as pfr, SUM(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FileSizeBytes.columnName).append(")").toString();
            Cursor cursor6 = sqlitedatabase6.query(s7, as9, (new StringBuilder()).append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Type.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeleted.columnName).append(" is null AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasKeptByUser.columnName).append(" = 0 ) AND ").append("(").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" is null OR ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.WasDeletedByUser.columnName).append(" = 0 ) AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.Source.columnName).append(" = ? AND ").append(com.flayvr.myrollshared.data.MediaItemDao.Properties.FolderId.columnName).append(" IN (").append(makePlaceholders(hashset1.size())).append(")").toString(), as8, null, null, null);
            cursor6.moveToFirst();
            mediaitemstat.setScreenshotsCount(cursor6.getLong(0));
            mediaitemstat.setScreenshotsSize(cursor6.getLong(1));
            cursor3.close();
            return mediaitemstat;
        } else
        {
            mediaitemstat.setScreenshotsCount(0L);
            mediaitemstat.setScreenshotsSize(0L);
            return mediaitemstat;
        }
    }

    private static DriveStat getPicasaStat()
    {
        PicasaSessionManager picasasessionmanager = PicasaSessionManager.getInstance();
        android.net.Uri.Builder builder = new android.net.Uri.Builder();
        builder.encodedPath("https://picasaweb.google.com/data/entry/api/user");
        builder.appendPath(picasasessionmanager.getUserId());
        String s = builder.build().toString();
        PicasawebService picasawebservice = picasasessionmanager.getPicasaService();
        UserEntry userentry;
        DriveStat drivestat1;
        try
        {
            userentry = (UserEntry)picasawebservice.getEntry(new URL(s), UserEntry.class);
        }
        catch(Exception exception)
        {
            Log.e(TAG, exception.getMessage(), exception);
            DriveStat drivestat = new DriveStat();
            drivestat.setFreeSpace(0L);
            drivestat.setTotalSpace(0L);
            userentry = null;
        }
        drivestat1 = new DriveStat();
        drivestat1.setFreeSpace(userentry.getQuotaLimit().longValue() - userentry.getQuotaUsed().longValue());
        drivestat1.setTotalSpace(userentry.getQuotaLimit().longValue());
        return drivestat1;
    }

    public static String[] getStorageDirectories()
    {
        HashSet hashset;
        String s;
        String s1;
        String s2;
        String s4;
        boolean flag;
        hashset = new HashSet();
        s = System.getenv("EXTERNAL_STORAGE");
        s1 = System.getenv("SECONDARY_STORAGE");
        s2 = System.getenv("EMULATED_STORAGE_TARGET");
        if(!TextUtils.isEmpty(s2)) {
            if(android.os.Build.VERSION.SDK_INT >= 17) {
                String s3 = Environment.getExternalStorageDirectory().getAbsolutePath();
                String as[] = DIR_SEPORATOR.split(s3);
                s4 = as[-1 + as.length];
                try {
                    Integer.valueOf(s4);
                    flag = true;
                }catch(NumberFormatException numberformatexception){
                    flag = false;
                }
                if(!flag)
                    s4 = "";
            }else
                s4 = "";
            if(TextUtils.isEmpty(s4))
                hashset.add(s2);
            else
                hashset.add((new StringBuilder()).append(s2).append(File.separator).append(s4).toString());
        } else {
            if (TextUtils.isEmpty(s))
                hashset.add("/storage/sdcard0");
            else
                hashset.add(s);
        }
        if(!TextUtils.isEmpty(s1))
            Collections.addAll(hashset, s1.split(File.pathSeparator));
        return (String[])hashset.toArray(new String[hashset.size()]);
    }

    private static String makePlaceholders(int i)
    {
        int j = 1;
        if(i < j)
        {
            throw new RuntimeException("No placeholders");
        }
        StringBuilder stringbuilder = new StringBuilder(-1 + i * 2);
        stringbuilder.append("?");
        for(; j < i; j++)
        {
            stringbuilder.append(",?");
        }

        return stringbuilder.toString();
    }

    public static class MediaItemStat
    {

        protected long avgPhotoSize;
        private long badPhotoCount;
        private long badPhotoSize;
        private long duplicatePhotoCount;
        private long duplicatePhotoSize;
        private long forReviewCount;
        private long forReviewSize;
        private long screenshotsCount;
        private long screenshotsSize;
        protected long sizeOfPhotos;
        protected long sizeOfVideos;
        private int source;
        protected long totalPhotos;
        protected long totalVideos;
        private long whatsappPhotosCount;
        private long whatsappPhotosSize;

        public long getAvgPhotoSize()
        {
            return avgPhotoSize;
        }

        public long getBadPhotoCount()
        {
            return badPhotoCount;
        }

        public long getBadPhotoSize()
        {
            return badPhotoSize;
        }

        public long getDuplicatePhotoCount()
        {
            return duplicatePhotoCount;
        }

        public long getDuplicatePhotoSize()
        {
            return duplicatePhotoSize;
        }

        public long getForReviewCount()
        {
            return forReviewCount;
        }

        public long getForReviewSize()
        {
            return forReviewSize;
        }

        public long getScreenshotsCount()
        {
            return screenshotsCount;
        }

        public long getScreenshotsSize()
        {
            return screenshotsSize;
        }

        public long getSizeOfPhotos()
        {
            return sizeOfPhotos;
        }

        public long getSizeOfVideos()
        {
            return sizeOfVideos;
        }

        public int getSource()
        {
            return source;
        }

        public long getTotalPhotos()
        {
            return totalPhotos;
        }

        public long getTotalPhotosSizeToClean()
        {
            return badPhotoSize + forReviewSize + duplicatePhotoSize + whatsappPhotosSize + screenshotsSize;
        }

        public long getTotalVideos()
        {
            return totalVideos;
        }

        public long getWhatsappPhotosCount()
        {
            return whatsappPhotosCount;
        }

        public long getWhatsappPhotosSize()
        {
            return whatsappPhotosSize;
        }

        public void setAvgPhotoSize(long l)
        {
            avgPhotoSize = l;
        }

        public void setBadPhotoCount(long l)
        {
            badPhotoCount = l;
        }

        public void setBadPhotoSize(long l)
        {
            badPhotoSize = l;
        }

        public void setDuplicatePhotoCount(long l)
        {
            duplicatePhotoCount = l;
        }

        public void setDuplicatePhotoSize(long l)
        {
            duplicatePhotoSize = l;
        }

        public void setForReviewCount(long l)
        {
            forReviewCount = l;
        }

        public void setForReviewSize(long l)
        {
            forReviewSize = l;
        }

        public void setScreenshotsCount(long l)
        {
            screenshotsCount = l;
        }

        public void setScreenshotsSize(long l)
        {
            screenshotsSize = l;
        }

        public void setSizeOfPhotos(long l)
        {
            sizeOfPhotos = l;
        }

        public void setSizeOfVideos(long l)
        {
            sizeOfVideos = l;
        }

        public void setSource(int i)
        {
            source = i;
        }

        public void setTotalPhotos(long l)
        {
            totalPhotos = l;
        }

        public void setTotalVideos(long l)
        {
            totalVideos = l;
        }

        public void setWhatsappPhotosCount(long l)
        {
            whatsappPhotosCount = l;
        }

        public void setWhatsappPhotosSize(long l)
        {
            whatsappPhotosSize = l;
        }

        public String toString()
        {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("source: ");
            stringbuilder.append(source);
            stringbuilder.append(", totalPhotos: ");
            stringbuilder.append(totalPhotos);
            stringbuilder.append(", totalVideos: ");
            stringbuilder.append(totalVideos);
            stringbuilder.append(", sizeOfPhotos: ");
            stringbuilder.append(sizeOfPhotos);
            stringbuilder.append(", sizeOfVideos: ");
            stringbuilder.append(sizeOfVideos);
            stringbuilder.append(", avgPhotoSize: ");
            stringbuilder.append(avgPhotoSize);
            stringbuilder.append(", badPhotoCount: ");
            stringbuilder.append(badPhotoCount);
            stringbuilder.append(", badPhotoSize: ");
            stringbuilder.append(badPhotoSize);
            stringbuilder.append(", duplicatePhotoCount: ");
            stringbuilder.append(duplicatePhotoCount);
            stringbuilder.append(", duplicatePhotoSize: ");
            stringbuilder.append(duplicatePhotoSize);
            return stringbuilder.toString();
        }

        public MediaItemStat()
        {
        }
    }


    public static class DriveStat
    {
        protected long freeSpace;
        protected long totalSpace;

        public long getFreeSpace()
        {
            return freeSpace;
        }

        public long getTotalSpace()
        {
            return totalSpace;
        }

        public void setFreeSpace(long l)
        {
            freeSpace = l;
        }

        public void setTotalSpace(long l)
        {
            totalSpace = l;
        }

        public String toString()
        {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("freeSpace: ");
            stringbuilder.append(freeSpace);
            stringbuilder.append(", totalSpace: ");
            stringbuilder.append(totalSpace);
            return stringbuilder.toString();
        }

        public DriveStat()
        {
            freeSpace = 0L;
            totalSpace = 0L;
        }
    }

}
