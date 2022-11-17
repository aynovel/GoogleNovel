package life.forever.cf.activtiy;

import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.adapter.ClassifyDetailAdapter;
import life.forever.cf.adapter.ConditionAdapter;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.entry.ChildCondition;
import life.forever.cf.entry.ParentCondition;
import life.forever.cf.publics.BaseRecyclerViewActivity;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ClassifyDetailActivity extends BaseRecyclerViewActivity {

    TextView mScreening;
    TextView mTitleName;

    @BindView(R.id.recyclerView)
    RecyclerView mConditionRecyclerView;
    @BindView(R.id.reset)
    TextView mReset;
    @BindView(R.id.complete)
    TextView mComplete;

    /**
     * 筛选框
     */
    private View mScreeningLayout;

    boolean isScreeningShow;
    private AnimationSet screeningEnterAnimations;
    private AnimationSet screeningExitAnimations;

    private final List<Work> works = new ArrayList<>();
    private ClassifyDetailAdapter worksAdapter;

    private final List<ParentCondition> conditions = new ArrayList<>();
    private ConditionAdapter conditionAdapter;

    private int parent_sortid;
    private String pname;
    private Map<String, Object> map;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    @Override
    protected void initializeView() {
        super.initializeView();
        mRefreshLayout.setHasHeader(FALSE);
        mLoadFooter.setOnLoadListener(onLoadListener);
        LayoutInflater inflater = LayoutInflater.from(context);
        // 重定义标题栏
        View customTitle = inflater.inflate(R.layout.layout_choices_title_bar, null);
        mTitleBar.setCustomTitleView(customTitle);
        customTitle.findViewById(R.id.left_image_view).setOnClickListener(onBackClick);
        mScreening = findViewById(R.id.right_text_view);
        mTitleName = findViewById(R.id.middle_text_view);
        mScreening.setOnClickListener(onScreeningClick);
        // 筛选框
        mScreeningLayout = inflater.inflate(R.layout.layout_library_classify_child_screening, null);
        mContentLayout.addView(mScreeningLayout);
        mScreeningLayout.setVisibility(View.GONE);

        ButterKnife.bind(this, mScreeningLayout);
        mConditionRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        Intent data = getIntent();
        map = (Map<String, Object>) data.getSerializableExtra("map");
        parent_sortid = (int) map.get("parent_sortid");
        pname = (String) map.get("pname");

        mTitleName.setText(pname);
        mScreening.setText(aiye_STRING_SCREENING);

        worksAdapter = new ClassifyDetailAdapter(context, works);
        mRecyclerView.setAdapter(worksAdapter);

        conditionAdapter = new ConditionAdapter(context, conditions);
        mConditionRecyclerView.setAdapter(conditionAdapter);

        fetchResult();
        fetchCondition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_LOG_IN) {
            reload();
        }
    }

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        pageIndex = ONE;
        totalPage = ZERO;
        fetchResult();
    }

    private void fetchResult() {
        NetRequest.libraryScreenResult(map, pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                    works.clear();
                    worksAdapter.notifyDataSetChanged();
                }
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int total = JSONUtil.getInt(result, "total");
                    if (pageIndex == ONE && totalPage == ZERO) {
                        totalPage = total % TWENTY == ZERO ? total / TWENTY : total / TWENTY + ONE;
                        mRefreshLayout.setHasFooter(totalPage > ONE);
                    }
                    JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                    for (int i = ZERO; lists != null && i < lists.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(lists, i);
                        Work work = BeanParser.getWork(child);
                        works.add(work);
                    }
                    worksAdapter.notifyDataSetChanged();
                    pageIndex++;
                    mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                } else {
                    NetRequest.error(ClassifyDetailActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                    PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                } else {
                    dismissLoading();
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fetchCondition() {
        mScreening.setEnabled(FALSE);
        NetRequest.libraryScreenCondition(parent_sortid, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    mScreening.setEnabled(TRUE);
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    JSONArray lists = JSONUtil.getJSONArray(result, "filterlists");
                    for (int i = ZERO; lists != null && i < lists.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(lists, i);
                        ParentCondition parentCondition = new ParentCondition();
                        parentCondition.type = JSONUtil.getString(child, "type");
                        parentCondition.title = JSONUtil.getString(child, "title");
                        JSONArray childArray = JSONUtil.getJSONArray(child, "data");
                        for (int j = ZERO; childArray != null && j < childArray.length(); j++) {
                            JSONObject childObject = JSONUtil.getJSONObject(childArray, j);
                            ChildCondition childCondition = new ChildCondition();
                            childCondition.id = JSONUtil.getInt(childObject, "sid");
                            childCondition.title = JSONUtil.getString(childObject, "title");
                            parentCondition.conditions.add(childCondition);
                        }
                        parentCondition.checkId = getIntent().getIntExtra(parentCondition.type, -ONE);
                        conditions.add(parentCondition);
                    }
                    conditionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetchResult();
        }
    };

    @OnClick(R.id.reset)
    void onResetClick() {
        for (int i = ZERO; i < conditions.size(); i++) {
            conditions.get(i).checkId = -ONE;
        }
        conditionAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.complete)
    void onCompleteClick() {
        for (ParentCondition condition : conditions) {
            map.put(condition.type, condition.checkId);
        }
        pageIndex = ONE;
        totalPage = ZERO;
        closeScreening();
        showLoading(BLANK);
        fetchResult();
    }

    /**
     * 返回按钮
     */
    View.OnClickListener onBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    /**
     * 筛选按钮点击
     */
    View.OnClickListener onScreeningClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isScreeningShow) {
                closeScreening();
            } else {
                openScreening();
            }
        }
    };

    /**
     * 打开筛选框
     */
    private void openScreening() {
        if (screeningEnterAnimations == null) {
            initScreeningEnterAnimations();
        }
        mScreeningLayout.clearAnimation();
        mScreeningLayout.setVisibility(View.VISIBLE);
        mScreeningLayout.startAnimation(screeningEnterAnimations);
        isScreeningShow = TRUE;
        mScreening.setCompoundDrawablesWithIntrinsicBounds(ZERO, ZERO, R.drawable.on_up, ZERO);
    }

    /**
     * 关闭筛选框
     */
    private void closeScreening() {
        if (screeningExitAnimations == null) {
            initScreeningExitAnimations();
        }
        mScreeningLayout.clearAnimation();
        mScreeningLayout.startAnimation(screeningExitAnimations);
        isScreeningShow = FALSE;
        mScreening.setCompoundDrawablesWithIntrinsicBounds(ZERO, ZERO, R.drawable.icon_x, ZERO);
    }

    /**
     * 初始化筛选窗进入动画
     */
    private void initScreeningEnterAnimations() {
        AlphaAnimation aaIn = new AlphaAnimation(ZERO, ONE);
        ScaleAnimation saIn = new ScaleAnimation(ZERO, ONE, ZERO, ONE,
                Animation.RELATIVE_TO_SELF, ONE,
                Animation.RELATIVE_TO_SELF, ZERO);
        screeningEnterAnimations = new AnimationSet(TRUE);
        screeningEnterAnimations.addAnimation(aaIn);
        screeningEnterAnimations.addAnimation(saIn);
        screeningEnterAnimations.setDuration(THREE_HUNDRED);
        screeningEnterAnimations.setFillAfter(TRUE);
        screeningEnterAnimations.setInterpolator(new LinearInterpolator());
    }

    /**
     * 初始化筛选窗出去动画
     */
    private void initScreeningExitAnimations() {
        AlphaAnimation aaOut = new AlphaAnimation(ONE, ZERO);
        ScaleAnimation saOut = new ScaleAnimation(ONE, ZERO, ONE, ZERO,
                Animation.RELATIVE_TO_SELF, ONE,
                Animation.RELATIVE_TO_SELF, ZERO);
        screeningExitAnimations = new AnimationSet(TRUE);
        screeningExitAnimations.addAnimation(aaOut);
        screeningExitAnimations.addAnimation(saOut);
        screeningExitAnimations.setDuration(THREE_HUNDRED);
        screeningExitAnimations.setFillAfter(TRUE);
        screeningExitAnimations.setInterpolator(new LinearInterpolator());
        screeningExitAnimations.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mScreeningLayout.clearAnimation();
                mScreeningLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isScreeningShow) {
            closeScreening();
        } else {
            super.onBackPressed();
        }
    }
}
