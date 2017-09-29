package com.flayvr.myrollshared.data;

import de.greenrobot.dao.DaoException;
import java.util.List;

public class ClassifierRule
{

    private List classifierRulePhotos;
    private DaoSession daoSession;
    private Long id;
    private ClassifierRuleDao myDao;
    private String ruleType;
    private Integer totalPhotosDeleted;
    private Integer totalPhotosKept;

    public ClassifierRule()
    {
    }

    public ClassifierRule(Long long1)
    {
        id = long1;
    }

    public ClassifierRule(Long long1, String s, Integer integer, Integer integer1)
    {
        id = long1;
        ruleType = s;
        totalPhotosKept = integer;
        totalPhotosDeleted = integer1;
    }

    public void __setDaoSession(DaoSession daosession)
    {
        daoSession = daosession;
        ClassifierRuleDao classifierruledao;
        if(daosession != null)
        {
            classifierruledao = daosession.getClassifierRuleDao();
        } else
        {
            classifierruledao = null;
        }
        myDao = classifierruledao;
    }

    public void delete()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.delete(this);
            return;
        }
    }

    public List getClassifierRulePhotos()
    {
        if(classifierRulePhotos == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            List list = daoSession.getClassifierRulesToPhotosDao()._queryClassifierRule_ClassifierRulePhotos(id);
            synchronized (this) {
                if (classifierRulePhotos == null)
                    classifierRulePhotos = list;
            }
        }
        return classifierRulePhotos;
    }

    public Long getId()
    {
        return id;
    }

    public String getRuleType()
    {
        return ruleType;
    }

    public RuleType getRuleTypeEnum()
    {
        if(ruleType == null)
        {
            return null;
        } else
        {
            return RuleType.valueOf(ruleType);
        }
    }

    public Integer getTotalPhotosDeleted()
    {
        return totalPhotosDeleted;
    }

    public Integer getTotalPhotosKept()
    {
        return totalPhotosKept;
    }

    public void refresh()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.refresh(this);
            return;
        }
    }

    public void resetClassifierRulePhotos()
    {
        synchronized (this) {
            classifierRulePhotos = null;
        }
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setRuleType(String s)
    {
        ruleType = s;
    }

    public void setRuleTypeEnum(RuleType ruletype)
    {
        if(ruletype == null)
        {
            ruleType = null;
        }
        ruleType = ruletype.name();
    }

    public void setTotalPhotosDeleted(Integer integer)
    {
        totalPhotosDeleted = integer;
    }

    public void setTotalPhotosKept(Integer integer)
    {
        totalPhotosKept = integer;
    }

    public void update()
    {
        if(myDao == null)
        {
            throw new DaoException("Entity is detached from DAO context");
        } else
        {
            myDao.update(this);
            return;
        }
    }

    public enum RuleType
    {
        TOO_BLURRY_FOR_BAD("TOO_BLURRY_FOR_BAD", 0),
        TOO_DARK_FOR_BAD("TOO_DARK_FOR_BAD", 1),
        LOW_SCORE_FOR_BAD("LOW_SCORE_FOR_BAD", 2),
        LOW_SCORE_FOR_REVIEW("LOW_SCORE_FOR_REVIEW", 3),
        SCREENSHOT("SCREENSHOT", 4),
        IN_BORING_FOLDER_AND_NOT_GOOD_ENOUGH("IN_BORING_FOLDER_AND_NOT_GOOD_ENOUGH", 5),
        IN_TEMP_FOLDER("IN_TEMP_FOLDER", 6),
        IN_HIDDEN_MOMENT("IN_HIDDEN_MOMENT", 7),
        REMOVED_FROM_MOMENT("REMOVED_FROM_MOMENT", 8),
        TAKEN_AT_WORK_AND_NOT_GOOD_ENOUGH("TAKEN_AT_WORK_AND_NOT_GOOD_ENOUGH", 9);

        private RuleType(String s, int i)
        {
        }
    }

}
