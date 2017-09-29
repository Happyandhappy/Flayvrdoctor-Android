package com.flayvr.myrollshared.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.flayvr.doctor.R;
import com.flayvr.myrollshared.data.DaoHelper;
import com.flayvr.myrollshared.data.Folder;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.*;

public abstract class FoldersCustomizeFragment extends DialogFragment
{

    private ListView list;
    private FoldersCustomizeFragmentListener listener;

    public FoldersCustomizeFragment()
    {
    }

    protected abstract Set<Long> getExludeFolders();

    protected abstract int getTitle();

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            listener = (FoldersCustomizeFragmentListener)activity;
            return;
        }
        catch(ClassCastException classcastexception)
        {
            throw new ClassCastException((new StringBuilder()).append(activity.toString()).append(" must implement FoldersCustomizeFragmentListener").toString());
        }
    }

    public Dialog onCreateDialog(Bundle bundle)
    {
        Dialog dialog = super.onCreateDialog(bundle);
        dialog.getWindow().requestFeature(1);
        return dialog;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        View view = layoutinflater.inflate(R.layout.folder_customize_fragment, viewgroup, false);
        ((TextView)view.findViewById(R.id.title)).setText(getTitle());
        list = (ListView)view.findViewById(R.id.folder_customize_list);
        view.findViewById(R.id.folder_customize_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumAdapter albumadapter = (AlbumAdapter)list.getAdapter();
                Set set = albumadapter.getExclude();
                if(set.size() == albumadapter.getCount())
                {
                    MessageDialog messagedialog = new MessageDialog();
                    messagedialog.setMsg(getResources().getString(R.string.customize_folders_error));
                    messagedialog.show(getActivity().getSupportFragmentManager(), "no_customize_folders");
                    return;
                }
                setExcludeFolders(albumadapter.getCount(), set);
                HashSet hashset = new HashSet();
                for(int i = 0; i < albumadapter.getCount(); i++)
                {
                    Folder folder = (Folder)albumadapter.getItem(i);
                    if(!set.contains(folder.getId()))
                    {
                        hashset.add(folder.getId());
                    }
                }

                listener.refreshAllFolder(hashset);
                dismiss();
            }
        });
        return view;
    }

    public void onViewCreated(View view, Bundle bundle)
    {
        super.onViewCreated(view, bundle);
        setAlbums();
    }

    public void setAlbums()
    {
        List list1 = DaoHelper.getNotHiddenFoldersQueryBuilder(1).list();
        final HashMap photos = new HashMap();
        final HashMap videos = new HashMap();
        Folder folder;
        for(Iterator iterator = list1.iterator(); iterator.hasNext(); videos.put(folder, folder.getNotDeletedVideoCount()))
        {
            folder = (Folder)iterator.next();
            photos.put(folder, folder.getNotDeletedPhotoCount());
        }

        Collections.sort(list1, new Comparator<Folder>() {
            @Override
            public int compare(Folder folder, Folder folder1) {
                long l = ((Long)photos.get(folder1)).longValue() + ((Long)videos.get(folder1)).longValue();
                long l1 = ((Long)photos.get(folder)).longValue() + ((Long)videos.get(folder)).longValue();
                if(l < l1)
                {
                    return -1;
                }
                return l != l1 ? 1 : 0;
            }
        });
        AlbumAdapter albumadapter = new AlbumAdapter(getActivity(), list1, photos, videos);
        list.setAdapter(albumadapter);
    }

    protected abstract void setExcludeFolders(int i, Set set);

    public interface FoldersCustomizeFragmentListener
    {
        public abstract void refreshAllFolder(Set set);
    }

    private class AlbumAdapter extends ArrayAdapter
    {
        private final Set exclude;
        private final LayoutInflater inflater;
        private final Map photos;
        private final Map videos;

        public Set getExclude()
        {
            return exclude;
        }

        public View getView(int i, View view, ViewGroup viewgroup)
        {
            if(view == null)
            {
                view = inflater.inflate(R.layout.folder_customize_item, viewgroup, false);
                ViewHolder viewholder1 = new ViewHolder();
                viewholder1.checkBox = (CheckBox)view.findViewById(R.id.folder_customize_check);
                viewholder1.name = (TextView)view.findViewById(R.id.folder_customize_folder_name);
                viewholder1.photos = (TextView)view.findViewById(R.id.folder_customize_folder_images);
                viewholder1.videos = (TextView)view.findViewById(R.id.folder_customize_folder_videoes);
                view.setTag(viewholder1);

                viewholder1.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Long long1 = (Long)compoundButton.getTag();
                        if(b)
                            exclude.remove(long1);
                        else
                            exclude.add(long1);
                    }
                });
            }
            Folder folder = (Folder)getItem(i);
            ViewHolder viewholder = (ViewHolder)view.getTag();
            viewholder.name.setText(folder.getName());
            TextView textview = viewholder.photos;
            String s = getString(R.string.moment_playing_photos);
            Object aobj[] = new Object[1];
            aobj[0] = photos.get(folder);
            textview.setText(String.format(s, aobj));
            TextView textview1 = viewholder.videos;
            String s1 = getString(R.string.moment_playing_videos);
            Object aobj1[] = new Object[1];
            aobj1[0] = videos.get(folder);
            textview1.setText(String.format(s1, aobj1));
            viewholder.checkBox.setTag(folder.getId());
            CheckBox checkbox = viewholder.checkBox;
            boolean flag;
            if(!exclude.contains(folder.getId()))
                flag = true;
            else
                flag = false;
            checkbox.setChecked(flag);
            return view;
        }


        public AlbumAdapter(Context context, List list1, Map map, Map map1)
        {
            super(context, -1, list1);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            exclude = getExludeFolders();
            photos = map;
            videos = map1;
        }

        private class ViewHolder
        {

            CheckBox checkBox;
            TextView name;
            TextView photos;
            TextView videos;

            private ViewHolder()
            {
                super();
            }
        }
    }

}
