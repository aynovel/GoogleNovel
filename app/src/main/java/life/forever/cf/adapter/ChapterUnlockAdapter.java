package life.forever.cf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.ChapterUnlockBean;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChapterUnlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final Context context;
    private List<ChapterUnlockBean.ResultData.Lists> mList;
    private OnItemClickListener onItemClickListener;

    public ChapterUnlockAdapter(Context context, List<ChapterUnlockBean.ResultData.Lists> list) {
        this.context = context;
        this.mList = list;
    }

    public void data(List<ChapterUnlockBean.ResultData.Lists> List) {
        this.mList = List;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chapter_unlock_list, parent, FALSE));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new OnItemViewClick(position));
        if (holder instanceof ListViewHolder) {
            ListViewHolder viewHolder = (ListViewHolder) holder;
            ChapterUnlockBean.ResultData.Lists bean = mList.get(position);

            GlideUtil.picCache(context,bean.h_url,bean.cid + "unlock",R.drawable.default_work_cover,  viewHolder.cover);

//            String cover = PlotRead.getConfig().getString(bean.cid + "unlock", "");
//            if (TextUtils.isEmpty(cover)) {
//                SharedPreferencesUtil.putString(PlotRead.getConfig(), bean.cid + "unlock", bean.h_url);
//                GlideUtil.recommentLoad(context,bean.cid + "unlock", bean.h_url, bean.h_url, R.drawable.default_work_cover, viewHolder.cover);
//            } else {
//                GlideUtil.recommentLoad(context, "",cover, bean.h_url, R.drawable.default_work_cover, viewHolder.cover);
//            }
            viewHolder.mTvName.setText(bean.work_name);
            viewHolder.mTvChapter.setText(bean.chapter_name);
            viewHolder.mTvTime.setText(ComYou.timeFormat(Integer.parseInt(bean.addtime), DATE_FORMATTER_10));
            viewHolder.mTvMoney.setTextColor(context.getResources().getColor(R.color.colorBlack));
            viewHolder.mTvMoney.setText("-" + bean.price + " " + bean.unit);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class OnItemViewClick implements View.OnClickListener {
        private final int position;
        OnItemViewClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_chapter)
        TextView mTvChapter;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_money)
        TextView mTvMoney;

        private ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
