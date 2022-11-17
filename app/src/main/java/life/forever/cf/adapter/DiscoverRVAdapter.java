package life.forever.cf.adapter;

import static life.forever.cf.publics.Constant.ZERO;
import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import life.forever.cf.activtiy.WerActivity;
import life.forever.cf.R;
import life.forever.cf.entry.DiscoverBean;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.activtiy.LibraryActivity;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.activtiy.ReadActivity;

import java.io.Serializable;
import java.util.List;

public class DiscoverRVAdapter extends RecyclerView.Adapter<DiscoverRVAdapter.RecomHolder> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final int mType;
    private final List<DiscoverBean.ResultData.list> mList;
    private final List<DiscoverBean.ResultData.list.tag_list> mTagList;
    private final String mTid;
    private String mBid;
    private final String mIsimg;
    private final String mLogoUrl;
    private final String TAG = "DiscoverRVAdapter";

    public DiscoverRVAdapter(Context context, int type, List<DiscoverBean.ResultData.list> mContentList, List<DiscoverBean.ResultData.list.tag_list> tag_list, String Tid,String mIsimg,String logoUrl) {
        super();
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mType = type;
        this.mList = mContentList;
        this.mTagList = tag_list;
        this.mTid = Tid;
        this.mIsimg = mIsimg;
        this.mLogoUrl = logoUrl;
    }

    @NonNull
    @Override
    public RecomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View convertView;
        if (mType == DiscoversAdapter.TYPE_TWO) {

            convertView = mInflater.inflate(R.layout.discover_read_item, viewGroup, false);
        } else if (mType == DiscoversAdapter.TYPE_THREE) {
            convertView = mInflater.inflate(R.layout.discover_hot_item, viewGroup, false);
        } else if (mType == DiscoversAdapter.TYPE_FOUR) {

            convertView = mInflater.inflate(R.layout.discover_grid_item, viewGroup, false);
        } else if (mType == DiscoversAdapter.TYPE_SIX) {

            convertView = mInflater.inflate(R.layout.discover_read_new_item, viewGroup, false);
        } else if (mType == DiscoversAdapter.TYPE_EIGHT) {
            convertView = mInflater.inflate(R.layout.discover_genre_item, viewGroup, false);
        } else if (mType == DiscoversAdapter.TYPE_NINE) {

            convertView = mInflater.inflate(R.layout.discover_tag_item, viewGroup, false);
        } else {

            convertView = mInflater.inflate(R.layout.discover_best_item, viewGroup, false);
        }
        RecomHolder holder = new RecomHolder(convertView);
        convertView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecomHolder holder, final int position) {
        DiscoverBean.ResultData.list bean;
        if (mType == DiscoversAdapter.TYPE_NINE) {
            bean = mList.get(0);
        } else {
            bean = mList.get(position);
        }

        GlideUtil.picCache(mContext,bean.recimg,bean.wid+"small",R.drawable.default_work_cover,  holder.mCover);
        switch (mType) {
            case DiscoversAdapter.TYPE_TWO:
                holder.mBookName.setText(bean.title);
                if ("0".equals(bean.config_num)) {
                    holder.mLLHot.setVisibility(View.GONE);
                } else {
                    holder.mLLHot.setVisibility(View.VISIBLE);
                    holder.mReadNum.setText(bean.config_num);
                }

                if (mIsimg != null && mIsimg.equals("1")){
                    // GlideUtil.load(mContext,mLogoUrl, R.drawable.add_book_rack,holder.mImgLogo);
                    Glide.with(mContext).load(mLogoUrl).into(holder.mImgLogo);
                    holder.mImgLogo.setVisibility(View.VISIBLE);
                } else {
                    holder.mImgLogo.setVisibility(View.GONE);
                }

                break;
            case DiscoversAdapter.TYPE_THREE:

                GlideUtil.picCache(mContext,bean.recimg,bean.id + "bann",R.drawable.default_info_cover,  holder.mCover);

//                String covers = PlotRead.getConfig().getString(bean.id + "bann", "");
//                if (TextUtils.isEmpty(covers)) {
//                    GlideUtil.recommentLoad(mContext, bean.id + "bann", bean.recimg, bean.recimg, R.drawable.default_info_cover, holder.mCover);
//                } else {
//                    GlideUtil.recommentLoad(mContext, "", covers, bean.recimg, R.drawable.default_info_cover, holder.mCover);
//                }
                break;
            case DiscoversAdapter.TYPE_FOUR:

                if (mIsimg != null && mIsimg.equals("1")){
//                GlideUtil.load(mContext,mLogoUrl,R.drawable.default_work_cover,holder.mImgLogo);
                    Glide.with(mContext).load(mLogoUrl).into(holder.mImgLogo);
                    holder.mImgLogo.setVisibility(View.VISIBLE);
                } else {
                    holder.mImgLogo.setVisibility(View.GONE);
                }
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
                holder.mTvName.setText(bean.title);
                holder.mTvAuthor.setText(bean.author);
                holder.mTvInfo.setText(bean.description);
                if ("0".equals(bean.config_num)) {
                    holder.mLLHot.setVisibility(View.GONE);
                } else {
                    holder.mLLHot.setVisibility(View.VISIBLE);
                    holder.mReadNum.setText(bean.config_num);
                }
                break;
            case DiscoversAdapter.TYPE_SIX:

                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                if (position == 0) {
                    holder.itemView.setVisibility(View.GONE);
                    param.height = 0;
                    param.width = 0;
                    param.rightMargin = 0;
                    holder.itemView.setLayoutParams(param);
                }
//                if((mList.size()-1)<=3){
//                    param.rightMargin = 40;
//                    holder.mLLReadItem.setLayoutParams(param);
//                }
                holder.mBookName.setText(bean.title);
                if (mIsimg != null && mIsimg.equals("1")){

//                GlideUtil.load(mContext,mLogoUrl,R.drawable.default_work_cover,holder.mImgLogo);
                    Glide.with(mContext).load(mLogoUrl).into(holder.mImgLogo);
                    holder.mImgLogo.setVisibility(View.VISIBLE);
                } else {
                    holder.mImgLogo.setVisibility(View.GONE);
                }

                break;
            case DiscoversAdapter.TYPE_SEVEN:
                holder.mBookName.setText(bean.title);
                holder.mBookAuthor.setText(bean.author);
                holder.mBookInfo.setText(bean.description);
                holder.mSortName.setText(bean.sortname);

                if ("0".equals(bean.config_num)) {
                    holder.mImgHot.setVisibility(View.INVISIBLE);
                    /*holder.mLLHot.setVisibility(View.GONE);*/
                } else {
                    /*holder.mLLHot.setVisibility(View.VISIBLE);*/
                    holder.mImgHot.setVisibility(View.VISIBLE);
                    holder.mReadNum.setText(bean.config_num);
                }
                if (mIsimg != null && mIsimg.equals("1")){
                    if (holder.mImgLogo != null){
//                    GlideUtil.load(mContext,mLogoUrl,R.drawable.default_work_cover,holder.mImgLogo);
                        Glide.with(mContext).load(mLogoUrl).into(holder.mImgLogo);
                        holder.mImgLogo.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.mImgLogo.setVisibility(View.GONE);
                }

                break;
            case DiscoversAdapter.TYPE_EIGHT:

                GlideUtil.picCache(mContext,bean.recimg,bean.id + "genres",R.drawable.default_work_cover,  holder.mCover);
//
//                String genrescover = PlotRead.getConfig().getString(bean.id + "genres", "");
//                if (TextUtils.isEmpty(genrescover)) {
//                    GlideUtil.recommentLoad(mContext, bean.id + "genres", bean.recimg, bean.recimg, R.drawable.default_work_cover, holder.mCover);
//                } else {
//                    GlideUtil.recommentLoad(mContext, "", genrescover, bean.recimg, R.drawable.default_work_cover, holder.mCover);
//                }
                holder.mTvGenre.setText(bean.title);
                break;
            case DiscoversAdapter.TYPE_NINE:
                holder.mTvTag.setText(bean.tag_list.get(position).tag);
                break;
        }

        if (mList != null) {
            holder.itemView.setOnClickListener(v -> {
                if (mType == DiscoversAdapter.TYPE_NINE) {
                    intents(0, position);
                } else {
                    intents(position, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mTagList == null || mTagList.size() == ZERO) {
            return mList.size();
        }
        return mTagList.size();
    }


    public static class RecomHolder extends RecyclerView.ViewHolder {

        public ImageView mCover;
        public TextView mBookName;
        public LinearLayout mLLHot;
        public ImageView mImgHot;
        public TextView mReadNum;

        //4模块
        public TextView mRanking;
        public TextView mTvName;
        public TextView mTvAuthor;
        public TextView mTvInfo;

        //7模块
        public TextView mBookAuthor;
        public TextView mBookInfo;
        public TextView mSortName;

        public TextView mTvGenre;
        public TextView mTvTag;
        public ImageView mImgLogo;

        public RecomHolder(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.cover);
            mBookName = itemView.findViewById(R.id.book_name);
            mLLHot = itemView.findViewById(R.id.ll_hot);
            mImgHot = itemView.findViewById(R.id.img_hot);
            mReadNum = itemView.findViewById(R.id.read_num);

            mRanking = itemView.findViewById(R.id.ranking);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvAuthor = itemView.findViewById(R.id.tv_author);
            mTvInfo = itemView.findViewById(R.id.tv_info);

            mBookAuthor = itemView.findViewById(R.id.book_author);
            mBookInfo = itemView.findViewById(R.id.book_info);
            mSortName = itemView.findViewById(R.id.sort_name);

            mTvGenre = itemView.findViewById(R.id.tv_genre);
            mTvTag = itemView.findViewById(R.id.tv_tag);

            mImgLogo = itemView.findViewById(R.id.imgLogo);
        }
    }

    public void intents(int position, int positions) {
        if (mList != null && mList.size() > 0) {
            Intent intent = new Intent();
            /*
             * advertise_type: 广告类型 1：作品 2：内部链接 3：外部链接
             * readflag: 0：作品信息 1：阅读
             */
            String advertise_type = mList.get(position).advertise_type;
            if ("1".equals(advertise_type)) {
                String readflag = mList.get(position).advertise_data.readflag;
                int wids = Integer.parseInt(mList.get(position).advertise_data.wid);
                mBid = mList.get(position).advertise_data.wid;
                if ("1".equals(readflag)) {
                    Work work = new Work();
                    work.wid = wids;
                    intent.setClass(mContext, ReadActivity.class);
                    intent.putExtra("work", work);
                    CollBookBean mCollBook  = new CollBookBean();
                    mCollBook.setTitle(work.title);
                    mCollBook.set_id(work.wid+"");
                    intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
                } else {
                    /*int recids = Integer.parseInt(mList.get(position).advertise_data.rec_id);*/
                    intent.setClass(mContext, WorkDetailActivity.class);
                    intent.putExtra("wid", wids);
                    intent.putExtra("recid", 0);
                }
                mContext.startActivity(intent);
            } else if ("2".equals(advertise_type)) {
                mBid = "";
                String ht = mList.get(position).advertise_data.ht;
                String path = mList.get(position).advertise_data.path;
                String ps = mList.get(position).advertise_data.ps;
                String is = mList.get(position).advertise_data.is;
                String su = mList.get(position).advertise_data.su;
                String st = mList.get(position).advertise_data.st;
                String ifreash = mList.get(position).advertise_data.ifreash;
                intent.setClass(mContext, WerActivity.class);

                intent.putExtra("index", ht);
                intent.putExtra("path", path);
                intent.putExtra("pagefresh", ps);
                intent.putExtra("share", is);
                intent.putExtra("shareUrl", su);
                intent.putExtra("shareType", st);
                intent.putExtra("sharefresh", ifreash);
                mContext.startActivity(intent);
            } else if ("3".equals(advertise_type)) {
                mBid = "";
                String url = mList.get(position).advertise_data.url;
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            } else if ("5".equals(advertise_type)) {
                intent.setClass(mContext, LibraryActivity.class);
                intent.putExtra("tagname", mList.get(position).title);
                intent.putExtra("tagtype", "");
                intent.putExtra("sortsid", (Serializable) mList.get(position).advertise_data.sid);
                intent.putExtra("tagid", "");
                mContext.startActivity(intent);
            } else if ("6".equals(advertise_type)) {
                intent.setClass(mContext, LibraryActivity.class);
                intent.putExtra("tagname", mList.get(position).tag_list.get(positions).tag);
                intent.putExtra("tagtype", "");
                intent.putExtra("tagid", mList.get(position).tag_list.get(positions).id);
                mContext.startActivity(intent);
            }



        }
    }
}
