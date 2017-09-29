package com.flayvr.screens.settings;

import com.flayvr.application.PreferencesManager;
import com.flayvr.doctor.R;
import com.flayvr.myrollshared.fragments.FoldersCustomizeFragment;
import java.util.Set;

public class ScanningFolderCustomizeFragment extends FoldersCustomizeFragment
{

    public ScanningFolderCustomizeFragment()
    {
    }

    protected Set getExludeFolders()
    {
        return PreferencesManager.getExcludeAllFolder();
    }

    protected int getTitle()
    {
        return R.string.customize_folders_title;
    }

    protected void setExcludeFolders(int i, Set set)
    {
        PreferencesManager.setExcludeAllFolder(set);
    }
}
