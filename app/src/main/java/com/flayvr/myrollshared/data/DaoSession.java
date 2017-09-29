package com.flayvr.myrollshared.data;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScope;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.Map;

public class DaoSession extends AbstractDaoSession
{

    private final ClassifierRuleDao classifierRuleDao;
    private final DaoConfig classifierRuleDaoConfig;
    private final ClassifierRulesToPhotosDao classifierRulesToPhotosDao;
    private final DaoConfig classifierRulesToPhotosDaoConfig;
    private final ClassifierThresholdDao classifierThresholdDao;
    private final DaoConfig classifierThresholdDaoConfig;
    private final DBFolderDao dBFolderDao;
    private final DaoConfig dBFolderDaoConfig;
    private final DBMediaItemDao dBMediaItemDao;
    private final DaoConfig dBMediaItemDaoConfig;
    private final DBMomentDao dBMomentDao;
    private final DaoConfig dBMomentDaoConfig;
    private final DBMomentsItemsDao dBMomentsItemsDao;
    private final DaoConfig dBMomentsItemsDaoConfig;
    private final DuplicatesSetDao duplicatesSetDao;
    private final DaoConfig duplicatesSetDaoConfig;
    private final DuplicatesSetsToPhotosDao duplicatesSetsToPhotosDao;
    private final DaoConfig duplicatesSetsToPhotosDaoConfig;
    private final FolderDao folderDao;
    private final DaoConfig folderDaoConfig;
    private final InteractionDao interactionDao;
    private final DaoConfig interactionDaoConfig;
    private final MediaItemDao mediaItemDao;
    private final DaoConfig mediaItemDaoConfig;
    private final MomentDao momentDao;
    private final DaoConfig momentDaoConfig;
    private final MomentsItemsDao momentsItemsDao;
    private final DaoConfig momentsItemsDaoConfig;
    private final UserDao userDao;
    private final DaoConfig userDaoConfig;

    public DaoSession(SQLiteDatabase sqlitedatabase, IdentityScopeType identityscopetype, Map map)
    {
        super(sqlitedatabase);
        dBMediaItemDaoConfig = ((DaoConfig)map.get(DBMediaItemDao.class)).clone();
        dBMediaItemDaoConfig.initIdentityScope(identityscopetype);
        dBMomentDaoConfig = ((DaoConfig)map.get(DBMomentDao.class)).clone();
        dBMomentDaoConfig.initIdentityScope(identityscopetype);
        dBMomentsItemsDaoConfig = ((DaoConfig)map.get(DBMomentsItemsDao.class)).clone();
        dBMomentsItemsDaoConfig.initIdentityScope(identityscopetype);
        dBFolderDaoConfig = ((DaoConfig)map.get(DBFolderDao.class)).clone();
        dBFolderDaoConfig.initIdentityScope(identityscopetype);
        mediaItemDaoConfig = ((DaoConfig)map.get(MediaItemDao.class)).clone();
        mediaItemDaoConfig.initIdentityScope(identityscopetype);
        folderDaoConfig = ((DaoConfig)map.get(FolderDao.class)).clone();
        folderDaoConfig.initIdentityScope(identityscopetype);
        duplicatesSetDaoConfig = ((DaoConfig)map.get(DuplicatesSetDao.class)).clone();
        duplicatesSetDaoConfig.initIdentityScope(identityscopetype);
        duplicatesSetsToPhotosDaoConfig = ((DaoConfig)map.get(DuplicatesSetsToPhotosDao.class)).clone();
        duplicatesSetsToPhotosDaoConfig.initIdentityScope(identityscopetype);
        userDaoConfig = ((DaoConfig)map.get(UserDao.class)).clone();
        userDaoConfig.initIdentityScope(identityscopetype);
        momentDaoConfig = ((DaoConfig)map.get(MomentDao.class)).clone();
        momentDaoConfig.initIdentityScope(identityscopetype);
        momentsItemsDaoConfig = ((DaoConfig)map.get(MomentsItemsDao.class)).clone();
        momentsItemsDaoConfig.initIdentityScope(identityscopetype);
        classifierThresholdDaoConfig = ((DaoConfig)map.get(ClassifierThresholdDao.class)).clone();
        classifierThresholdDaoConfig.initIdentityScope(identityscopetype);
        interactionDaoConfig = ((DaoConfig)map.get(InteractionDao.class)).clone();
        interactionDaoConfig.initIdentityScope(identityscopetype);
        classifierRuleDaoConfig = ((DaoConfig)map.get(ClassifierRuleDao.class)).clone();
        classifierRuleDaoConfig.initIdentityScope(identityscopetype);
        classifierRulesToPhotosDaoConfig = ((DaoConfig)map.get(ClassifierRulesToPhotosDao.class)).clone();
        classifierRulesToPhotosDaoConfig.initIdentityScope(identityscopetype);
        dBMediaItemDao = new DBMediaItemDao(dBMediaItemDaoConfig, this);
        dBMomentDao = new DBMomentDao(dBMomentDaoConfig, this);
        dBMomentsItemsDao = new DBMomentsItemsDao(dBMomentsItemsDaoConfig, this);
        dBFolderDao = new DBFolderDao(dBFolderDaoConfig, this);
        mediaItemDao = new MediaItemDao(mediaItemDaoConfig, this);
        folderDao = new FolderDao(folderDaoConfig, this);
        duplicatesSetDao = new DuplicatesSetDao(duplicatesSetDaoConfig, this);
        duplicatesSetsToPhotosDao = new DuplicatesSetsToPhotosDao(duplicatesSetsToPhotosDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        momentDao = new MomentDao(momentDaoConfig, this);
        momentsItemsDao = new MomentsItemsDao(momentsItemsDaoConfig, this);
        classifierThresholdDao = new ClassifierThresholdDao(classifierThresholdDaoConfig, this);
        interactionDao = new InteractionDao(interactionDaoConfig, this);
        classifierRuleDao = new ClassifierRuleDao(classifierRuleDaoConfig, this);
        classifierRulesToPhotosDao = new ClassifierRulesToPhotosDao(classifierRulesToPhotosDaoConfig, this);
        registerDao(DBMediaItem.class, dBMediaItemDao);
        registerDao(DBMoment.class, dBMomentDao);
        registerDao(DBMomentsItems.class, dBMomentsItemsDao);
        registerDao(DBFolder.class, dBFolderDao);
        registerDao(MediaItem.class, mediaItemDao);
        registerDao(Folder.class, folderDao);
        registerDao(DuplicatesSet.class, duplicatesSetDao);
        registerDao(DuplicatesSetsToPhotos.class, duplicatesSetsToPhotosDao);
        registerDao(User.class, userDao);
        registerDao(Moment.class, momentDao);
        registerDao(MomentsItems.class, momentsItemsDao);
        registerDao(ClassifierThreshold.class, classifierThresholdDao);
        registerDao(Interaction.class, interactionDao);
        registerDao(ClassifierRule.class, classifierRuleDao);
        registerDao(ClassifierRulesToPhotos.class, classifierRulesToPhotosDao);
    }

    public void clear()
    {
        dBMediaItemDaoConfig.getIdentityScope().clear();
        dBMomentDaoConfig.getIdentityScope().clear();
        dBMomentsItemsDaoConfig.getIdentityScope().clear();
        dBFolderDaoConfig.getIdentityScope().clear();
        mediaItemDaoConfig.getIdentityScope().clear();
        folderDaoConfig.getIdentityScope().clear();
        duplicatesSetDaoConfig.getIdentityScope().clear();
        duplicatesSetsToPhotosDaoConfig.getIdentityScope().clear();
        userDaoConfig.getIdentityScope().clear();
        momentDaoConfig.getIdentityScope().clear();
        momentsItemsDaoConfig.getIdentityScope().clear();
        classifierThresholdDaoConfig.getIdentityScope().clear();
        interactionDaoConfig.getIdentityScope().clear();
        classifierRuleDaoConfig.getIdentityScope().clear();
        classifierRulesToPhotosDaoConfig.getIdentityScope().clear();
    }

    public ClassifierRuleDao getClassifierRuleDao()
    {
        return classifierRuleDao;
    }

    public ClassifierRulesToPhotosDao getClassifierRulesToPhotosDao()
    {
        return classifierRulesToPhotosDao;
    }

    public ClassifierThresholdDao getClassifierThresholdDao()
    {
        return classifierThresholdDao;
    }

    public DBFolderDao getDBFolderDao()
    {
        return dBFolderDao;
    }

    public DBMediaItemDao getDBMediaItemDao()
    {
        return dBMediaItemDao;
    }

    public DBMomentDao getDBMomentDao()
    {
        return dBMomentDao;
    }

    public DBMomentsItemsDao getDBMomentsItemsDao()
    {
        return dBMomentsItemsDao;
    }

    public DuplicatesSetDao getDuplicatesSetDao()
    {
        return duplicatesSetDao;
    }

    public DuplicatesSetsToPhotosDao getDuplicatesSetsToPhotosDao()
    {
        return duplicatesSetsToPhotosDao;
    }

    public FolderDao getFolderDao()
    {
        return folderDao;
    }

    public InteractionDao getInteractionDao()
    {
        return interactionDao;
    }

    public MediaItemDao getMediaItemDao()
    {
        return mediaItemDao;
    }

    public MomentDao getMomentDao()
    {
        return momentDao;
    }

    public MomentsItemsDao getMomentsItemsDao()
    {
        return momentsItemsDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }
}
