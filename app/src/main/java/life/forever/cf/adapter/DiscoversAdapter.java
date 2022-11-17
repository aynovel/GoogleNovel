package life.forever.cf.adapter;

import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import life.forever.cf.activtiy.WerActivity;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.DiscoverBean;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.activtiy.TopUpActivity;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.banner.Banner;
import life.forever.cf.publics.banner.BannerConfig;
import life.forever.cf.publics.banner.Transformer;
import life.forever.cf.publics.banner.holder.CustomViewHolder;
import life.forever.cf.publics.banner.listener.OnBannerClickListener;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.activtiy.TaskCenterActivity;
import life.forever.cf.activtiy.ReadActivity;

import java.util.ArrayList;
import java.util.List;


public class DiscoversAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int TYPE_ONE = 0;
    public final static int TYPE_TWO = 1;
    public final static int TYPE_THREE = 2;
    public final static int TYPE_FOUR = 3;
    public final static int TYPE_FIVE = 4;
    public final static int TYPE_SIX = 5;
    public final static int TYPE_SEVEN = 6;
    public final static int TYPE_EIGHT = 7;
    public final static int TYPE_NINE = 8;
    private int TYPE = 100;
    int record = 0;
    boolean isShow = true;
    private final Context mContext;
    private final List<String> beans;

    List<DiscoverBean.ResultData> mContactList;
    private String mBid;

    public DiscoversAdapter(Context context, List<DiscoverBean.ResultData> contactList) {
        this.mContext = context;
        this.mContactList = contactList;
        this.beans = new ArrayList<>();
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public void add(String bean) {
        this.beans.add(bean);
        notifyDataSetChanged();
    }

    public void update() {
        if (this.beans != null) {
            this.beans.clear();
        }
        notifyDataSetChanged();
    }

    public void data(List<DiscoverBean.ResultData> contactList) {
        this.mContactList = contactList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (record == 0) {
            if (("0").equals(beans.get(position))) {
                isShow = true;
            }
        }

        record++;


        if (("0").equals(beans.get(position))) {
            TYPE = TYPE_ONE;
        } else if (("2").equals(beans.get(position))) {
            TYPE = TYPE_TWO;
        } else if (("3").equals(beans.get(position))) {
            TYPE = TYPE_THREE;
        } else if (("4").equals(beans.get(position))) {
            TYPE = TYPE_FOUR;
        } else if (("5").equals(beans.get(position))) {
            TYPE = TYPE_FIVE;
        } else if (("6").equals(beans.get(position))) {
            TYPE = TYPE_SIX;
        } else if (("7").equals(beans.get(position))) {
            TYPE = TYPE_SEVEN;
        } else if (("11").equals(beans.get(position))) {
            TYPE = TYPE_EIGHT;
        } else if (("12").equals(beans.get(position))) {
            TYPE = TYPE_NINE;
        }
        return TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case TYPE_ONE:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_zero, parent, false);
                holder = new ViewHolderOne(view);
                break;
            case TYPE_TWO:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_one, parent, false);
                holder = new ViewHolderTwo(view);
                break;
            case TYPE_THREE:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_one, parent, false);
                holder = new ViewHolderThree(view);
                break;
            case TYPE_FOUR:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_one, parent, false);
                holder = new ViewHolderFour(view);
                break;
            case TYPE_FIVE:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_four, parent, false);
                holder = new ViewHolderFive(view);
                break;
            case TYPE_SIX:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_one, parent, false);
                holder = new ViewHolderSix(view);
                break;
            case TYPE_SEVEN:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_one, parent, false);
                holder = new ViewHolderSeven(view);
                break;
            case TYPE_EIGHT:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_one, parent, false);
                holder = new ViewHolderEight(view);
                break;
            case TYPE_NINE:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_item, parent, false);
                holder = new ViewHolderNine(view);
                break;
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.discover_empty_item, parent, false);
                holder = new ViewHolderEmpty(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int positions) {
        LinearLayoutManager HLayoutManager = new LinearLayoutManager(mContext);
        HLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);


        LinearLayoutManager VLayoutManager = new LinearLayoutManager(mContext);
        VLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        StaggeredGridLayoutManager StaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.HORIZONTAL);
        GridLayoutManager TwoGridLayoutManager = new GridLayoutManager(mContext, 2, RecyclerView.HORIZONTAL, false);
        GridLayoutManager ThreeGridLayoutManager = new GridLayoutManager(mContext, 3, RecyclerView.HORIZONTAL, false);
        if (holder instanceof ViewHolderOne) {
            ArrayList<String> list_path = new ArrayList<>();
            for (int i = 0; i < mContactList.get(positions).list.size(); i++) {
                list_path.add(mContactList.get(positions).list.get(i).recimg);
            }

            ((ViewHolderOne) holder).mBanner.setAutoPlay(true)
                    .setPages(list_path, new CustomViewHolder())
                    .setBannerStyle(BannerConfig.NOT_INDICATOR)
                    .setBannerAnimation(Transformer.Scale)
                    .start();
            ((ViewHolderOne) holder).mBanner.setOnBannerClickListener(new OnBannerClickListener() {
                @Override
                public void onBannerClick(List datas, int position) {
                    ReadIntent(positions, position);
                }
            });
            GlideUtil.loadDetail(mContext, list_path.get(positions), ((ViewHolderOne) holder).the_fuzzy);
            changeLight(((ViewHolderOne) holder).the_fuzzy,-40);


            ((ViewHolderOne) holder).mBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    GlideUtil.loadDetail(mContext, list_path.get(position), ((ViewHolderOne) holder).the_fuzzy);
                    setAlphaAnimation(((ViewHolderOne) holder).the_fuzzy);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } else if (holder instanceof ViewHolderTwo) {
            ((ViewHolderTwo) holder).mTitle.setText(mContactList.get(positions).title);
            ((ViewHolderTwo) holder).mTwoViewMore.setOnClickListener(v -> MoreIntent(positions));

            ((ViewHolderTwo) holder).mTwoRecyclerView.setLayoutManager(HLayoutManager);
            ((ViewHolderTwo) holder).mTwoRecyclerView.setHasFixedSize(true);
            ((ViewHolderTwo) holder).mTwoRecyclerView.setItemAnimator(null);
            DiscoverRVAdapter mTwoRVAdapter = new DiscoverRVAdapter(mContext, TYPE_TWO, mContactList.get(positions).list, null, mContactList.get(positions).rec_id,mContactList.get(positions).isimg,mContactList.get(positions).recimg);
            ((ViewHolderTwo) holder).mTwoRecyclerView.setAdapter(mTwoRVAdapter);
        } else if (holder instanceof ViewHolderThree) {
            ((ViewHolderThree) holder).mTitle.setText(mContactList.get(positions).title);
            ((ViewHolderThree) holder).mThreeViewMore.setVisibility(View.GONE);
            ((ViewHolderThree) holder).mThreeRecyclerView.setLayoutManager(HLayoutManager);
            ((ViewHolderThree) holder).mThreeRecyclerView.setHasFixedSize(true);
            ((ViewHolderThree) holder).mThreeRecyclerView.setItemAnimator(null);

            DiscoverRVAdapter mThreeRVAdapter = new DiscoverRVAdapter(mContext, TYPE_THREE, mContactList.get(positions).list, null, mContactList.get(positions).rec_id,mContactList.get(positions).isimg,mContactList.get(positions).recimg);
            ((ViewHolderThree) holder).mThreeRecyclerView.setAdapter(mThreeRVAdapter);
        } else if (holder instanceof ViewHolderFour) {
            ((ViewHolderFour) holder).mTitle.setText(mContactList.get(positions).title);
            ((ViewHolderFour) holder).mFourViewMore.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, DiscoverMoreActivity.class);
                intent.putExtra("positions", 3);
                intent.putExtra("rec_id", Integer.parseInt(mContactList.get(positions).rec_id));
                mContext.startActivity(intent);
            });
            ((ViewHolderFour) holder).mFourRecyclerView.setLayoutManager(ThreeGridLayoutManager);
            ((ViewHolderFour) holder).mFourRecyclerView.setHasFixedSize(true);
            ((ViewHolderFour) holder).mFourRecyclerView.setItemAnimator(null);

            DiscoverRVAdapter mGridRecylerViewAdapter = new DiscoverRVAdapter(mContext, TYPE_FOUR, mContactList.get(positions).list, null, mContactList.get(positions).rec_id,mContactList.get(positions).isimg,mContactList.get(positions).recimg);
            ((ViewHolderFour) holder).mFourRecyclerView.setAdapter(mGridRecylerViewAdapter);
        } else if (holder instanceof ViewHolderFive) {

            DiscoverBean.ResultData.list bean = mContactList.get(positions).list.get(TYPE_ONE);

            GlideUtil.picCache(mContext, bean.h_url,bean.wid + "small",R.drawable.default_work_cover, ((ViewHolderFive) holder).mRoundImageView);


//            String cover = PlotRead.getConfig().getString(bean.wid + "small", "");
//            if (TextUtils.isEmpty(cover)) {
//                GlideUtil.recommentLoad(mContext, bean.wid + "small", bean.recimg, bean.recimg, R.drawable.default_work_cover, ((ViewHolderFive) holder).mRoundImageView);
//            } else {
//                GlideUtil.recommentLoad(mContext, "", cover, bean.recimg, R.drawable.default_work_cover, ((ViewHolderFive) holder).mRoundImageView);
//            }

            ((ViewHolderFive) holder).mRoundImageView.setOnClickListener(v -> ReadIntent(positions, TYPE_ONE));
        } else if (holder instanceof ViewHolderSix) {
            ((ViewHolderSix) holder).mTitle.setText(mContactList.get(positions).title);

            ((ViewHolderSix) holder).mSixViewMore.setOnClickListener(v -> MoreIntent(positions));

            DiscoverBean.ResultData.list bean = mContactList.get(positions).list.get(0);
            GlideUtil.picCache(mContext,  bean.recimg,bean.recimg + "small",R.drawable.default_work_cover, ((ViewHolderSix) holder).mRoundImageView);

//            String cover = PlotRead.getConfig().getString(bean.wid + "small", "");
//            if (TextUtils.isEmpty(cover)) {
//                GlideUtil.recommentLoad(mContext, bean.wid + "small", bean.recimg, bean.recimg, R.drawable.default_work_cover, ((ViewHolderSix) holder).mRoundImageView);
//            } else {
//                GlideUtil.recommentLoad(mContext, "", cover, bean.recimg, R.drawable.default_work_cover, ((ViewHolderSix) holder).mRoundImageView);
//            }
            String isimg = mContactList.get(positions).isimg;
            String logoUrl = mContactList.get(positions).recimg;
            if (isimg != null && isimg.equals("1")){
                ((ViewHolderSix) holder).mImgLogo.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(logoUrl).into(((ViewHolderSix) holder).mImgLogo);
            } else {
                ((ViewHolderSix) holder).mImgLogo.setVisibility(View.GONE);
            }

            ((ViewHolderSix) holder).mBookName.setText(bean.title);
            ((ViewHolderSix) holder).mBookInfo.setText(bean.description);
            ((ViewHolderSix) holder).mBookAuthor.setText(bean.author);
            ((ViewHolderSix) holder).mSortName.setText(bean.sortname);
            ((ViewHolderSix) holder).mLLBook.setOnClickListener(v -> ReadIntent(positions, 0));

            ((ViewHolderSix) holder).mSixRecyclerView.setLayoutManager(HLayoutManager);
            ((ViewHolderSix) holder).mSixRecyclerView.setHasFixedSize(true);
            ((ViewHolderSix) holder).mSixRecyclerView.setItemAnimator(null);

            DiscoverRVAdapter mSixRVAdapter = new DiscoverRVAdapter(mContext, TYPE_SIX, mContactList.get(positions).list, null, mContactList.get(positions).rec_id,mContactList.get(positions).isimg,mContactList.get(positions).recimg);
            ((ViewHolderSix) holder).mSixRecyclerView.setAdapter(mSixRVAdapter);
        } else if (holder instanceof ViewHolderSeven) {
            ((ViewHolderSeven) holder).mTitle.setText(mContactList.get(positions).title);
            ((ViewHolderSeven) holder).mSevenViewMore.setOnClickListener(v -> MoreIntent(positions));
            ((ViewHolderSeven) holder).mSevenRecyclerView.setLayoutManager(VLayoutManager);

            ((ViewHolderSeven) holder).mSevenRecyclerView.setHasFixedSize(true);
            ((ViewHolderSeven) holder).mSevenRecyclerView.setItemAnimator(null);

            DiscoverRVAdapter mSevenRVAdapter = new DiscoverRVAdapter(mContext, TYPE_SEVEN, mContactList.get(positions).list, null, mContactList.get(positions).rec_id,mContactList.get(positions).isimg,mContactList.get(positions).recimg);
            ((ViewHolderSeven) holder).mSevenRecyclerView.setAdapter(mSevenRVAdapter);
        } else if (holder instanceof ViewHolderEight) {
            ((ViewHolderEight) holder).mTitle.setText(mContactList.get(positions).title);
            ((ViewHolderEight) holder).mEightViewMore.setVisibility(View.GONE);
            ((ViewHolderEight) holder).mEightRecyclerView.setLayoutManager(TwoGridLayoutManager);
            ((ViewHolderEight) holder).mEightRecyclerView.setHasFixedSize(true);
            ((ViewHolderEight) holder).mEightRecyclerView.setItemAnimator(null);

            DiscoverRVAdapter mEightRVAdapter = new DiscoverRVAdapter(mContext, TYPE_EIGHT, mContactList.get(positions).list, null, mContactList.get(positions).rec_id,mContactList.get(positions).isimg,mContactList.get(positions).recimg);
            ((ViewHolderEight) holder).mEightRecyclerView.setAdapter(mEightRVAdapter);
        } else if (holder instanceof ViewHolderNine) {
            ((ViewHolderNine) holder).mLayoutRoot.setBackgroundResource(R.color.color_F7FAF3);
            ((ViewHolderNine) holder).mTitle.setText(mContactList.get(positions).title);
            ((ViewHolderNine) holder).mNineViewMore.setVisibility(View.GONE);
            ((ViewHolderNine) holder).mNineRecyclerView.setLayoutManager(StaggeredGridLayoutManager);
            ((ViewHolderNine) holder).mNineRecyclerView.setHasFixedSize(true);
            ((ViewHolderNine) holder).mNineRecyclerView.setItemAnimator(null);
            DiscoverRVAdapter mNineRVAdapter = new DiscoverRVAdapter(mContext, TYPE_NINE, mContactList.get(positions).list, mContactList.get(positions).list.get(0).tag_list, mContactList.get(positions).rec_id,mContactList.get(positions).isimg,mContactList.get(positions).recimg);
            ((ViewHolderNine) holder).mNineRecyclerView.setAdapter(mNineRVAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }


    static class ViewHolderOne extends RecyclerView.ViewHolder {
        Banner mBanner;
        ImageView the_fuzzy;

        public ViewHolderOne(View itemView) {
            super(itemView);
            mBanner = itemView.findViewById(R.id.banner);
            the_fuzzy = itemView.findViewById(R.id.the_fuzzy);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {
        TextView mTitle;
        LinearLayout mTwoViewMore;
        RecyclerView mTwoRecyclerView;

        public ViewHolderTwo(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mTwoViewMore = itemView.findViewById(R.id.more);
            mTwoRecyclerView = itemView.findViewById(R.id.rcv_content);
        }
    }

    static class ViewHolderThree extends RecyclerView.ViewHolder {
        TextView mTitle;
        LinearLayout mThreeViewMore;
        RecyclerView mThreeRecyclerView;

        public ViewHolderThree(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mThreeViewMore = itemView.findViewById(R.id.more);
            mThreeRecyclerView = itemView.findViewById(R.id.rcv_content);
        }
    }

    static class ViewHolderFour extends RecyclerView.ViewHolder {
        TextView mTitle;
        LinearLayout mFourViewMore;
        RecyclerView mFourRecyclerView;

        public ViewHolderFour(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mFourViewMore = itemView.findViewById(R.id.more);
            mFourRecyclerView = itemView.findViewById(R.id.rcv_content);

        }
    }

    static class ViewHolderFive extends RecyclerView.ViewHolder {
        ImageView mRoundImageView;

        public ViewHolderFive(View itemView) {
            super(itemView);
            mRoundImageView = itemView.findViewById(R.id.banner_img);

        }
    }

    static class ViewHolderSix extends RecyclerView.ViewHolder {
        TextView mTitle;
        LinearLayout mSixViewMore;

        LinearLayout mLLBook;
        ImageView mRoundImageView;
        TextView mBookName;
        TextView mBookInfo;
        TextView mBookAuthor;
        TextView mSortName;
        ImageView mImgLogo;

        RecyclerView mSixRecyclerView;

        public ViewHolderSix(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mSixViewMore = itemView.findViewById(R.id.more);

            mLLBook = itemView.findViewById(R.id.ll_book);
            mLLBook.setVisibility(View.VISIBLE);
            mRoundImageView = itemView.findViewById(R.id.cover);
            mBookName = itemView.findViewById(R.id.book_name);
            mBookInfo = itemView.findViewById(R.id.book_info);
            mBookAuthor = itemView.findViewById(R.id.book_author);
            mSortName = itemView.findViewById(R.id.sort_name);
            mSixRecyclerView = itemView.findViewById(R.id.rcv_content);
            mImgLogo = itemView.findViewById(R.id.imgLogo);

        }
    }

    static class ViewHolderSeven extends RecyclerView.ViewHolder {
        TextView mTitle;
        LinearLayout mSevenViewMore;
        RecyclerView mSevenRecyclerView;

        public ViewHolderSeven(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mSevenViewMore = itemView.findViewById(R.id.more);
            mSevenRecyclerView = itemView.findViewById(R.id.rcv_content);
        }
    }

    static class ViewHolderEight extends RecyclerView.ViewHolder {
        TextView mTitle;
        LinearLayout mEightViewMore;
        RecyclerView mEightRecyclerView;

        public ViewHolderEight(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mEightViewMore = itemView.findViewById(R.id.more);
            mEightRecyclerView = itemView.findViewById(R.id.rcv_content);
        }
    }

    static class ViewHolderNine extends RecyclerView.ViewHolder {
        LinearLayout mLayoutRoot;
        TextView mTitle;
        LinearLayout mNineViewMore;
        RecyclerView mNineRecyclerView;

        public ViewHolderNine(View itemView) {
            super(itemView);
            mLayoutRoot = itemView.findViewById(R.id.layout_root);
            mTitle = itemView.findViewById(R.id.tv_name);
            mNineViewMore = itemView.findViewById(R.id.more);
            mNineRecyclerView = itemView.findViewById(R.id.rcv_content);
        }
    }

    static class ViewHolderEmpty extends RecyclerView.ViewHolder {

        LinearLayout mLayoutEmpty;

        public ViewHolderEmpty(View itemView) {
            super(itemView);
            mLayoutEmpty = itemView.findViewById(R.id.layout_empty);
            mLayoutEmpty.setVisibility(View.GONE);
        }
    }

    private void MoreIntent(int positions) {
        if (mContactList != null && mContactList.size() > 0) {
            Intent intent = new Intent(mContext, DiscoverMoreActivity.class);
            intent.putExtra("rec_id", Integer.parseInt(mContactList.get(positions).rec_id));
            mContext.startActivity(intent);
        }
    }

    private void ReadIntent(int positions, int positiontype) {
        if (mContactList != null && mContactList.size() > 0) {
            Intent intent = new Intent();
            /*
             * advertise_type: 广告类型 1：作品 2：内部链接 3：外部链接
             * readflag: 0：作品信息 1：阅读
             */
            String mTid = mContactList.get(positions).rec_id;
            String advertise_type = mContactList.get(positions).list.get(positiontype).advertise_type;
            if ("1".equals(advertise_type)) {
                String readflag = mContactList.get(positions).list.get(positiontype).advertise_data.readflag;
                int wids = Integer.parseInt(mContactList.get(positions).list.get(positiontype).advertise_data.wid);
                mBid = mContactList.get(positions).list.get(positiontype).advertise_data.wid;
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
                    int recids = Integer.parseInt(mContactList.get(positions).rec_id);
                    intent.setClass(mContext, WorkDetailActivity.class);
                    intent.putExtra("wid", wids);
                    intent.putExtra("recid", recids);
                }
                mContext.startActivity(intent);

            } else if ("2".equals(advertise_type)) {
                mBid = "";
                String ht = mContactList.get(positions).list.get(positiontype).advertise_data.ht;
                String path = mContactList.get(positions).list.get(positiontype).advertise_data.path;
                String ps = mContactList.get(positions).list.get(positiontype).advertise_data.ps;
                String is = mContactList.get(positions).list.get(positiontype).advertise_data.is;
                String su = mContactList.get(positions).list.get(positiontype).advertise_data.su;
                String st = mContactList.get(positions).list.get(positiontype).advertise_data.st;
                String ifreash = mContactList.get(positions).list.get(positiontype).advertise_data.ifreash;
                intent.setClass(mContext, WerActivity.class);

                intent.putExtra("index", ht);
                intent.putExtra("path", path);
                intent.putExtra("pagefresh", ps);
                intent.putExtra("share", is);
                intent.putExtra("shareUrl", su);
                intent.putExtra("shareType", st);
                intent.putExtra("sharefresh", ifreash);
            /*intent.putExtra("shareTitle", ad.shareTitle);
            intent.putExtra("shareDesc", ad.shareDesc);
            intent.putExtra("shareImg", ad.shareImg);*/
                mContext.startActivity(intent);
            } else if ("3".equals(advertise_type)) {
                mBid = "";
                String url = mContactList.get(positions).list.get(positiontype).advertise_data.url;
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            } else if ("4".equals(advertise_type)) {
                if ("4".equals(mContactList.get(positions).list.get(positiontype).advertise_data.homeindex)) {
                    if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
                        intent.setClass(mContext, TopUpActivity.class);
                    } else {
                        intent.setClass(mContext, LoginActivity.class);
                    }
                } else if ("6".equals(mContactList.get(positions).list.get(positiontype).advertise_data.homeindex)){
                    if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
                        intent.setClass(mContext, TaskCenterActivity.class);
                    } else {
                        intent.setClass(mContext, LoginActivity.class);
                    }
                }
                mContext.startActivity(intent);
            }



        }
    }

    /*
     *改变图片的亮度方法 0--原样  >0---调亮  <0---调暗
     */
    private void changeLight(ImageView imageView, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0,
                brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
    }

    public static void setAlphaAnimation(View v) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", .7f, 1f);
        fadeIn.setDuration(600);
        final AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(fadeIn);
        mAnimationSet.start();
    }

}
