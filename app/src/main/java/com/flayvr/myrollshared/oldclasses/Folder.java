package com.flayvr.myrollshared.oldclasses;


public class Folder
{

    private String folderId;
    private String folderName;

    public Folder(String s, String s1)
    {
        folderId = s;
        folderName = s1;
    }

    public boolean equals(Object obj)
    {
        Folder folder;
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        folder = (Folder)obj;
        if(folderId != null)
        {
            if(folderId.equals(folder.folderId))
                return true;
            return false;
        }
        if(folder.folderId == null)
            return true;
        return false;
    }

    public String getFolderId()
    {
        return folderId;
    }

    public String getFolderName()
    {
        return folderName;
    }

    public int hashCode()
    {
        int i;
        if(folderId == null)
        {
            i = 0;
        } else
        {
            i = folderId.hashCode();
        }
        return i + 31;
    }

    public String toString()
    {
        return (new StringBuilder()).append(getFolderName()).append(" (").append(getFolderId()).append(")").toString();
    }
}
