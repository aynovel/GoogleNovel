package life.forever.cf.publics.weight;

import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.WerActivity;
import life.forever.cf.R;
import life.forever.cf.entry.AD;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.activtiy.ReadActivity;

import java.util.List;

public class AutoRollBanner extends FrameLayout {

    private final int MESSAGE = 0;
    private final int DELAYED = 3000;

    private final LinearLayoutManager mLinearLayoutManager;
    private final RecyclerView mRecyclerView;
    private final LinearLayout mDots;

    // 多个推荐轮播
    private List<AD> banners;
    private MultiBannerAdapter mMultiAdapter;
    private int current;

    // 单个推荐不轮播
    private AD banner;
    private SingleBannerAdapter mSingleAdapter;

    private final Drawable dot_src_normal;
    private final Drawable dot_src_select;
    private final int marginBetweenDots;
    private final int dotsBottomMargin;
    private final int dotSize;
    private final int DP10;

    /**
     * 1：详情页banner 2：签到页banner
     */
    private int type;

    public AutoRollBanner(@NonNull Context context) {
        this(context, null);
    }

    public AutoRollBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DP10 = DisplayUtil.dp2px(getContext(), 10);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AutoRollBanner);
        dot_src_normal = array.getDrawable(R.styleable.AutoRollBanner_normalDotSrc);
        dot_src_select = array.getDrawable(R.styleable.AutoRollBanner_selectDotSrc);
        marginBetweenDots = array.getDimensionPixelOffset(R.styleable.AutoRollBanner_marginBetweenDots, DP10);
        dotsBottomMargin = array.getDimensionPixelOffset(R.styleable.AutoRollBanner_dotsBottomMargin, DP10);
        dotSize = array.getDimensionPixelOffset(R.styleable.AutoRollBanner_dotSize, DP10);
        array.recycle();

        LayoutInflater.from(context).inflate(R.layout.view_auto_roll_banner, this, true);
        mRecyclerView = findViewById(R.id.recyclerView);
        mDots = findViewById(R.id.dots);
        mLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (banners == null || mMultiAdapter == null) {
                    return;
                }
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        mHandler.removeMessages(MESSAGE);
                        current = mLinearLayoutManager.findFirstVisibleItemPosition();

                        for (int i = 0; i < mDots.getChildCount(); i++) {
                            ImageView dot = (ImageView) mDots.getChildAt(i);
                            if (i == current % banners.size()) {
                                dot.setImageDrawable(dot_src_select);
                            } else {
                                dot.setImageDrawable(dot_src_normal);
                            }
                        }

                        mHandler.sendEmptyMessageDelayed(MESSAGE, DELAYED);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mHandler.removeMessages(MESSAGE);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });

        LayoutParams lp = (LayoutParams) mDots.getLayoutParams();
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = dotsBottomMargin;
        mDots.setLayoutParams(lp);
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (banners.size() == 0) {
                return;
            }
            current++;
            mRecyclerView.smoothScrollToPosition(current);
            mHandler.sendEmptyMessageDelayed(MESSAGE, DELAYED);
        }
    };

    /**
     * 设置多个数据
     *
     * @param banners
     * @param type    1：详情页banner 2：签到页banner
     */
    public void setBanners(List<AD> banners, int type) {
        if (banners == null || banners.isEmpty()) {
            return;
        }
        this.type = type;
        mHandler.removeMessages(MESSAGE);
        this.banners = banners;
        mMultiAdapter = new MultiBannerAdapter();
        mRecyclerView.setAdapter(mMultiAdapter);
        initDots();
        current = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % banners.size();
        mRecyclerView.scrollToPosition(current);
        mHandler.sendEmptyMessageDelayed(MESSAGE, DELAYED);
    }

    /**
     * 设置单个数据
     *
     * @param banner
     * @param type   1：详情页banner 2：签到页banner
     */
    public void setBanner(AD banner, int type) {
        if (banner == null) {
            return;
        }
        this.type = type;
        this.banner = banner;
        mSingleAdapter = new SingleBannerAdapter();
        mRecyclerView.setAdapter(mSingleAdapter);
    }

    public void notifyDataSetChanged() {
        if (mMultiAdapter != null) {
            mHandler.removeMessages(MESSAGE);
            mMultiAdapter.notifyDataSetChanged();
            initDots();
            current = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % banners.size();
            mRecyclerView.scrollToPosition(current);
            mHandler.sendEmptyMessageDelayed(MESSAGE, DELAYED);
        }
    }

    private void initDots() {
        mDots.removeAllViews();
        for (int i = 0; i < banners.size(); i++) {
            ImageView dot = new ImageView(getContext());
            mDots.addView(dot);
            LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) dot.getLayoutParams();
            llp.width = llp.height = dotSize;
            if (i == 0) {
                dot.setImageDrawable(dot_src_select);
            } else {
                dot.setImageDrawable(dot_src_normal);
                llp.leftMargin = marginBetweenDots;
            }
            dot.setLayoutParams(llp);
        }
    }

    private class MultiBannerAdapter extends RecyclerView.Adapter<BannerViewHolder> {

        @Override
        public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView itemView = new ImageView(getContext());
            itemView.setScaleType(ImageView.ScaleType.FIT_XY);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new BannerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BannerViewHolder holder, int position) {
            final int p = position % banners.size();
            AD banner = banners.get(p);
            ImageView itemView = (ImageView) holder.itemView;
            GlideUtil.load(getContext(), banner.image, 0, itemView);
            createOnClick(itemView, banner);
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    private class SingleBannerAdapter extends RecyclerView.Adapter<BannerViewHolder> {

        @Override
        public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView itemView = new ImageView(getContext());
            itemView.setScaleType(ImageView.ScaleType.FIT_XY);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return new BannerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BannerViewHolder holder, int position) {
            ImageView itemView = (ImageView) holder.itemView;
            GlideUtil.load(getContext(), banner.image, 0, itemView);
            createOnClick(itemView, banner);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    private class BannerViewHolder extends RecyclerView.ViewHolder {

        BannerViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MESSAGE);
    }

    private void createOnClick(View view, final AD ad) {
//        if (type == 1) {
//            MobclickAgent.onEvent(getContext(), Constant.XQ_7);
//        } else if (type == 2) {
//            MobclickAgent.onEvent(getContext(), Constant.QD);
//        }
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (ad.type == 1) {
                    if (ad.readflag == 1) {
                        Work work = new Work();
                        work.wid = ad.wid;
                        work.lastChapterId = ad.cid;
                        work.toReadType = 2;
                        intent.setClass(getContext(), ReadActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("work", work);
                        CollBookBean mCollBook  = new CollBookBean();
                        mCollBook.setTitle(work.title);
                        mCollBook.set_id(work.wid+"");
                        intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
                    } else {
                        intent.setClass(getContext(), WorkDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("wid", ad.wid);
                        intent.putExtra("recid", ad.recId);
                    }
                } else if (ad.type == 2) {
                    intent.setClass(getContext(), WerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("index", ad.index);
                    intent.putExtra("path", ad.path);
                    intent.putExtra("pagefresh", ad.pagefresh);
                    intent.putExtra("share", ad.share);
                    intent.putExtra("shareUrl", ad.shareUrl);
                    intent.putExtra("shareType", ad.shareType);
                    intent.putExtra("sharefresh", ad.sharefresh);
                    intent.putExtra("shareTitle", ad.shareTitle);
                    intent.putExtra("shareDesc", ad.shareDesc);
                    intent.putExtra("shareImg", ad.shareImg);
                } else if (ad.type == 3) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(ad.url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                getContext().startActivity(intent);
            }
        });
    }
}
