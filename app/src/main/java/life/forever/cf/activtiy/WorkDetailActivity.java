package life.forever.cf.activtiy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.CustomLinearLayoutManager;
import life.forever.cf.entry.TagBean;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.BookUpdateTimeInfoBean;
import life.forever.cf.entry.ChapterItemBean;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.entry.BookCatalogResult;
import life.forever.cf.entry.BookChapterPackage;
import life.forever.cf.entry.BookModifyInfoPackage;
import life.forever.cf.entry.BookModifyInfoResult;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.adapter.CommentItemCreator;
import life.forever.cf.adapter.WorkInfoRecommendAdapter;
import life.forever.cf.adapter.DiscoverMoreActivity;
import life.forever.cf.sql.DBUtils;
import life.forever.cf.internet.ReaderRemoteRepository;
import life.forever.cf.sql.NiceRatingBar;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.adapter.person.personcenter.ReportActivity;
import life.forever.cf.publics.fresh.android.able.OnScrollListener;
import life.forever.cf.publics.fresh.android.view.ObservableScrollView;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.textviewfold.ExpandableTextView;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ScreenUtil;
import life.forever.cf.publics.weight.poputil.LoadingAlertDialog;
import life.forever.cf.publics.weight.poputil.SharePopup;
import life.forever.cf.adapter.NewReadCatalogAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.DisplayUtil;


public class WorkDetailActivity extends ReaderFullScreenBaseActivity {

    protected CompositeDisposable mDisposable;


    private final int page = 20;
    protected Context context;
    public FirebaseAnalytics mFirebaseAnalytics;


    @BindView(R.id.rl)
    RelativeLayout rl;
    @BindView(R.id.book_detail_discount)
    TextView book_detail_discount;
    @BindView(R.id.book_detail_author)
    TextView book_detail_author;
    @BindView(R.id.ll_start_read)
    LinearLayout ll_start_read;

    @BindView(R.id.rl_tibar)
    RelativeLayout rl_tibar;
    @BindView(R.id.book_title)
    TextView book_title;
    @BindView(R.id.iv_back)
    ImageView iv_back;


    @BindView(R.id.iv_share)
    ImageView iv_share;
    @BindView(R.id.btn_state)
    Button btn_state;

    @BindView(R.id.book_detail_follow)
    TextView book_detail_follow;
    @BindView(R.id.book_detail_read)
    TextView book_detail_read;
    @BindView(R.id.book_detail_reward)
    TextView book_detail_reward;
    @BindView(R.id.scrollView)
    ObservableScrollView mScrollView;
    @BindView(R.id.cover)
    ImageView mCover;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.essay_logo)
    ImageView essay_logo;

    @BindView(R.id.descriptions)
    ExpandableTextView descriptions;

    @BindView(R.id.opreview_ratingbar)
    NiceRatingBar opreview_ratingbar;
    @BindView(R.id.clickCount)
    TextView mClickCount;
    @BindView(R.id.do_bookcase)
    ImageView do_bookcase;
    @BindView(R.id.startRead)
    TextView mStartRead;
//    @BindView(R.id.viewPager)
//    ViewPager mViewPager;


    @BindView(R.id.ll_nei_content)
    LinearLayout ll_nei_content;

    @BindView(R.id.ll_type)
    LinearLayout ll_type;

    @BindView(R.id.ll_description_wai)
    LinearLayout ll_description_wai;
    @BindView(R.id.tv_description_wai)
    TextView tv_description_wai;
    @BindView(R.id.v_description_wai)
    View v_description_wai;

//    @BindView(R.id.ll_chapters_wai)
//    LinearLayout ll_chapters_wai;
//    @BindView(R.id.tv_chapters_wai)
//    TextView tv_chapters_wai;
//    @BindView(R.id.v_chapters_wai)
//    View v_chapters_wai;

    @BindView(R.id.ll_comment_wai)
    LinearLayout ll_comment_wai;
    @BindView(R.id.tv_comment_wai)
    TextView tv_comment_wai;
    @BindView(R.id.v_comment_wai)
    View v_comment_wai;

    @BindView(R.id.ll_description)
    LinearLayout ll_description;
    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.v_description)
    View v_description;

//    @BindView(R.id.ll_chapters)
//    LinearLayout ll_chapters;
//    @BindView(R.id.tv_chapters)
//    TextView tv_chapters;
//    @BindView(R.id.v_chapters)
//    View v_chapters;

    @BindView(R.id.ll_comment)
    LinearLayout ll_comment;
    @BindView(R.id.tv_comment)
    TextView tv_comment;
    @BindView(R.id.v_comment)
    View v_comment;

    @BindView(R.id.ll_description_content)
    LinearLayout ll_description_content;

    @BindView(R.id.ll_comment_content)
    LinearLayout ll_comment_content;

    @BindView(R.id.ll_label)
    LinearLayout ll_label;
    @BindView(R.id.tagfl)
    TagFlowLayout tagfl;


    /**
     * 评论布局
     */
    @BindView(R.id.ll_comments)
    LinearLayout ll_comments;
    @BindView(R.id.ll_comment_group)
    LinearLayout ll_comment_group;


    @BindView(R.id.noComment)
    LinearLayout noComment;
    @BindView(R.id.write_comment)
    ImageView write_comment;
    @BindView(R.id.ll_chapters)
    LinearLayout ll_chapters;

    @BindView(R.id.chapter_num)
    TextView chapter_num;
    @BindView(R.id.book_state_chapter)
    TextView book_state_chapter;

    @BindView(R.id.recomment_cover)
    ImageView recomment_cover;
    @BindView(R.id.book_name)
    TextView book_name;
    @BindView(R.id.book_other)
    TextView book_other;
    @BindView(R.id.book_info)
    TextView book_info;
    @BindView(R.id.read_num)
    TextView read_num;
    @BindView(R.id.book_author)
    TextView book_author;
    @BindView(R.id.tv_name_recommend)
    TextView tv_name_recommend;

    /**
     * 推荐作者书籍
     */
    @BindView(R.id.rcv_content)
    RecyclerView rcv_content;
    @BindView(R.id.ll_reconmend_list)
    LinearLayout ll_reconmend_list;
    @BindView(R.id.rl_upload)
    RelativeLayout rl_upload;
    @BindView(R.id.more_recommend)
    LinearLayout more_recommend;


    /**
     * 推荐布局
     */
    @BindView(R.id.ll_white_comment)
    LinearLayout ll_white_comment;
    @BindView(R.id.ll_white_comment_in)
    LinearLayout ll_white_comment_in;

    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.tv_commit)
    TextView tv_commit;
    @BindView(R.id.opreview_ratingbar_comment)
    NiceRatingBar opreview_ratingbar_comment;
    @BindView(R.id.edit_comment)
    EditText edit_comment;

    @BindView(R.id.ll_all)
    LinearLayout ll_all;
    @BindView(R.id.ll_catalog)
    LinearLayout ll_catalog;
    @BindView(R.id.chpater_counts)
    TextView chpater_counts;
    @BindView(R.id.chpater_status)
    TextView chpater_status;
    @BindView(R.id.chpater_order)
    ImageView chpater_order;
    @BindView(R.id.chpater_recyclerView)//章节目录
    RecyclerView chpater_recyclerView;
    //举报按钮
    @BindView(R.id.iv_report)
    ImageView mImgReport;

    private int wid;
    private int recid;
    private Work work;
    private int mAlpha = 0;
    /**
     * 推荐作者书id
     */
    private int mWorkid;
    private int topHeight;
    private SharePopup sharePopup;

    /**
     * 是否在阅读记录里面  addExist是否加入书架
     */
    boolean exist;
    /**
     * 推荐
     */
    private final List<Work> sortWorks = new ArrayList<>();
    private final List<Work> otherWorks = new ArrayList<>();
    private WorkInfoRecommendAdapter sortAdapter;
    private int rec_id;

    /**
     * 目录
     */
    private final List<ChapterItemBean> catalogs = new ArrayList<>();
    private NewReadCatalogAdapter catalogAdapter;
    private CustomLinearLayoutManager mCustomLinearLayoutManager;

    /**
     * 评论
     */
    private int commentCount;
    private final List<Comment> comments = new ArrayList<>();
    private CommentItemCreator commentItemCreator;
    private Random random;
    private AlertDialog loadingDialog;

//    @SuppressLint("MissingPermission")
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_work_detail);
//        ButterKnife.bind(this);
//
//
//    }


    @Override
    protected int getContentId() {
        return R.layout.activity_work_detail;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        context = this;
        mScrollView.setOnScrollListener(onScrollListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ScreenUtil.transparentStatusBar(this);
            ScreenUtil.setStatusBarDark(this, true);
            rl_tibar.setFitsSystemWindows(Constant.TRUE);
        }
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initializeView();
        initializeData();
    }

    protected void initializeView() {
        setContentView(R.layout.activity_work_detail);
        ButterKnife.bind(this);

        mScrollView.setOnScrollListener(onScrollListener);
        topHeight = DisplayUtil.dp2px(WorkDetailActivity.this, Constant.FOUR_HUNDRED_THREE);
        ll_start_read.setBackgroundResource(R.color.color_FFFFFF);
        setTextViewWatcher();

    }


    public void initializeData() {
        EventBus.getDefault().register(this);
        wid = getIntent().getIntExtra("wid", Constant.ZERO);

        // TODO: 2021/10/11  1.8.1测试书籍
////        wid = 80;
//        wid = 81;
//        wid = 9;
//        wid = 10;
//        wid = 11;
//        wid = 12;
//        wid = 13;
//        wid = 14;
//        wid = 15;aaaaaaaa


        recid = getIntent().getIntExtra("recid", Constant.ZERO);
//        String cover = PlotRead.getConfig().getString(wid+"big","");
//        GlideUtil.loadDetail(context,"", cover,"", R.drawable.default_work_cover, mCover);
        exist = ShelfUtil.existRecord(wid);
//        cacheSQLiteHelper = CacheSQLiteHelper.get(WorkDetailActivity.this, wid);
//        cacheSQLiteHelper.deleteTable();

        mStartRead.setText(exist ? getString(R.string.aiye_read_continue) : getString(R.string.detail_start_read));
        do_bookcase.setImageResource(ShelfUtil.exist(wid) ? R.drawable.added_bookcase : R.drawable.add_bookcase);
        getWorkInfo();
        commentItemCreator = new CommentItemCreator(WorkDetailActivity.this, ll_comment_content);
        getComment();
        getRecommend();
        ll_start_read.setBackgroundResource(R.color.color_FFFFFF);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStartRead != null){
            mStartRead.setText(exist ? getString(R.string.aiye_read_continue) : getString(R.string.detail_start_read));
            do_bookcase.setImageResource( ShelfUtil.exist(wid) ? R.drawable.added_bookcase : R.drawable.add_bookcase);
        }
    }

    private void setTextViewWatcher() {
        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 9) {
                    //编辑框长度>0
                    tv_commit.setEnabled(true);
                    tv_commit.setTextColor(getResources().getColor(R.color.theme_color));
                } else {
                    //编辑框长度为0
                    tv_commit.setTextColor(getResources().getColor(R.color.color_999999));
                    tv_commit.setEnabled(false);
                }
            }
        });
    }


    /**
     * 获取作品信息
     */
    private void getWorkInfo() {
        NetRequest.workInfo(wid, recid, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (Constant.SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == Constant.ONE) {
                        JSONObject info = JSONUtil.getJSONObject(result, "info");
                        work = BeanParser.getWork(info);

                        JSONObject stat = JSONUtil.getJSONObject(result, "stat");
                        work.totalFans = JSONUtil.getInt(stat, "fans_total");
                        work.totalCollect = JSONUtil.getInt(stat, "collect_total");
                        work.award_total = JSONUtil.getInt(stat, "award_total");
                        work.totalShare = JSONUtil.getInt(stat, "share_total");
                        work.sortTitle = JSONUtil.getString(info, "sort");
                        work.sort_id = JSONUtil.getInt(info, "sort_id");
                        work.uv = JSONUtil.getInt(stat, "uv");
                        work.pv = JSONUtil.getInt(stat, "pv");

                        JSONArray tag = JSONUtil.getJSONArray(info, "tag");
                        List<TagBean> mTagBeans = new ArrayList<>();
                        for (int i = Constant.ZERO; tag != null && i < tag.length(); i++) {
                            TagBean mTagbean = new TagBean();
                            mTagbean.id = JSONUtil.getString(JSONUtil.getJSONObject(tag, i), "id");
                            mTagbean.tag = JSONUtil.getString(JSONUtil.getJSONObject(tag, i), "tag");
                            mTagBeans.add(mTagbean);
                        }
                        work.tag = mTagBeans;
                        JSONObject sell = JSONUtil.getJSONObject(result, "sell");
                        work.isDiscount = JSONUtil.getInt(sell, "is_discount") == Constant.ONE;
                        work.discount = JSONUtil.getInt(sell, "discount");
                        work.discount_start_time = JSONUtil.getInt(sell, "discount_start");
                        work.discount_end_time = JSONUtil.getInt(sell, "discount_end");
                        work.isMonth = JSONUtil.getInt(sell, "is_month") == Constant.ONE;
                        work.month_start_time = JSONUtil.getInt(sell, "month_start");
                        work.month_end_time = JSONUtil.getInt(sell, "month_end");
                        work.solicit_logo  = JSONUtil.getString(result, "solicit_logo");
                        JSONObject latest = JSONUtil.getJSONObject(result, "latest");
                        work.latestChapter = BeanParser.getLatestChapter(latest);

                        fillView();
//                        fetchWorkCatalog(ZERO, -ONE);
                        getBookChapters();
                    } else {
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                        finish();
                    }
                } else {
                    NetRequest.error(WorkDetailActivity.this, serverNo);
                    finish();
                }
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }


    protected void reload() {
        getWorkInfo();
    }


    private void fillView() {
        GlideUtil.load(context, work.cover,R.drawable.default_work_cover,mCover);
//        GlideUtil.loadDetail(context, "", work.cover, work.cover, R.drawable.default_work_cover, mCover);
        //征文logo
        if (TextUtils.isEmpty(work.solicit_logo )){
            essay_logo.setVisibility(View.GONE);
        }else{
            essay_logo.setVisibility(View.VISIBLE);
            GlideUtil.load(context, work.solicit_logo , 0, essay_logo);
        }

        book_detail_discount.setText(work.title);
        book_title.setText(work.title);
        chapter_num.setText(getString(R.string.read_recommend_detail_chapter)+" "+ work.totalChapter +" "+ getString(R.string.chapters));
        if (work.score > 0) {
            opreview_ratingbar.setRating(((float) work.score) / 2);
        } else {
            opreview_ratingbar.setRating(work.score);
        }
        btn_state.setText(work.isfinish == Constant.ZERO ? context.getString(R.string.author_ongoing) : context.getString(R.string.author_greatly));
        book_state_chapter.setText(work.isfinish == Constant.ZERO ? context.getString(R.string.author_ongoing):context.getString(R.string.author_greatly));
        mClickCount.setText(work.score + ".0");
        book_detail_author.setText(work.author);
        description.setText(work.description);


        int viewWidth = getWindowManager().getDefaultDisplay().getWidth() - DisplayUtil.dp2px(this, 20f);
        descriptions.initWidth(viewWidth);
        descriptions.setMaxLines(4);
        descriptions.setHasAnimation(true);
        descriptions.setCloseInNewLine(true);
        descriptions.setOpenSuffixColor(getResources().getColor(R.color.theme_color));
        if (!TextUtils.isEmpty(work.description)){
            descriptions.setOriginalText(work.description);
        }

        book_detail_follow.setText(ComYou.formatNum(work.totalCollect));
        book_detail_read.setText(ComYou.formatNum(work.pv));
        book_detail_reward.setText(ComYou.formatNum(work.award_total));
        initMarksView();


    }

    private void initMarksView() {
        if (work.tag == null || work.tag.size() == 0) {
            ll_label.setVisibility(View.GONE);
            return;
        }
        ll_label.setVisibility(View.VISIBLE);
        List<String> datas = new ArrayList<>();
        datas.add(work.sortTitle);
        for (int i = 0; i < work.tag.size(); i++) {
            datas.add(work.tag.get(i).tag);
        }

        TagAdapter<String> adapter = new TagAdapter<String>(datas) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                View view = LayoutInflater.from(WorkDetailActivity.this).inflate(R.layout.tag_item, parent, false);
                TextView tv_tag = view.findViewById(R.id.tv_tag);
                TextView tv_name = view.findViewById(R.id.tv_name);
                ImageView ivHot = view.findViewById(R.id.iv_hot);
                if (position == 0) {
                    tv_tag.setVisibility(View.VISIBLE);
                    ivHot.setVisibility(View.VISIBLE);
                    tv_name.setText(work.sortTitle);
                } else {
                    tv_tag.setVisibility(View.GONE);
                    ivHot.setVisibility(View.GONE);
                    tv_name.setText(work.tag.get(position - 1).tag);
                }

                return view;
            }
        };
        tagfl.setAdapter(adapter);
        tagfl.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
//                LogUtil.e("点击了tag:"+position);
                if (position == 0) {
                    Intent intent = new Intent(context, LibraryActivity.class);
                    intent.putExtra("tagname",work.sortTitle);
                    intent.putExtra("tagtype",work.sort_id+"");
                    intent.putExtra("tagid","");
                    startActivity(intent);
                } else {
                    work.tag.get(position - 1);
                    Intent intent = new Intent(context, LibraryActivity.class);
                    intent.putExtra("tagname",work.tag.get(position - 1).tag);
                    intent.putExtra("tagtype","");
                    intent.putExtra("tagid",work.tag.get(position - 1).id);
                    startActivity(intent);
                }
                return false;
            }
        });
        tagfl.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
            }
        });
        adapter.setSelectedList(0);//默认选中第一个

    }


    private final OnScrollListener onScrollListener = new OnScrollListener() {

        @Override
        public void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy) {

            if (y >= topHeight) {
                //重点 通过距离变化隐藏内外固定栏实现
                ll_type.setVisibility(View.VISIBLE);
                ll_nei_content.setVisibility(View.INVISIBLE);
                scrollView.setNestedScrollingEnabled(true);
            } else {
                ll_type.setVisibility(View.GONE);
                ll_nei_content.setVisibility(View.VISIBLE);
                scrollView.setNestedScrollingEnabled(false);
            }

            /**  ScrollView 滚动动态改变标题栏 */
            // 滑动的最小距离（自行定义，you happy jiu ok）
            int minHeight = 50;
            // 滑动的最大距离（自行定义，you happy jiu ok）
            int maxHeight = rl_tibar.getHeight() - DisplayUtil.dp2px(WorkDetailActivity.this, minHeight);

            // 滑动距离小于定义得最小距离
            if (scrollView.getScrollY() <= minHeight) {
                mAlpha = 0;
            }
            // 滑动距离大于定义得最大距离
            else if (scrollView.getScrollY() > maxHeight) {
                mAlpha = 255;
            }
            // 滑动距离处于最小和最大距离之间
            else {
                // （滑动距离 - 开始变化距离）：最大限制距离 = mAlpha ：255
                mAlpha = (scrollView.getScrollY() - minHeight) * 255 / (maxHeight - minHeight);
            }

            // 初始状态 标题栏/导航栏透明等
            if (mAlpha <= 0) {
                setViewBackgroundAlpha(rl_tibar, 0);
                book_title.setVisibility(View.GONE);
                rl_tibar.setBackgroundResource(R.drawable.black_00000000);
                iconColorFilter(Color.parseColor("#ffffff"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Window window = getWindow();
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            }
            //  终止状态：标题栏/导航栏 不在进行变化
            else if (mAlpha >= 255) {
                setViewBackgroundAlpha(rl_tibar, 255);
                book_title.setVisibility(View.VISIBLE);
                book_title.setTextColor(Color.argb(255, 0, 0, 0));
                rl_tibar.setBackgroundResource(R.color.colorWhite);
                iconColorFilter(Color.parseColor("#000000"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Window window = getWindow();
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

                }
            }
            // 变化中状态：标题栏/导航栏随ScrollView 的滑动而产生相应变化
            else {
                rl_tibar.setBackgroundResource(R.drawable.black_00000000);
                book_title.setVisibility(View.VISIBLE);
                book_title.setTextColor(Color.argb(255, 255 - mAlpha, 255 - mAlpha, 255 - mAlpha));
                iconColorFilter(Color.argb(255, 255 - mAlpha, 255 - mAlpha, 255 - mAlpha));
            }
        }
    };

    /**
     * 设置View的背景透明度
     *
     * @param view
     * @param alpha
     */
    public void setViewBackgroundAlpha(View view, int alpha) {
        if (view == null) return;

        Drawable drawable = view.getBackground();
        if (drawable != null) {
            drawable.setAlpha(alpha);
        }
    }

    /**
     * 标题栏/导航栏icon 颜色改变
     *
     * @param color
     */
    private void iconColorFilter(int color) {
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        iv_share.setColorFilter(colorFilter);
        iv_back.setColorFilter(colorFilter);
        mImgReport.setColorFilter(colorFilter);
    }


    private void setCount() {
        if (catalogs != null) {
            chpater_counts.setText(catalogs.size()+ " " + getString(R.string.chapters));
            chpater_status.setText(work.isfinish == Constant.ZERO ? context.getString(R.string.author_ongoing):context.getString(R.string.author_greatly));
        }
    }

    @OnClick({R.id.rl_tibar, R.id.iv_share, R.id.chpater_order, R.id.author_refresh, R.id.do_bookcase, R.id.more_recommend, R.id.opreview_ratingbar,
            R.id.iv_close,  R.id.ll_item, R.id.tv_commit, R.id.startRead, R.id.moreComment,R.id.write_comment,R.id.rl_close_chapter,
            R.id.ll_description,R.id.ll_comment,R.id.ll_chapters, R.id.ll_description_wai, R.id.ll_comment_wai, R.id.ll_all,R.id.ll_white_comment,
            R.id.iv_report})
    public void setOnClick(View id) {
        Intent intent = new Intent();
        switch (id.getId()) {
            case R.id.rl_tibar:
                onBackPressed();
                break;
            case R.id.iv_share:
                showSharePop();
                break;
            case R.id.ll_chapters:
                if (wid == 0 || catalogs == null || catalogs.size() == 0){
                    return;
                }
                onDirectoryUpClick(ll_all,ll_catalog);
                break;
            case R.id.ll_all:
            case R.id.rl_close_chapter:
                onDirectoryDownClick(ll_all,ll_catalog);
                break;

            case R.id.chpater_order:
                if (null != catalogAdapter && null != chpater_order) {

                    if (catalogAdapter.reverse) {
                        catalogAdapter.reverse = Constant.FALSE;
                        chpater_order.setImageResource(R.drawable.positive_sequence_up);

                    } else {
                        catalogAdapter.reverse = Constant.TRUE;
                        chpater_order.setImageResource(R.drawable.positive_sequence_down);
                    }
                    catalogAdapter.update(work.lastChapterOrder);
                    catalogAdapter.notifyDataSetChanged();

                    catalogAdapter.smoothMoveToPosition(chpater_recyclerView,catalogAdapter.getCurrentPosition());
                }
                break;

            case R.id.author_refresh:
                setAuthorData();
                break;

            case R.id.do_bookcase:
                if (ShelfUtil.exist(wid)) {
                    if (work == null ){
                        return;
                    }
                    work.deleteflag = Constant.ONE;
                    ShelfUtil.insert(WorkDetailActivity.this, work,false);
                    PlotRead.toast(PlotRead.SUCCESS, getString(R.string.cancel_bookshelf_added_successfully));
                    do_bookcase.setImageResource(R.drawable.add_bookcase);
                    DeepLinkUtil.addPermanent(WorkDetailActivity.this, "event_details_subshelf", "详情页", "点击移除书架", "", work.wid+"", "", "", "", "");
                }else{
                    if (work == null ){
                        return;
                    }
                    DeepLinkUtil.addPermanent(WorkDetailActivity.this, "event_details_addshelf", "详情页", "点击加入书架", "", work.wid+"", "", "", "", "");
                    updateRecord();
                    work.deleteflag = Constant.ZERO;
                    ShelfUtil.insert(WorkDetailActivity.this, work,false);
                    do_bookcase.setImageResource(R.drawable.added_bookcase);
                    PlotRead.toast(PlotRead.SUCCESS, getString(R.string.bookshelf_added_successfully));

                }
                break;
            case R.id.more_recommend:
                intent.setClass(this, DiscoverMoreActivity.class);
                intent.putExtra("rec_id", rec_id);
                startActivity(intent);
                break;
            case R.id.opreview_ratingbar:
            case R.id.write_comment:
                onDirectoryUpClick(ll_white_comment,ll_white_comment_in);
                break;
            case R.id.iv_close:
            case R.id.ll_white_comment:
                onDirectoryDownClick(ll_white_comment,ll_white_comment_in);
                break;
            case R.id.ll_item:
                DeepLinkUtil.addPermanent(context, "event_details_author", "详情页", "详情作者推荐", "", mWorkid+"", "", "", "", "");
                intent.setClass(this, WorkDetailActivity.class);
                intent.putExtra("wid", mWorkid);
                context.startActivity(intent);
                break;
            case R.id.tv_commit:
                if (!PlotRead.getAppUser().login()) {
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                int score = (int) (opreview_ratingbar.getRating() * 2);
                String trim = edit_comment.getText().toString().trim();
                loadingDialog = LoadingAlertDialog.show(context, getString(R.string.loading));

                if (score == Constant.ZERO) {
                    addComment(trim);
                } else {
                    addScoreComment(trim, score);
                }
                break;

            case R.id.moreComment:
                intent = new Intent(context, WorkCommentListActivity.class);
                intent.putExtra("wid", wid);
                startActivity(intent);
                break;


            case R.id.ll_description:
            case R.id.ll_description_wai:
                onTheColumnClick(1);
                break;
//            case R.id.ll_chapters:
//            case R.id.ll_chapters_wai:
//                onTheColumnClick(2);
//                break;
            case R.id.ll_comment:
            case R.id.ll_comment_wai:
                onTheColumnClick(3);
                break;
            case R.id.startRead:
                DeepLinkUtil.addPermanent(WorkDetailActivity.this, "event_details_read", "简介", "点击阅读", "", "", "", "", "", "");
                if(work == null || work.wid == 0){
                    return;
                }
                updateRecord();
                work.toReadType = 0;
                intent.setClass(this, ReadActivity.class);
                intent.putExtra("work", work);
                CollBookBean mCollBook  = new CollBookBean();
                mCollBook.setTitle(work.title);
                mCollBook.set_id(work.wid+"");
                intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
                startActivity(intent);

                break;
//            case R.id.loadFooter:
//                fetchWorkCatalog(catalogs.size(), page);
//                break;
            case R.id.iv_report:
                //举报页面
                intent.putExtra("type",2);
                intent.putExtra("wid",work.wid);
                intent.setClass(this, ReportActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * @param type 1-description 2-chapters 3-comment
     */
    private void onTheColumnClick(int type) {
        tv_description_wai.setTextColor(getResources().getColor(R.color.color_656667));
        v_description_wai.setVisibility(View.INVISIBLE);
        tv_comment_wai.setTextColor(getResources().getColor(R.color.color_656667));
        v_comment_wai.setVisibility(View.INVISIBLE);
        tv_description.setTextColor(getResources().getColor(R.color.color_656667));
        v_description.setVisibility(View.INVISIBLE);
        tv_comment.setTextColor(getResources().getColor(R.color.color_656667));
        v_comment.setVisibility(View.INVISIBLE);
        ll_description_content.setVisibility(View.GONE);
        ll_comment_content.setVisibility(View.GONE);

        switch (type) {
            case 1:
                tv_description.setTextColor(getResources().getColor(R.color.color_000001));
                v_description.setVisibility(View.VISIBLE);
                tv_description_wai.setTextColor(getResources().getColor(R.color.color_000001));
                v_description_wai.setVisibility(View.VISIBLE);
                ll_description_content.setVisibility(View.VISIBLE);
                break;

            case 3:
                tv_comment.setTextColor(getResources().getColor(R.color.color_000001));
                v_comment.setVisibility(View.VISIBLE);
                tv_comment_wai.setTextColor(getResources().getColor(R.color.color_000001));
                v_comment_wai.setVisibility(View.VISIBLE);
                ll_comment_content.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 弹出分享弹窗
     */
    private void showSharePop() {
        if (sharePopup == null) {
            sharePopup = new SharePopup(this);
        }
        sharePopup.show(rl);
    }

    /**
     * 查询作品的阅读记录
     */
    private void updateRecord() {
        if (null != work && ShelfUtil.existRecord(work.wid)) {
            Work record = ShelfUtil.queryRecord(this.work.wid);
            if (record != null) {
                work.lasttime = record.lasttime;
                work.lastChapterId = record.lastChapterId;
                work.lastChapterOrder = record.lastChapterOrder;
                work.lastChapterPosition = record.lastChapterPosition;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getBookModifyInfo()
    {
        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderBookModifyInfo(""+work.wid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookModifyInfoPackage modifyInfoPackage) -> {

                            if (modifyInfoPackage != null && modifyInfoPackage.getResult() != null) {

                                BookModifyInfoResult modifyInfoResult = modifyInfoPackage.getResult();

                                BookUpdateTimeInfoBean timeInfoBean = DBUtils.getInstance().getBookUpdateTimeBean(""+work.wid);

                                if(timeInfoBean != null)
                                {
                                    if(timeInfoBean.update_time < modifyInfoResult.update_time) {
                                        deleteBookChapterAndContentCache(work);
                                        fetchWorkCatalog(Constant.ZERO,-Constant.ONE);

                                        timeInfoBean.counts = modifyInfoResult.counts;
                                        timeInfoBean.update_time = modifyInfoResult.update_time;
                                    }


                                }else{
                                    work.totalChapter = modifyInfoResult.counts;
                                    timeInfoBean = new BookUpdateTimeInfoBean(""+work.wid,
                                            modifyInfoResult.counts,
                                            modifyInfoResult.update_time);
                                }
                                DBUtils.getInstance().saveBookUpdateTimeInfoBeanWithAsync(timeInfoBean);
                            }
                        },
                        (e) -> {
                            LogUtils.e("获取书籍 缓存更新  ====== " + e);
                        }
                );

        addDisposable(disposable);
    }

    private void deleteBookChapterAndContentCache(Work work){
        if(work != null)
        {
            BookManager.getInstance().deleteBookChapterAndContentCache(""+work.wid);
        }
    }


    private void getBookChapters(){
        if (catalogAdapter == null) {
            mCustomLinearLayoutManager = new CustomLinearLayoutManager(context);
            chpater_recyclerView.setLayoutManager(mCustomLinearLayoutManager);
            catalogAdapter = new NewReadCatalogAdapter(this,catalogs,new ArrayList<>());
            catalogAdapter.wid = ""+wid;
            chpater_recyclerView.setAdapter(catalogAdapter);
            catalogAdapter.setOnItemClickListener(catalogItemClick);
        }
        getLocalChapterCatalog();
    }


    private void getLocalChapterCatalog()
    {
        Disposable getLocalChaptersDispo = DBUtils.getInstance().
                getBookChaptersInRx(""+wid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        chapterItemBeans -> {
                            if (chapterItemBeans != null) {
                                catalogs.clear();
                                catalogs.addAll(chapterItemBeans);
                            }

                            if(catalogs.size() <=0)
                            {
                                fetchWorkCatalog(Constant.ZERO,-Constant.ONE);
                            }else{
                                if(catalogs.size() < work.totalChapter)
                                {
//                                    if(catalogs.get(0).sort > 1)
//                                    {
//                                        catalogs.clear();
//                                        fetchWorkCatalog(ZERO,-ONE);
//
//                                    }else{
//
//                                        fetchWorkCatalog(catalogs.size(),work.totalChapter - catalogs.size());
//                                    }
                                    fetchWorkCatalog(Constant.ZERO,-Constant.ONE);

                                }else{
                                    getBookModifyInfo();
                                    freashChapterAdapter();
                                }
                            }
                        },
                        (e) -> {
                            fetchWorkCatalog(Constant.ZERO,-Constant.ONE);
                        }
                );

        addDisposable(getLocalChaptersDispo);
    }

    private void freashChapterAdapter()
    {
        if (catalogAdapter.reverse){
            catalogAdapter.notifyDataSetChanged();
        }else{
            catalogAdapter.notifyItemRangeInserted(0,catalogAdapter.getItemCount());
        }
        setCount();

        ll_start_read.setBackgroundResource(R.color.color_FFFFFF);
    }

    /**
     * 目录
     *
     * @param start
     * @param count
     */
    private void fetchWorkCatalog(int start, int count) {

        boolean isAllOnline = false;
        if(count == -1)
        {
            isAllOnline = true;
        }

        final boolean lastAllOnline = isAllOnline;

        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderCatalog(""+wid,start,count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookChapterPackage chapterInfo) -> {
                            if (chapterInfo.getResult() != null) {
                                BookCatalogResult result = chapterInfo.getResult();

                                if(result.getCatalog() != null)
                                {
                                    BookUpdateTimeInfoBean tempTimeInfoBean = DBUtils.getInstance().getBookUpdateTimeBean(""+work.wid);

                                    if(tempTimeInfoBean != null && !lastAllOnline)
                                    {
                                        if(tempTimeInfoBean.update_time > result.update_time) {
                                            deleteBookChapterAndContentCache(work);
                                            catalogs.clear();
                                            freashChapterAdapter();
                                            fetchWorkCatalog(Constant.ZERO, -Constant.ONE);
                                            return;
                                        }
                                    }

                                    if(lastAllOnline && tempTimeInfoBean != null)
                                    {
                                        if(result.update_time < tempTimeInfoBean.update_time)
                                        {
                                            result.update_time = tempTimeInfoBean.update_time;
                                        }
                                    }


                                    BookUpdateTimeInfoBean timeInfoBean = new BookUpdateTimeInfoBean(""+wid,
                                            result.count,
                                            result.update_time);
                                    DBUtils.getInstance().saveBookUpdateTimeInfoBeanWithAsync(timeInfoBean);

                                    if(lastAllOnline)
                                    {
                                        catalogs.clear();
                                        catalogs.addAll(result.getCatalog());
                                    }
                                }

//                                freashChapterAdapter();

                                DBUtils.getInstance().saveBookChaptersWithAsync(catalogs, "" + wid, new AsyncOperationListener() {
                                    @Override
                                    public void onAsyncOperationCompleted(AsyncOperation operation) {
                                        getLocalChapterCatalog();
                                    }
                                });


                            }else{
                                NetRequest.error(WorkDetailActivity.this, getString(R.string.no_internet));
                            }
                        },
                        (e) -> {
                            LogUtils.e("目录加载失败=========" + e);
                            NetRequest.error(WorkDetailActivity.this, getString(R.string.no_internet));
                        }

                );
        addDisposable(disposable);

//
//        NetRequest.workCatalog(wid, start, count, new OkHttpResult() {
//
//            @Override
//            public void onSuccess(JSONObject data) {
//                String serverNo = JSONUtil.getString(data, "ServerNo");
//                if (SN000.equals(serverNo)) {
//                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
//                    JSONArray array = JSONUtil.getJSONArray(result, "catalog");
//
//                    for (int i = ZERO; array != null && i < array.length(); i++) {
//                        JSONObject child = JSONUtil.getJSONObject(array, i);
//                        Catalog catalog = BeanParser.getCatalog(child);
//                        catalogs.add(catalog);
//                    }
//                    if (catalogAdapter.reverse){
//                        catalogAdapter.notifyDataSetChanged();
//                    }else{
//                        catalogAdapter.notifyItemRangeInserted(catalogs.size()-array.length(),array.length());
//                    }
//                    setCount();
//                } else {
//                    NetRequest.error(WorkDetailActivity.this, getString(R.string.no_internet));
//
//                }
//                ll_start_read.setBackgroundResource(R.color.color_FFFFFF);
//            }
//
//            @Override
//            public void onFailure(String error) {
//
//            }
//        });
    }

    /**
     * 章节目录弹窗点击事件
     */
    private final NewReadCatalogAdapter.OnItemClickListener catalogItemClick =
            new NewReadCatalogAdapter.OnItemClickListener() {
                @SuppressLint("WrongConstant")
                @Override
                public void onItemClick(RecyclerView.ViewHolder viewHolder, int chapterPos) {
                    work.lastChapterPosition = Constant.ZERO;
                    int lastChapterPos = chapterPos;
                    if(catalogAdapter != null)
                    {
                        catalogAdapter.update(chapterPos);

                        if (catalogAdapter.reverse) { // 取数据，注意正序倒序
                            lastChapterPos = catalogs.size() - 1 - lastChapterPos;
                        }

                        work.lastChapterOrder = lastChapterPos;
                        work.lastChapterId = Integer.parseInt(catalogs.get(lastChapterPos).getChapterId().trim());
                        work.lasttime = ComYou.currentTimeSeconds();
                        work.toReadType = 1;
                        Intent intent = new Intent(context, ReadActivity.class);
                        intent.putExtra("work", work);
                        CollBookBean mCollBook  = new CollBookBean();
                        mCollBook.setTitle(work.title);
                        mCollBook.set_id(work.wid+"");
                        intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
                        ShelfUtil.insertRecord(work);
                        startActivity(intent);
                    }
                }
            };

//    /**
//     * 目录适配器
//     *
//     * @author haojie
//     */
//    private class CatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//        private boolean reverse;
//
//        public void update(boolean reverse) {
//            this.reverse = reverse;
//            notifyDataSetChanged();
//
//        }
//
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new CatalogViewHolder(LayoutInflater.from(context).inflate(R.layout.item_catalog, parent, FALSE));
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            Catalog catalog;
//            if (reverse) {
//                catalog = catalogs.get(catalogs.size() - position - 1);
//            } else {
//                catalog = catalogs.get(position);
//            }
//            CatalogViewHolder catalogViewHolder = (CatalogViewHolder) holder;
//            catalogViewHolder.title.setText(catalog.title);
//            catalogViewHolder.status.setVisibility(catalog.isvip == ZERO ? View.GONE : View.VISIBLE);
//            holder.itemView.setOnClickListener(new OnItemClick(catalog));
//        }
//
//        @Override
//        public int getItemCount() {
//            return catalogs.size();
//        }
//
//    }
//
//    /**
//     * 目录布局装载器
//     *
//     * @author haojie
//     */
//    class CatalogViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.title)
//        TextView title;
//        @BindView(R.id.status)
//        View status;
//
//        CatalogViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    private class OnItemClick implements View.OnClickListener {
//
//        private final Catalog catalog;
//
//        OnItemClick(Catalog catalog) {
//            this.catalog = catalog;
//        }
//
//        @Override
//        public void onClick(View v) {
//            work.lastChapterPosition = ZERO;
//            work.lastChapterOrder = catalog.order-1;
//            work.lastChapterId = catalog.id;
//            work.lasttime = aiyeUtil.currentTimeSeconds();
//            Intent intent = new Intent(context, ReaderReadActivity.class);
//            intent.putExtra("work", work);
//            CollBookBean mCollBook  = new CollBookBean();
//            mCollBook.setTitle(work.title);
//            mCollBook.set_id(work.wid+"");
//            intent.putExtra(ReaderConstant.EXTRA_COLL_BOOK, mCollBook);
//            ShelfUtil.insertRecord(work);
//            startActivity(intent);
//
//        }
//    }


    /**
     * 评论
     */
    private void getComment() {
        NetRequest.workCommentList(wid, Constant.ONE, Constant.ONE, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (Constant.SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == Constant.ONE) {
                        comments.clear();
                        commentCount = JSONUtil.getInt(result, "count");
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = Constant.ZERO; lists != null && i < lists.length(); i++) {
                            Comment comment = BeanParser.getComment(JSONUtil.getJSONObject(lists, i));
                            comments.add(comment);
                        }
                        if (comments.size() > Constant.ZERO) {
                            updateCommentLayout();

                            if(tv_comment != null && tv_comment_wai != null && noComment != null && ll_comments != null) {
                                tv_comment.setText(getResources().getString(R.string.comments) + String.format(Locale.getDefault(), "（%d）", commentCount));
                                tv_comment_wai.setText(getResources().getString(R.string.comments) + String.format(Locale.getDefault(), "（%d）", commentCount));
                                noComment.setVisibility(View.GONE);
                                ll_comments.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if(ll_comments != null && noComment != null)
                            {
                                ll_comments.setVisibility(View.GONE);
                                noComment.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if(noComment != null) {
                            noComment.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if(noComment != null) {
                        noComment.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(String error) {


                if(noComment != null)
                {
                    noComment.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 更新评论的显示
     */
    private void updateCommentLayout() {
        if (WorkDetailActivity.this == null ||
                WorkDetailActivity.this.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && WorkDetailActivity.this.isDestroyed())) {
            return;
        }
        ll_comment_group.removeAllViews();
        for (int i = Constant.ZERO; i < Math.min(Constant.THREE, comments.size()); i++) {
            ll_comment_group.addView(commentItemCreator.createView(comments.get(i)));
        }
    }


    /**
     * 推荐位数据
     */
    private void getRecommend() {


        rcv_content.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, Constant.FALSE));
        sortAdapter = new WorkInfoRecommendAdapter(context, sortWorks, Constant.ZERO);
        rcv_content.setAdapter(sortAdapter);


        NetRequest.workInfoRecommend(wid, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (Constant.SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == Constant.ONE) {
                        JSONObject author_rec = JSONUtil.getJSONObject(result, "author_rec");
                        JSONObject author_rec_info = JSONUtil.getJSONObject(author_rec, "rec_info");
                        JSONArray author_rec_list = JSONUtil.getJSONArray(author_rec, "rec_list");
                        for (int i = Constant.ZERO; author_rec_list != null && i < author_rec_list.length(); i++) {
                            Work work = BeanParser.getWork(JSONUtil.getJSONObject(author_rec_list, i));
                            work.recId = JSONUtil.getInt(author_rec_info, "rec_id");
                            work.config_num = JSONUtil.getInt(JSONUtil.getJSONObject(author_rec_list, i), "config_num");
                            JSONArray tag = JSONUtil.getJSONArray(JSONUtil.getJSONObject(author_rec_list, i), "tag");
                            List<TagBean> mTagBeans = new ArrayList<>();
                            for (int j = Constant.ZERO; tag != null && j < tag.length(); j++) {
                                TagBean mTagbean = new TagBean();
                                mTagbean.id = JSONUtil.getString(JSONUtil.getJSONObject(tag, j), "id");
                                mTagbean.tag = JSONUtil.getString(JSONUtil.getJSONObject(tag, j), "tag");
                                mTagBeans.add(mTagbean);
                            }
                            work.tag = mTagBeans;
                            otherWorks.add(work);
                        }
                        if (otherWorks.size() > Constant.ZERO) {
                            if(rl_upload != null)
                            {
                                rl_upload.setVisibility(View.VISIBLE);
                            }
                            setAuthorData();
                        } else {
                            if(rl_upload != null) {
                                rl_upload.setVisibility(View.GONE);
                            }
                        }
                        JSONObject sort_rec = JSONUtil.getJSONObject(result, "sort_rec");
                        JSONObject sort_rec_info = JSONUtil.getJSONObject(sort_rec, "rec_info");
                        JSONArray sort_list = JSONUtil.getJSONArray(sort_rec, "rec_list");
                        rec_id = JSONUtil.getInt(sort_rec_info, "rec_id");
                        String title = JSONUtil.getString(sort_rec_info, "title");
                        String ishot = JSONUtil.getString(sort_rec_info, "ishot");
                        //isimg  1显示 0不显示
                        String isimg = JSONUtil.getString(sort_rec_info, "isimg");
                        String recimg = JSONUtil.getString(sort_rec_info, "recimg");
                        if (!TextUtils.isEmpty(title)) {
                            tv_name_recommend.setText(title);
                        }
                        for (int i = Constant.ZERO; sort_list != null && i < sort_list.length(); i++) {
                            Work work = BeanParser.getWork(JSONUtil.getJSONObject(sort_list, i));
                            work.recId = JSONUtil.getInt(sort_rec_info, "rec_id");
                            work.config_num = JSONUtil.getInt(JSONUtil.getJSONObject(sort_list, i), "config_num");
                            sortWorks.add(work);
                        }
                        if (sortWorks.size() > Constant.ZERO) {
                            sortAdapter.setLable(isimg,recimg);
                            sortAdapter.setNotify(ishot);
                            ll_reconmend_list.setVisibility(View.VISIBLE);
                        } else {
                            ll_reconmend_list.setVisibility(View.GONE);
                        }

                    }
                }

            }

            @Override
            public void onFailure(String error) {
                rl_upload.setVisibility(View.GONE);
                ll_reconmend_list.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 更新评论的显示
     */
    private void setAuthorData() {
        if (random == null) {
            random = new Random();
        }
        if (otherWorks.size()>0) {
            try {
                Work mWork = otherWorks.get(random.nextInt(otherWorks.size()));
                GlideUtil.loads(context, mWork.cover, R.drawable.default_work_cover, recomment_cover);
                book_name.setText(mWork.title);
                book_author.setText(mWork.author);
                book_info.setText(mWork.description);
                read_num.setText(ComYou.formatNum(mWork.config_num));
//                read_num.setText(mWork.config_num + "");
                String tags = "";
                for (int i = 0; i < mWork.tag.size(); i++) {
                    if (i == mWork.tag.size() - 1) {
                        tags += mWork.tag.get(i).tag;
                    } else {
                        tags += mWork.tag.get(i).tag + " · ";
                    }

                }
                book_other.setText(tags);
                mWorkid = mWork.wid;
            } catch (Exception e) {

            }
        }


//        book_author.setText(mWork.);
    }


    void onDirectoryUpClick(LinearLayout a,LinearLayout b) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        ctrlAnimation.setDuration(400l);     //设置动画的过渡时间
        a.setVisibility(View.VISIBLE);
        b.startAnimation(ctrlAnimation);



    }

    void onDirectoryDownClick(LinearLayout a,LinearLayout b) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(getResources().getColor(R.color.colorWhite));

        }
        if (b.getVisibility() == View.VISIBLE) {
            final TranslateAnimation ctrlAnimation = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                    TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
            ctrlAnimation.setDuration(400l);     //设置动画的过渡时间

            b.startAnimation(ctrlAnimation);
            ctrlAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                    b.setVisibility(View.GONE);
                    a.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

            });
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(b.getWindowToken(), 0);
        }

    }


    private void addScoreComment(String content, int score) {
        NetRequest.workAddScoreComment(wid, score, content, okHttpResult);
    }

    private void addComment(String content) {
        NetRequest.workAddComment(wid, Constant.ZERO, Constant.ONE, Constant.ZERO, Constant.ZERO,
                PlotRead.getAppUser().uid, Constant.BLANK, content, okHttpResult);
    }

    private final OkHttpResult okHttpResult = new OkHttpResult() {

        @Override
        public void onSuccess(JSONObject data) {
            LoadingAlertDialog.dismiss(loadingDialog);
            String serverNo = JSONUtil.getString(data, "ServerNo");
            if (Constant.SN000.equals(serverNo)) {
                JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                int status = JSONUtil.getInt(result, "status");
                if (status == Constant.ONE) {
                    JSONObject child = JSONUtil.getJSONObject(result, "comment");
                    Comment comment = BeanParser.getComment(child);
                    // 发送通知
                    Message message = Message.obtain();
                    message.what = Constant.BUS_WORK_COMMENT_ADD_SUCCESS;
                    message.obj = comment;
                    EventBus.getDefault().post(message);
                    PlotRead.toast(PlotRead.SUCCESS, getString(R.string.published_success));
                    onDirectoryDownClick(ll_white_comment,ll_white_comment_in);
                } else {
                    String msg = JSONUtil.getString(result, "msg");
                    PlotRead.toast(PlotRead.FAIL, msg);
                }
            } else {
                NetRequest.error(WorkDetailActivity.this, serverNo);
            }
        }

        @Override
        public void onFailure(String error) {
            LoadingAlertDialog.dismiss(loadingDialog);
            PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == Constant.BUS_SHELF_CHANGE) {
            return;
        }
        if (message.what == Constant.BUS_WORK_COMMENT_ADD_SUCCESS
                || message.what == Constant.BUS_SEND_CHAPTER_COMMENT_SUCCESS) {
            Comment comment = (Comment) message.obj;
            if (comment.wid == wid) {
                comments.add(Constant.ZERO, comment);
                commentCount++;
                tv_comment.setText(getResources().getString(R.string.comments) + String.format(Locale.getDefault(), "（%d）", commentCount));
                tv_comment_wai.setText(getResources().getString(R.string.comments) + String.format(Locale.getDefault(), "（%d）", commentCount));

                updateCommentLayout();
                noComment.setVisibility(View.GONE);
                ll_comments.setVisibility(View.VISIBLE);
//                if (mScore.getProgress() == ZERO) {
//                    mScore.setProgress(comment.score);
//                }
            }
            return;
        }
        if (message.what == Constant.BUS_COMMENT_ADD_LIKE) {
            Comment temp = (Comment) message.obj;
            for (int i = Constant.ZERO; i < comments.size(); i++) {
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
        if (message.what == Constant.BUS_COMMENT_ADD_REPLY) {
            Comment temp = (Comment) message.obj;
            for (int i = Constant.ZERO; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                if (comment.equals(temp)) {
                    comment.replyCount = temp.replyCount;
                    updateCommentLayout();
                    break;
                }
            }
            return;
        }
        if (message.what == Constant.BUS_COMMENT_DELETE) {
            Comment temp = (Comment) message.obj;
            for (int i = Constant.ZERO; i < comments.size(); i++) {
                Comment comment = comments.get(i);
                if (comment.equals(temp)) {
                    comments.remove(i);
                    commentCount--;
                    tv_comment.setText(getResources().getString(R.string.comments) + String.format(Locale.getDefault(), "（%d）", commentCount));
                    tv_comment_wai.setText(getResources().getString(R.string.comments) + String.format(Locale.getDefault(), "（%d）", commentCount));
                    updateCommentLayout();
                    if (comments.size() > Constant.ZERO) {
                        noComment.setVisibility(View.GONE);
                        ll_comments.setVisibility(View.VISIBLE);
                    } else {
                        ll_comments.setVisibility(View.GONE);
                        noComment.setVisibility(View.VISIBLE);
                    }

                    break;
                }
            }
            return;
        }
        if (message.what == Constant.BUS_REWARD_SUCCESS) {
            if ((int) message.obj == wid) {
                getComment();
            }
        }
    }

    @Override
    public void onBackPressed() {
         if (ll_all.getVisibility() == View.VISIBLE) {
             onDirectoryDownClick(ll_all,ll_catalog);
        } else {
            super.onBackPressed();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

