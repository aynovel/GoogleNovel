package life.forever.cf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.RecList;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

public class ReadCommendMoreAdapter extends RecyclerView.Adapter<ReadCommendMoreAdapter.RecomHolder> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    List<RecList>  mList;
    private OnItemClickListener onItemClickListener;
    private final int mPositions;

    public ReadCommendMoreAdapter(Context context, List<RecList> mReadRecLists, int positions) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mList = mReadRecLists;
        this.mPositions = positions;
    }

    @NonNull
    @Override
    public RecomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View convertView = mInflater.inflate(R.layout.read_commend_more_item, viewGroup, false);
        RecomHolder holder = new RecomHolder(convertView);
        convertView.setTag(holder);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecomHolder holder, final int position) {
        holder.itemView.setOnClickListener(new OnItemViewClick(position));
        RecList bean = mList.get(position);
        if (mPositions == 3) {

        }

        GlideUtil.picCache(mContext,bean.h_url,bean.wid+"read_exit_small",R.drawable.default_work_cover, holder.mCover);

//        String cover = PlotRead.getConfig().getString(bean.wid+"read_exit_small","");
//        if (TextUtils.isEmpty(cover)){
//            GlideUtil.recommentLoad(mContext,bean.wid+"read_exit_small",bean.h_url,bean.h_url,R.drawable.default_work_cover, holder.mCover);
//        }else{
//            GlideUtil.recommentLoad(mContext,"",cover,bean.h_url,R.drawable.default_work_cover, holder.mCover);
//        }

        if (!TextUtils.isEmpty(bean.title)) {
            holder.mBookName.setText(bean.title);
        }

        if (!TextUtils.isEmpty(bean.author)) {
            holder.mBookInfo.setText(bean.author);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
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


    public static class RecomHolder extends RecyclerView.ViewHolder {

        public ImageView mCover;
        public TextView mBookName;
        public TextView mBookInfo;

        public RecomHolder(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.cover);
            mBookName = itemView.findViewById(R.id.sort_name);
            mBookInfo = itemView.findViewById(R.id.book_info);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

}
