package life.forever.cf.adapter;

import static life.forever.cf.publics.Constant.FALSE;
import static life.forever.cf.publics.Constant.THEME_COLOR;
import static life.forever.cf.publics.Constant.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.ChapterItemBean;
import life.forever.cf.sql.DBUtils;
import life.forever.cf.activtiy.BookManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewReadCatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final List<ChapterItemBean> HRCatalogs;
    private final List<Integer> hotIds;
    private int color, titleColor, drawable;
    public boolean reverse;
    public  String wid;

    private int currentPosition;

    public int getCurrentPosition() {
        return currentPosition;
    }


    private NewReadCatalogAdapter.OnItemClickListener catalogItemClick;

    public void setOnItemClickListener(NewReadCatalogAdapter.OnItemClickListener onItemClickListener) {
        this.catalogItemClick = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder viewHolder,int chapterPos);
    }


    public NewReadCatalogAdapter(Context context, List<ChapterItemBean> Catalogs, List<Integer> hotIds) {
        super();
        mContext = context;
        this.HRCatalogs = Catalogs;
        this.hotIds = hotIds;
        this.titleColor = application.getResources().getColor(R.color.color_656667);
    }


    public void update(int currentChapterOrder) {
        if (reverse) {
            currentPosition = HRCatalogs.size() - 1 - currentChapterOrder;
        } else {
            currentPosition = currentChapterOrder;
        }
        notifyDataSetChanged();
    }

    public void update(int color, int titleColor, int drawable) {
        this.color = color;
        this.titleColor = titleColor;
        this.drawable = drawable;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewReadCatalogAdapter.CatalogViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_read_catalog, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final NewReadCatalogAdapter.CatalogViewHolder viewHolder = (NewReadCatalogAdapter.CatalogViewHolder) holder;
        ChapterItemBean HRCatalog;
        if (reverse) {
            HRCatalog = HRCatalogs.get(HRCatalogs.size() - 1 - position);
        } else {
            HRCatalog = HRCatalogs.get(position);
        }
        viewHolder.title.setText(HRCatalog.getChapterName());
        viewHolder.title.setTextColor(titleColor);
        viewHolder.v_line.setBackgroundColor(color);

        viewHolder.hot.setVisibility(hotIds.contains(HRCatalog.getChapterId()) ? View.VISIBLE : View.GONE);
        viewHolder.status.setVisibility(HRCatalog.getIsvip() == false ? View.GONE : View.VISIBLE);
        if(HRCatalog.getIsvip())
        {
            ChapterItemBean itemBean = DBUtils.getInstance().getChapterItemBean(HRCatalog.getBookID(),HRCatalog.getChapterId());
            if(itemBean != null)
            {
                viewHolder.status.setVisibility(itemBean.getIsvip() == false ? View.GONE : View.VISIBLE);
            }else{
                boolean hasCache = BookManager.isChapterCached(wid, "0", HRCatalog.getChapterId());
                viewHolder.status.setVisibility(hasCache == true ? View.GONE : View.VISIBLE);
            }
        }

//        viewHolder.status.setImageResource(drawable);
        if (position == currentPosition) {
            viewHolder.title.setTextColor(THEME_COLOR);
        } else {
            viewHolder.title.setTextColor(titleColor);
        }

        holder.itemView.setOnClickListener(v -> {
            if (catalogItemClick != null) {
                catalogItemClick.onItemClick(viewHolder,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return HRCatalogs.size();
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.hot)
        View hot;
        @BindView(R.id.status)
        ImageView status;
        @BindView(R.id.v_line)
        View v_line;

        public CatalogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;
    /**
     * 滑动到指定位置
     */
    public void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }
}
