package life.forever.cf.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import life.forever.cf.R;
import life.forever.cf.datautils.ISNav;
import life.forever.cf.entry.Folder;
import life.forever.cf.interfaces.OnFolderChangeListener;
import life.forever.cf.entry.ISListConfig;
import com.yuyh.easyadapter.abslistview.EasyLVAdapter;
import com.yuyh.easyadapter.abslistview.EasyLVHolder;

import java.util.List;


public class FolderListAdapter extends EasyLVAdapter<Folder> {

    private final Context context;
    private final List<Folder> folderList;
    private final ISListConfig config;

    private int selected = 0;
    private OnFolderChangeListener listener;

    public FolderListAdapter(Context context, List<Folder> folderList, ISListConfig config) {
        super(context, folderList, R.layout.item_img_sel_folder);
        this.context = context;
        this.folderList = folderList;
        this.config = config;
    }

    @Override
    public void convert(EasyLVHolder holder, final int position, Folder folder) {
        if (position == 0) {
            holder.setText(R.id.tvFolderName, "所有图片")
                    .setText(R.id.tvImageNum, "共" + getTotalImageSize() + "张");
            ImageView ivFolder = holder.getView(R.id.ivFolder);
            if (folderList.size() > 0) {
                ISNav.getInstance().displayImage(context, folder.cover.path, ivFolder);
            }
        } else {
            holder.setText(R.id.tvFolderName, folder.name)
                    .setText(R.id.tvImageNum, "共" + folder.images.size() + "张");
            ImageView ivFolder = holder.getView(R.id.ivFolder);
            if (folderList.size() > 0) {
                ISNav.getInstance().displayImage(context, folder.cover.path, ivFolder);
            }
        }

        holder.setVisible(R.id.viewLine, position != getCount() - 1);

        holder.setVisible(R.id.indicator, selected == position);

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectIndex(position);
            }
        });
    }

    public void setData(List<Folder> folders) {
        folderList.clear();
        if (folders != null && folders.size() > 0) {
            folderList.addAll(folders);
        }
        notifyDataSetChanged();
    }

    private int getTotalImageSize() {
        int result = 0;
        if (folderList != null && folderList.size() > 0) {
            for (Folder folder : folderList) {
                result += folder.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int position) {
        if (selected == position)
            return;
        if (listener != null)
            listener.onChange(position, folderList.get(position));
        selected = position;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selected;
    }

    public void setOnFloderChangeListener(OnFolderChangeListener listener) {
        this.listener = listener;
    }
}
