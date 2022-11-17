package life.forever.cf.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import life.forever.cf.R;
import life.forever.cf.datautils.ISNav;
import life.forever.cf.entry.Image;
import life.forever.cf.manage.pins;
import life.forever.cf.interfaces.OnItemClickListener;
import life.forever.cf.entry.ISListConfig;
import com.yuyh.easyadapter.recyclerview.EasyRVAdapter;
import com.yuyh.easyadapter.recyclerview.EasyRVHolder;

import java.util.List;


public class ImageListAdapter extends EasyRVAdapter<Image> {

    private boolean showCamera;
    private boolean mutiSelect;

    private final ISListConfig config;
    private final Context context;
    private OnItemClickListener listener;

    public ImageListAdapter(Context context, List<Image> list, ISListConfig config) {
        super(context, list, R.layout.item_img_sel, R.layout.item_img_sel_take_photo);
        this.context = context;
        this.config = config;
    }

    @Override
    protected void onBindData(final EasyRVHolder viewHolder, final int position, final Image item) {

        if (position == 0 && showCamera) {
            ImageView iv = viewHolder.getView(R.id.ivTakePhoto);
            iv.setImageResource(R.drawable.ic_take_photo);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageClick(position, item);
                }
            });
            return;
        }

        if (mutiSelect) {
            viewHolder.getView(R.id.ivPhotoCheaked).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int ret = listener.onCheckedClick(position, item);
                        if (ret == 1) { // 局部刷新
                            if (pins.imageList.contains(item.path)) {
                                viewHolder.setImageResource(R.id.ivPhotoCheaked, R.drawable.ic_checked);
                            } else {
                                viewHolder.setImageResource(R.id.ivPhotoCheaked, R.drawable.ic_uncheck);
                            }
                        }
                    }
                }
            });
        }

        viewHolder.setOnItemViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onImageClick(position, item);
            }
        });

        final ImageView iv = viewHolder.getView(R.id.ivImage);
        ISNav.getInstance().displayImage(context, item.path, iv);

        if (mutiSelect) {
            viewHolder.setVisible(R.id.ivPhotoCheaked, true);
            if (pins.imageList.contains(item.path)) {
                viewHolder.setImageResource(R.id.ivPhotoCheaked, R.drawable.ic_checked);
            } else {
                viewHolder.setImageResource(R.id.ivPhotoCheaked, R.drawable.ic_uncheck);
            }
        } else {
            viewHolder.setVisible(R.id.ivPhotoCheaked, false);
        }
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setMutiSelect(boolean mutiSelect) {
        this.mutiSelect = mutiSelect;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showCamera) {
            return 1;
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
