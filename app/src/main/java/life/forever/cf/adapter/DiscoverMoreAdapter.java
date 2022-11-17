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

import com.bumptech.glide.Glide;
import life.forever.cf.R;
import life.forever.cf.entry.DiscoverBean;
import life.forever.cf.publics.tool.CustomIntent;
import life.forever.cf.publics.tool.GlideUtil;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

public class DiscoverMoreAdapter extends RecyclerView.Adapter<DiscoverMoreAdapter.RecomHolder> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    List<DiscoverBean.ResultData.list> mList;
    private OnItemClickListener onItemClickListener;
    private final int mPositions;
    private final String mIsimg;//是否显示推荐为logo  1是  0否
    private final String mLogoUrl;//推荐位logo

    public DiscoverMoreAdapter(Context context, List<DiscoverBean.ResultData.list> List, int positions,String mIsimg,String logoUrl) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mList = List;
        this.mPositions = positions;
        this.mIsimg = mIsimg;
        this.mLogoUrl = logoUrl;
    }

//    public void data(List<MoreBean.ResultData.list> mContentList) {
//        this.mList = mContentList;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public RecomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View convertView = mInflater.inflate(R.layout.discover_more_item, viewGroup, false);
        RecomHolder holder = new RecomHolder(convertView);
        convertView.setTag(holder);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecomHolder holder, final int position) {
        DiscoverBean.ResultData.list bean = mList.get(position);
//        Glide.with(mContext).load("").placeholder(R.drawable.default_work_cover).into(holder.mCover);
        if (mPositions == 3) {
            holder.mRanking.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.mRanking.setBackgroundResource(R.drawable.icon_ranking_one);
                holder.mRanking.setText("1");
            } else if (position == 1) {
                holder.mRanking.setBackgroundResource(R.drawable.icon_ranking_two);
                holder.mRanking.setText("2");
            } else if (position == 2) {
                holder.mRanking.setBackgroundResource(R.drawable.icon_ranking_three);
                holder.mRanking.setText("3");
            } else {
                holder.mRanking.setVisibility(View.GONE);
            }
        } else {
            holder.mRanking.setVisibility(View.GONE);
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < bean.tag.size(); i++) {
            list.add(bean.tag.get(i).tag);
        }
        String tag = Joiner.on("·").join(list);

        GlideUtil.picCache(mContext,bean.h_url,bean.wid+"small",R.drawable.default_work_cover,  holder.mCover);

//        String cover = PlotRead.getConfig().getString(bean.wid+"small","");
//        if (TextUtils.isEmpty(cover)){
////            SharedPreferencesUtil.putString(PlotRead.getConfig(), bean.wid+"small",bean.h_url);
//            GlideUtil.recommentLoad(mContext,bean.wid+"small",bean.h_url,bean.h_url,R.drawable.default_work_cover, holder.mCover);
//        }else{
//            GlideUtil.recommentLoad(mContext,"",cover,bean.h_url,R.drawable.default_work_cover, holder.mCover);
//        }

        holder.mBookName.setText(bean.title);
        holder.mBookAuthor.setText(bean.author);
        holder.mSortName.setText(bean.sort_name+"·"+tag);
        holder.mReadNum.setText(bean.view_num+"View"+"·"+bean.is_vip_str+"·"+bean.is_finish_str);
        holder.mBookInfo.setText(bean.description);

        if (mIsimg != null && mIsimg.equals("1")){
            // GlideUtil.load(mContext,mLogoUrl, R.drawable.add_book_rack,holder.mImgLogo);
            Glide.with(mContext).load(mLogoUrl).into(holder.mImgLogo);
            holder.mImgLogo.setVisibility(View.VISIBLE);
        } else {
            holder.mImgLogo.setVisibility(View.GONE);
        }

        if (mList != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomIntent.intent(mContext, mList, position);
                }
            });
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
        public TextView mRanking;
        public TextView mBookName;
        public TextView mBookAuthor;
        public TextView mSortName;
        public TextView mReadNum;
        public TextView mBookInfo;
        public ImageView mImgLogo;

        public RecomHolder(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.cover);
            mRanking = itemView.findViewById(R.id.ranking);
            mBookName = itemView.findViewById(R.id.book_name);
            mBookAuthor = itemView.findViewById(R.id.book_author);
            mSortName = itemView.findViewById(R.id.sort_name);
            mReadNum = itemView.findViewById(R.id.read_num);
            mBookInfo = itemView.findViewById(R.id.book_info);
            mImgLogo = itemView.findViewById(R.id.imgLogo);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

}
