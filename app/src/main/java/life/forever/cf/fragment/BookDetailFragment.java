package life.forever.cf.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.AD;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.Person;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.activtiy.CatalogActivity;
import life.forever.cf.activtiy.FansListActivity;
import life.forever.cf.activtiy.PublishWorkCommentActivity;
import life.forever.cf.activtiy.WorkCommentListActivity;
import life.forever.cf.adapter.CommentItemCreator;
import life.forever.cf.adapter.WorkInfoFansAdapter;
import life.forever.cf.adapter.WorkInfoRecommendAdapter;
import life.forever.cf.linstener.OnClickRecyclerViewListener;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.AutoRollBanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BookDetailFragment extends BaseFragment {


    @BindView(R.id.ll_label)
    LinearLayout ll_label;
    @BindView(R.id.description)
    TextView mDescription;
    @BindView(R.id.descriptionFlex)
    ImageView mDescriptionFlex;

    @BindView(R.id.newChapterName)
    TextView mNewChapterName;
    @BindView(R.id.newChapterDate)
    TextView mNewChapterDate;

    @BindView(R.id.tv_abstract_more)
    TextView tv_abstract_more;

    @BindView(R.id.fansLayout)
    View mFansLayout;
    @BindView(R.id.fansList)
    RecyclerView mFansList;
    @BindView(R.id.commentCount)
    TextView mCommentCount;
    @BindView(R.id.hasComment)
    View mHasComment;
    @BindView(R.id.commentGroup)
    LinearLayout mCommentGroup;
    @BindView(R.id.noComment)
    View mNoComment;


    @BindView(R.id.bannerLayout)
    View mBannerLayout;
    @BindView(R.id.banner)
    AutoRollBanner mBanner;
    @BindView(R.id.sortRecommendLayout)
    View mSortRecommendLayout;
    @BindView(R.id.sortRecyclerView)
    RecyclerView mSortRecyclerView;
    @BindView(R.id.otherRecommendLayout)
    View mOtherRecommendLayout;
    @BindView(R.id.otherRecyclerView)
    RecyclerView mOtherRecyclerView;

    private boolean isFlex;
    private final List<Work> otherWorks = new ArrayList<>();
    private final List<Work> sortWorks = new ArrayList<>();
    private final List<AD> ads = new ArrayList<>();
    private WorkInfoRecommendAdapter sortAdapter;
    private WorkInfoRecommendAdapter otherAdapter;

    private int commentCount;
    private final List<Comment> comments = new ArrayList<>();
    private CommentItemCreator commentItemCreator;

    private final List<Person> fans = new ArrayList<>();
    private WorkInfoFansAdapter fansAdapter;

    private int wid;
    private Work work;

    public static BookDetailFragment get(int wid,Work work) {
        BookDetailFragment instance = new BookDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("wid", wid);
        bundle.putParcelable("work", work);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    protected void bindView() {
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_book_detail, mContentLayout, TRUE);
        ButterKnife.bind(this, root);
//        EventBus.getDefault().register(this);
        mTitleBar.setVisibility(View.GONE);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    protected void fetchData() {
        Bundle bundle = getArguments();

        work = bundle.getParcelable("work");
        wid = bundle.getInt("wid");
        mDescription.setText(work.description);
        mNewChapterName.setText(work.latestChapter.title);
        mNewChapterDate.setText(work.isfinish == ONE ? getString(R.string.completed) : formatUpdateTime(work.latestChapter.updatetime));
        getComment();
        getRecommend();

        mSortRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, FALSE));
        mOtherRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, FALSE));
        mFansList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, FALSE));
        mFansList.addOnItemTouchListener(new OnClickRecyclerViewListener(mFansList, new OnClickRecyclerViewListener.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FansListActivity.class);
                intent.putExtra("wid", wid);
                startActivity(intent);
            }
        }));
        sortAdapter = new WorkInfoRecommendAdapter(context, sortWorks, ZERO);
        mSortRecyclerView.setAdapter(sortAdapter);
        otherAdapter = new WorkInfoRecommendAdapter(context, otherWorks, ONE);
        mOtherRecyclerView.setAdapter(otherAdapter);
        commentItemCreator = new CommentItemCreator(getActivity(), mCommentGroup);
        fansAdapter = new WorkInfoFansAdapter(context, fans);
        mFansList.setAdapter(fansAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }




    @OnClick(R.id.writeComment)
    void onWriteCommentClick() {
//        DeepLinkUtil.addPermanent(getActivity(),"event_details_write_comment","简介","点击写评论","","","","","","");
        Intent intent = new Intent(context, PublishWorkCommentActivity.class);
        intent.putExtra("wid", work.wid);
        startActivity(intent);
    }

    @OnClick(R.id.newChapter)
    void onNewChapterClick() {
        DeepLinkUtil.addPermanent(getActivity(),"event_details_catalog","简介","详情页目录","","","","","","");
        Intent intent = new Intent(context, CatalogActivity.class);
        intent.putExtra("work", work);
        startActivity(intent);
    }

    @OnClick({R.id.description, R.id.descriptionFlex})
    void onDescriptionClick() {
        if (isFlex) {
            isFlex = FALSE;
            mDescription.setMaxLines(FOUR);
            mDescriptionFlex.setImageResource(R.drawable.book_abstract);
            tv_abstract_more.setVisibility(View.VISIBLE);
        } else {
            isFlex = TRUE;
            mDescription.setMaxLines(Integer.MAX_VALUE);
            mDescriptionFlex.setImageResource(R.drawable.book_abstract);
            tv_abstract_more.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.moreComment)
    void onMoreCommentClick() {

//        DeepLinkUtil.addPermanent(getActivity(),"event_details_readall_comment","简介","点击查看全部评论","","","","","","");

        Intent intent = new Intent(context, WorkCommentListActivity.class);
        intent.putExtra("wid", wid);
        startActivity(intent);
    }
    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);

    }
    /**
     * 评论
     */
    private void getComment() {
        NetRequest.workCommentList(wid, ONE, ONE,new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        comments.clear();
                        commentCount = JSONUtil.getInt(result, "count");
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            Comment comment = BeanParser.getComment(JSONUtil.getJSONObject(lists, i));
                            comments.add(comment);
                        }
                        if (comments.size() > ZERO) {
                            mCommentCount.setText(String.format(Locale.getDefault(), "（%d）",  commentCount));
                            updateCommentLayout();
                            mHasComment.setVisibility(View.VISIBLE);
                            mNoComment.setVisibility(View.GONE);
                        } else {
                            mHasComment.setVisibility(View.GONE);
                            mNoComment.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mHasComment.setVisibility(View.GONE);
                        mNoComment.setVisibility(View.VISIBLE);
                    }
                } else {
                    mHasComment.setVisibility(View.GONE);
                    mNoComment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String error) {
                mHasComment.setVisibility(View.GONE);
                mNoComment.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 推荐位数据
     */
    private void getRecommend() {
        NetRequest.workInfoRecommend(wid, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        JSONObject good_rec = JSONUtil.getJSONObject(result, "good_rec");
                        JSONObject good_rec_info = JSONUtil.getJSONObject(good_rec, "rec_info");
                        JSONArray good_list = JSONUtil.getJSONArray(good_rec, "rec_list");
                        for (int i = ZERO; good_list != null && i < good_list.length(); i++) {
                            Work work = BeanParser.getWork(JSONUtil.getJSONObject(good_list, i));
                            work.recId = JSONUtil.getInt(good_rec_info, "rec_id");
                            otherWorks.add(work);
                        }
                        if (otherWorks.size() > ZERO) {
                            otherAdapter.notifyDataSetChanged();
                            mOtherRecommendLayout.setVisibility(View.VISIBLE);
                        } else {
                            mOtherRecommendLayout.setVisibility(View.GONE);
                        }
                        JSONObject sort_rec = JSONUtil.getJSONObject(result, "sort_rec");
                        JSONObject sort_rec_info = JSONUtil.getJSONObject(sort_rec, "rec_info");
                        JSONArray sort_list = JSONUtil.getJSONArray(sort_rec, "rec_list");
                        for (int i = ZERO; sort_list != null && i < sort_list.length(); i++) {
                            Work work = BeanParser.getWork(JSONUtil.getJSONObject(sort_list, i));
                            work.recId = JSONUtil.getInt(sort_rec_info, "rec_id");
                            sortWorks.add(work);
                        }
                        if (sortWorks.size() > ZERO) {
                            sortAdapter.notifyDataSetChanged();
                            mSortRecommendLayout.setVisibility(View.VISIBLE);
                        } else {
                            mSortRecommendLayout.setVisibility(View.GONE);
                        }
                        JSONObject ad_rec = JSONUtil.getJSONObject(result, "ad_rec");
                        JSONObject ad_rec_info = JSONUtil.getJSONObject(ad_rec, "rec_info");
                        JSONArray ad_list = JSONUtil.getJSONArray(ad_rec, "rec_list");
                        for (int i = ZERO; ad_list != null && i < ad_list.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(ad_list, i);
                            AD ad = new AD();
                            ad.recId = JSONUtil.getInt(ad_rec_info, "rec_id");
                            ad.during = JSONUtil.getInt(ad_rec_info, "length");
                            ad.image = JSONUtil.getString(child, "h_url");
                            ad.type = JSONUtil.getInt(child, "advertise_type");

                            JSONObject advertise = JSONUtil.getJSONObject(child, "advertise_data");
                            if (ad.type == ONE) {
                                ad.wid = JSONUtil.getInt(advertise, "wid");
                                ad.readflag = JSONUtil.getInt(advertise, "readflag");
                                ad.cid = JSONUtil.getInt(advertise, "cid");
                            } else if (ad.type == TWO) {
                                ad.index = JSONUtil.getString(advertise, "ht");
                                ad.path = JSONUtil.getString(advertise, "path");
                                ad.pagefresh = JSONUtil.getInt(advertise, "ps") == ZERO;
                                ad.share = JSONUtil.getInt(advertise, "is") == ONE;
                                ad.shareUrl = JSONUtil.getString(advertise, "su");
                                ad.shareType = JSONUtil.getInt(advertise, "st");
                                ad.sharefresh = JSONUtil.getInt(advertise, "ifreash") == ZERO;
                                ad.shareTitle = JSONUtil.getString(advertise, "title");
                                ad.shareDesc = JSONUtil.getString(advertise, "desc");
                                ad.shareImg = JSONUtil.getString(advertise, "image");
                            } else if (ad.type == THREE) {
                                ad.url = JSONUtil.getString(advertise, "url");
                            }
                            ads.add(ad);
                        }
                        if (ads.size() == ONE) {
                            mBanner.setBanner(ads.get(ZERO), ONE);
                            mBannerLayout.setVisibility(View.VISIBLE);
                        } else if (ads.size() > ONE) {
                            mBanner.setBanners(ads, ONE);
                            mBannerLayout.setVisibility(View.VISIBLE);
                        } else {
                            mBannerLayout.setVisibility(View.GONE);
                        }
                    } else {
                        mBannerLayout.setVisibility(View.GONE);
                        mSortRecommendLayout.setVisibility(View.GONE);
                        mOtherRecommendLayout.setVisibility(View.GONE);
                    }
                } else {
                    mBannerLayout.setVisibility(View.GONE);
                    mSortRecommendLayout.setVisibility(View.GONE);
                    mOtherRecommendLayout.setVisibility(View.GONE);
                }
                mLoadingLayout.setVisibility(View.GONE);
                mContentLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
                mBannerLayout.setVisibility(View.GONE);
                mSortRecommendLayout.setVisibility(View.GONE);
                mOtherRecommendLayout.setVisibility(View.GONE);
            }
        });
    }
    /**
     * 更新评论的显示
     */
    private void updateCommentLayout() {
        if (getActivity() == null ||
                getActivity().isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getActivity().isDestroyed())) {
            return;
        }
        mCommentGroup.removeAllViews();
        for (int i = ZERO; i < Math.min(THREE, comments.size()); i++) {
            mCommentGroup.addView(commentItemCreator.createView(comments.get(i)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_SHELF_CHANGE) {
            boolean exist = ShelfUtil.exist(work.wid);
//            mDoCollect.setEnabled(!exist);
            mTitleBar.setRightImageViewTwoIsOnClick(!exist);
//            mCollect.setEnabled(!exist);
//            mCollect.setText(exist ? getString(R.string.added) : getString(R.string.add_bookcase));
            if (exist){
                mTitleBar.setRightImageResourceTwo(R.drawable.added_book_rack);
            }else{
                mTitleBar.setRightImageResourceTwo(R.drawable.add_book_rack);
            }
            return;
        }
        if (message.what == BUS_WORK_COMMENT_ADD_SUCCESS
                || message.what == BUS_SEND_CHAPTER_COMMENT_SUCCESS) {
            Comment comment = (Comment) message.obj;
            if (comment.wid == wid) {
                comments.add(ZERO, comment);
                commentCount++;
                mCommentCount.setText(String.format(Locale.getDefault(), "（%d）",  commentCount));
                updateCommentLayout();
                mHasComment.setVisibility(View.VISIBLE);
                mNoComment.setVisibility(View.GONE);
//                if (mScore.getProgress() == ZERO) {
//                    mScore.setProgress(comment.score);
//                }
            }
            return;
        }
        if (message.what == BUS_COMMENT_ADD_LIKE) {
            Comment temp = (Comment) message.obj;
            for (int i = ZERO; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                if (comment.equals(temp)) {
                    comment.isLike = temp.isLike;
                    comment.likeCount = temp.likeCount;
                    updateCommentLayout();
                    break;
                }
            }
            return;
        }
        if (message.what == BUS_COMMENT_ADD_REPLY) {
            Comment temp = (Comment) message.obj;
            for (int i = ZERO; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                if (comment.equals(temp)) {
                    comment.replyCount = temp.replyCount;
                    updateCommentLayout();
                    break;
                }
            }
            return;
        }
        if (message.what == BUS_COMMENT_DELETE) {
            Comment temp = (Comment) message.obj;
            for (int i = ZERO; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                if (comment.equals(temp)) {
                    comments.remove(i);
                    commentCount--;
                    mCommentCount.setText(String.format(Locale.getDefault(), "（%d）",  commentCount));
                    updateCommentLayout();
                    if (comments.size() > ZERO) {
                        mHasComment.setVisibility(View.VISIBLE);
                        mNoComment.setVisibility(View.GONE);
                    } else {
                        mHasComment.setVisibility(View.GONE);
                        mNoComment.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
            return;
        }
        if (message.what == BUS_REWARD_SUCCESS) {
            if ((int) message.obj == wid) {
                getComment();
            }
        }
    }

    /**
     * 连载作品最新更新时间格式化
     *
     * @param seconds
     * @return
     */
    public String formatUpdateTime(int seconds) {
        int temp = ComYou.currentTimeSeconds() - seconds;
        if (temp < SIXTY) {
            return getString(R.string.just);
        }
        if ((temp = temp / SIXTY) < SIXTY) { // 不足1小时
            return temp + getString(R.string.minutes_ago);
        }
        if ((temp = temp / SIXTY) < TWENTY_FOUR) { // 不足1天
            return temp + getString(R.string.hours_before);
        }
        return getString(R.string.in_the_serial);
    }

}
