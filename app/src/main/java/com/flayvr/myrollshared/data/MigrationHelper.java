package com.flayvr.myrollshared.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.flayvr.myrollshared.application.FlayvrApplication;
import com.flayvr.myrollshared.managers.InteractionManager;

import de.greenrobot.dao.Property;

public class MigrationHelper extends DaoMaster.OpenHelper
{

    private static final String TAG = "flayvr_migration_helper";
    private static MigrationHelper instance;

    public MigrationHelper(Context context, String s, android.database.sqlite.SQLiteDatabase.CursorFactory cursorfactory)
    {
        super(context, s, cursorfactory);
    }

    private void updateInteractions(SQLiteDatabase sqlitedatabase)
    {
        sqlitedatabase.execSQL("ALTER TABLE 'MEDIA_ITEM' ADD 'INTERACTION_SCORE' REAL");
        InteractionDao.dropTable(sqlitedatabase, true);
        InteractionDao.createTable(sqlitedatabase, true);
        FlayvrApplication.runAction(new Runnable() {
            @Override
            public void run() {
                InteractionManager.getInstance().migrate();
            }
        });
    }

    public boolean isFieldExist(String s, String s1, SQLiteDatabase sqlitedatabase)
    {
        Cursor cursor = sqlitedatabase.rawQuery((new StringBuilder()).append("PRAGMA table_info(").append(s).append(")").toString(), null);
        int i = cursor.getColumnIndex("name");
        while(cursor.moveToNext()) 
        {
            if(cursor.getString(i).equals(s1))
            {
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public void onDowngrade(SQLiteDatabase sqlitedatabase, int i, int j)
    {
    }

    public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j)
    {
        Log.d("flayvr_migration_helper", (new StringBuilder()).append("old version: ").append(i).append(" new version: ").append(j).toString());
        if(i < 37)
        {
            sqlitedatabase.execSQL("ALTER TABLE 'DBMEDIA_ITEM' ADD 'SHOULD_RUN_HEAVY_PROCESSING' INTEGER DEFAULT (0)");
            Log.d("flayvr_migration_helper", "added should run heavy processing column");
        }
        if(i < 38)
        {
            sqlitedatabase.execSQL("CREATE INDEX IDX_DBMOMENTS_ITEMS_MOMENT_ID ON DBMOMENTS_ITEMS (MOMENT_ID);");
            Log.d("flayvr_migration_helper", "added db moments to items moments index");
        }
        if(i < 39)
        {
            sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE DBMEDIA_ITEM ADD '").append(DBMediaItemDao.Properties.CenterX.columnName).append("' REAL").toString());
            sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE DBMEDIA_ITEM ADD '").append(DBMediaItemDao.Properties.CenterY.columnName).append("' REAL").toString());
            sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE DBMEDIA_ITEM ADD '").append(DBMediaItemDao.Properties.FacesCount.columnName).append("' INTEGER Default ( -1 )").toString());
            Log.d("flayvr_migration_helper", "added centerX, centerY and faces count fields");
        }
        if(i < 41)
        {
            DaoMaster.createAllTables(sqlitedatabase, true);
            FlayvrApplication.runAction(new Runnable() {
                @Override
                public void run() {
                    InteractionManager.getInstance().migrate();
                }
            });
        } else
        {
            if(i < 42)
            {
                updateInteractions(sqlitedatabase);
                Log.d("flayvr_migration_helper", "added interaction score column - myroll < 42 and GD for myrollshared");
            }
            if(i < 43)
            {
                Log.d("flayvr_migration_helper", "migrating db for myroll shared");
                if(!isFieldExist("MEDIA_ITEM", MediaItemDao.Properties.WasDeletedByUser.columnName, sqlitedatabase))
                {
                    Log.d("flayvr_migration_helper", "migrating db for myroll shared - Myroll");
                    sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE 'MEDIA_ITEM' ADD '").append(MediaItemDao.Properties.WasDeletedByUser.columnName).append("' INTEGER").toString());
                    sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE 'DUPLICATES_SET' ADD '").append(DuplicatesSetDao.Properties.WasAnalyzedByGD.columnName).append("' INTEGER").toString());
                }
                if(!isFieldExist("MEDIA_ITEM", MediaItemDao.Properties.SimilarityScoreToNext.columnName, sqlitedatabase))
                {
                    Log.d("flayvr_migration_helper", "migrating db for myroll shared - Myroll old and GD");
                    sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE 'MEDIA_ITEM' ADD '").append(MediaItemDao.Properties.SimilarityScoreToNext.columnName).append("' REAL").toString());
                    sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE 'MEDIA_ITEM' ADD '").append(MediaItemDao.Properties.SimilarityScoreToPrev.columnName).append("' REAL").toString());
                }
            }
            if(i < 44)
            {
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE MEDIA_ITEM ADD '").append(MediaItemDao.Properties.Source.columnName).append("' INTEGER").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE FOLDER ADD '").append(FolderDao.Properties.Source.columnName).append("' INTEGER").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE FOLDER ADD '").append(FolderDao.Properties.FolderSourceId.columnName).append("' INTEGER").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE MEDIA_ITEM ADD '").append(MediaItemDao.Properties.Width.columnName).append("' INTEGER").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE MEDIA_ITEM ADD '").append(MediaItemDao.Properties.Height.columnName).append("' INTEGER").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("UPDATE MEDIA_ITEM SET ").append(MediaItemDao.Properties.Source.columnName).append(" = ").append(1).toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("UPDATE FOLDER SET ").append(FolderDao.Properties.Source.columnName).append(" = ").append(1).toString());
            }
            if(i < 45)
            {
                sqlitedatabase.execSQL((new StringBuilder()).append("UPDATE FOLDER SET ").append(FolderDao.Properties.FolderSourceId.columnName).append(" = ").append(FolderDao.Properties.Id.columnName).append(" WHERE ").append(FolderDao.Properties.Source.columnName).append(" = ").append(1).toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE FOLDER ADD '").append(FolderDao.Properties.IsUserCreated.columnName).append("' BOOLEAN").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE FOLDER ADD '").append(FolderDao.Properties.IsHidden.columnName).append("' BOOLEAN").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE FOLDER ADD '").append(FolderDao.Properties.FolderPath.columnName).append("' TEXT").toString());
            }
            if(i < 47)
            {
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE CLASSIFIER_THRESHOLD ADD '").append(ClassifierThresholdDao.Properties.BestScore.columnName).append("' REAL").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE CLASSIFIER_THRESHOLD ADD '").append(ClassifierThresholdDao.Properties.BestDirectoryScore.columnName).append("' REAL").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE CLASSIFIER_THRESHOLD ADD '").append(ClassifierThresholdDao.Properties.Source.columnName).append("' INTEGER").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("UPDATE CLASSIFIER_THRESHOLD SET ").append(ClassifierThresholdDao.Properties.Source.columnName).append(" = ").append(1).toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("UPDATE MEDIA_ITEM SET ").append(MediaItemDao.Properties.Color.columnName).append(" = NULL, ").append(MediaItemDao.Properties.CvRan.columnName).append(" = 0").toString());
            }
            if(i < 48)
            {
                sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX IDX_MEDIA_ITEM_DATE ON MEDIA_ITEM (").append(MediaItemDao.Properties.Date.columnName).append(")").toString());
            }
            if(i < 49)
            {
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE MEDIA_ITEM ADD '").append(MediaItemDao.Properties.WasMinimizedByUser.columnName).append("' BOOLEAN").toString());
                sqlitedatabase.execSQL((new StringBuilder()).append("UPDATE MEDIA_ITEM SET ").append(MediaItemDao.Properties.WasMinimizedByUser.columnName).append(" = 0").toString());
            }
            if(i < 50)
            {
                sqlitedatabase.execSQL((new StringBuilder()).append("ALTER TABLE MEDIA_ITEM ADD '").append(MediaItemDao.Properties.ServerId.columnName).append("' INTEGER").toString());
                return;
            }
        }
    }
}
