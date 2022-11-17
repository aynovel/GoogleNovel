package life.forever.cf.activtiy;

import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.adapter.HotKeyAdapter;
import life.forever.cf.adapter.HotSearchWorkAdapter;
import life.forever.cf.adapter.SearchHistoryAdapter;
import life.forever.cf.adapter.SearchResultAdapter;
import life.forever.cf.datautils.SearchUtil;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.SearchKey;
import life.forever.cf.entry.TagBean;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.OnTextChangeListener;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.scrollweight.RecyclerFrameLayout;
import life.forever.cf.publics.scrollweight.ScrollLayout;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.FlowLayout;
import life.forever.cf.publics.weight.PowerEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.loadFooter)
    LoadFooterView mLoadFooter;
    @BindView(R.id.resultRecyclerView)
    RecyclerView mResultRecyclerView;

    @BindView(R.id.scrollLayout)
    ScrollLayout mScrollLayout;
    @BindView(R.id.ll_history)
    LinearLayout mLlHistory;
    @BindView(R.id.recommendLayout)
    View mRecommendLayout;
    @BindView(R.id.recyclerFrameLayout)
    RecyclerFrameLayout mRecyclerFrameLayout;
    RecyclerView mSearchHistoryRecyclerView;

    @BindView(R.id.hotSearchKeyLayout)
    View mHotSearchKeyLayout;
    @BindView(R.id.switchKeys)
    View mSwitchKeys;
    @BindView(R.id.flowLayout)
    FlowLayout mFlowLayout;

    @BindView(R.id.hotSearchWorkLayout)
    View mHotSearchWorkLayout;
    @BindView(R.id.switchWorks)
    View mSwitchWorks;
    @BindView(R.id.hotSearchRecyclerView)
    RecyclerView mHotSearchRecyclerView;

    private PowerEditText mETSearch;
    private String mRecWords;

    private final List<String> totalHotKeys = new ArrayList<>();
    private final List<String> showHotKeys = new ArrayList<>();
    private final List<Work> totalHotWorks = new ArrayList<>();
    private final List<Work> showHotWorks = new ArrayList<>();
    private final List<SearchKey> searchKeys = new ArrayList<>();
    private final List<Work> results = new ArrayList<>();
    private HotSearchWorkAdapter hotSearchWorkAdapter;
    private SearchHistoryAdapter searchHistoryAdapter;
    private SearchResultAdapter searchResultAdapter;

    private Work work;

    private int pageIndex = ONE;
    private int totalPage = ZERO;
    private boolean isFirst = true;

    @Override
    protected void initializeView() {
        mTitleBar.setVisibility(View.GONE);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mETSearch = findViewById(R.id.et_search);
        mETSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = mETSearch.getText().toString().trim();
                    if (TextUtils.isEmpty(key)) {
                        key = mETSearch.getHint().toString().trim();
                    }
                    DeepLinkUtil.addPermanent(SearchActivity.this, "event_search", "搜索页", "点击搜索", "", "", "", "", key, "");
                    if (TextUtils.isEmpty(key)) {
                        PlotRead.toast(PlotRead.INFO, getString(R.string.search_keyword));
                        return false;
                    }
                    // 保存搜索记录
                    SearchKey searchKey = new SearchKey();
                    searchKey.keyWord = key;
                    searchKey.timestamp = ComYou.currentTimeSeconds();
                    SearchUtil.insert(searchKey);
                    // 执行搜索
                    search(searchKey.keyWord);
                }
                return false;
            }
        });

        findViewById(R.id.tv_cancel).setOnClickListener(onBackClick);
        mHotSearchRecyclerView.setLayoutManager(new GridLayoutManager(context, THREE));
        mSearchHistoryRecyclerView = (RecyclerView) mRecyclerFrameLayout.getScrollView();
        mSearchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mScrollLayout.getHelper().setCurrentScrollParent(mRecyclerFrameLayout);
        mResultRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mLoadFooter.setOnLoadListener(onLoadListener);
        mETSearch.setOnTextChangeListener(onTextChangeListener);
        mETSearch.setOnFocusChangeListener(mOnFocusChangeListener);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }


    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        hotSearchWorkAdapter = new HotSearchWorkAdapter(context, showHotWorks);
        mHotSearchRecyclerView.setAdapter(hotSearchWorkAdapter);

        searchHistoryAdapter = new SearchHistoryAdapter(context, searchKeys);
        mSearchHistoryRecyclerView.setAdapter(searchHistoryAdapter);

        searchResultAdapter = new SearchResultAdapter(context, results);
        mResultRecyclerView.setAdapter(searchResultAdapter);

        searchHistoryAdapter.setOnItemClickListener(onHistoryItemClickListener);
        searchHistoryAdapter.setOnItemClearClickListener(onItemClearClickListener);

        fetchRecommend();
        fetchHistory();
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_SEARCH_CHANGE) {
            searchKeys.clear();
            fetchHistory();
        }
    }

    /**
     * 获取搜索历史
     */
    private void fetchHistory() {
        searchKeys.addAll(SearchUtil.query());
        if (searchKeys.size() == 0) {
            mLlHistory.setVisibility(View.GONE);
        } else {
            mLlHistory.setVisibility(View.VISIBLE);
        }
        searchHistoryAdapter.notifyDataSetChanged();
    }

    /**
     * 获取热搜词及推荐
     */
    private void fetchRecommend() {
        NetRequest.searchRecommend(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    String hotWords = JSONUtil.getString(result, "hot_words");
                    mRecWords = JSONUtil.getString(result, "rec_words");
                    String[] split = hotWords.split(";");
                    for (String s : split) {
                        String temp = s.trim();
                        if (!TextUtils.isEmpty(temp)) {
                            totalHotKeys.add(temp);
                        }
                    }
                    if (!TextUtils.isEmpty(mRecWords)) {
//                        mETSearch.setText(mRecWords);

                        mETSearch.setHint(mRecWords);
                    }
                    JSONObject bookList = JSONUtil.getJSONObject(result, "bookList");
                    JSONObject rec_info = JSONUtil.getJSONObject(bookList, "rec_info");
                    int recId = JSONUtil.getInt(rec_info, "rec_id");
                    JSONArray rec_list = JSONUtil.getJSONArray(bookList, "rec_list");
                    for (int i = ZERO; rec_list != null && i < rec_list.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(rec_list, i);
                        Work work = BeanParser.getWork(child);
                        work.recId = recId;
                        totalHotWorks.add(work);
                    }
                    if (totalHotKeys.size() == ZERO && totalHotWorks.size() == ZERO) {
                        mRecommendLayout.setVisibility(View.GONE);
                    } else {
                        mRecommendLayout.setVisibility(View.VISIBLE);
                        if (totalHotKeys.size() == ZERO) {
                            mHotSearchKeyLayout.setVisibility(View.GONE);
                        } else {
                            mHotSearchKeyLayout.setVisibility(View.VISIBLE);
                            switchHotKeys();
                            if (totalHotKeys.size() <= TEN) {
                                mSwitchKeys.setVisibility(View.GONE);
                            } else {
                                mSwitchKeys.setVisibility(View.VISIBLE);
                            }
                        }
                        if (totalHotWorks.size() == ZERO) {
                            mHotSearchWorkLayout.setVisibility(View.GONE);
                        } else {
                            mHotSearchWorkLayout.setVisibility(View.VISIBLE);
                            switchHotWorks();
                            if (totalHotWorks.size() <= THREE) {
                                mSwitchWorks.setVisibility(View.GONE);
                            } else {
                                mSwitchWorks.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    mRecommendLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String error) {
                mRecommendLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 热搜词换一换
     */
    private void switchHotKeys() {
        showHotKeys.clear();
        int[] random = ComYou.getRandomArray(totalHotKeys.size());
        for (int i = ZERO; i < TEN && i < totalHotKeys.size(); i++) {
            showHotKeys.add(totalHotKeys.get(random[i] - ONE));
        }
        mFlowLayout.setAdapter(new HotKeyAdapter(context, showHotKeys, onHotKeyItemClick));
    }

    /**
     * 热搜书换一换
     */
    private void switchHotWorks() {
        showHotWorks.clear();
        int[] random = ComYou.getRandomArray(totalHotWorks.size());
        for (int i = ZERO; i < THREE && i < totalHotWorks.size(); i++) {
            showHotWorks.add(totalHotWorks.get(random[i] - ONE));
        }
        hotSearchWorkAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.switchKeys)
    void onSwitchKeys() {
        switchHotKeys();
    }

    @OnClick(R.id.switchWorks)
    void onSwitchWorks() {
        switchHotWorks();
    }

    @OnClick(R.id.clear)
    void onClearClick() {
        SearchUtil.delete();
    }

    private final OnTextChangeListener onTextChangeListener = new OnTextChangeListener() {

        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after) {
            mScrollLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.setVisibility(View.GONE);
            // 初始化搜索结果
            pageIndex = ONE;
            totalPage = ZERO;
            results.clear();
            searchResultAdapter.update(BLANK);
        }
    };
    private final View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && isFirst) {
                // 此处为得到焦点时的处理内容
                mETSearch.setText("");
                if (TextUtils.isEmpty(mRecWords)) {
                    mETSearch.setHint("");
                } else {
                    mETSearch.setHint(mRecWords);
                }
                isFirst = false;
            }
        }
    };

    /**
     * 删除某一条搜索记录
     */
    private final SearchHistoryAdapter.OnItemClearClickListener onItemClearClickListener = SearchUtil::deletes;
    /**
     * 搜索历史条目点击
     */
    private final SearchHistoryAdapter.OnItemClickListener onHistoryItemClickListener = new SearchHistoryAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(int position) {
            SearchKey searchKey = searchKeys.get(position);
            searchKey.timestamp = ComYou.currentTimeSeconds();
            SearchUtil.insert(searchKey);

            mETSearch.setText(searchKey.keyWord);
            mETSearch.setSelection(searchKey.keyWord.length());
            search(searchKey.keyWord);
        }
    };

    /**
     * 热搜词条目点击
     */
    private final HotKeyAdapter.OnItemClickListener onHotKeyItemClick = new HotKeyAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            String key = showHotKeys.get(position).split("#")[0];
            SearchKey searchKey = new SearchKey();
            searchKey.keyWord = key;
            searchKey.timestamp = ComYou.currentTimeSeconds();
            SearchUtil.insert(searchKey);

            mETSearch.setText(searchKey.keyWord);
            mETSearch.setSelection(searchKey.keyWord.length());
            search(key);

            DeepLinkUtil.addPermanent(SearchActivity.this, "event_search_word", "搜索页", "点击热词", "", "", "", "", key, "");

        }
    };

    /**
     * 加载更多监听
     */
    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            String key = mETSearch.getText().toString().trim();
            if (TextUtils.isEmpty(key)) {
                PlotRead.toast(PlotRead.INFO, getString(R.string.search_keyword));
                return;
            }
            search(key);
        }
    };

    /**
     * 请求搜索
     *
     * @param keyword
     */
    private void search(final String keyword) {
        if (ComYou.isDestroy(SearchActivity.this)) {
            return;
        }
        ComYou.closeKeyboard(mETSearch, SearchActivity.this);
        showLoading(getString(R.string.searching));
        NetRequest.search(keyword, ONE, pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        }
                        if (pageIndex == ONE) {
                            int total = JSONUtil.getInt(result, "total");
                            totalPage = total % TWENTY == ZERO ? total / TWENTY : total / TWENTY + 1;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        JSONObject child = null;
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            child = JSONUtil.getJSONObject(lists, i);
                            work = BeanParser.getWork(child);
                            results.add(work);
                        }
                        JSONArray tag = JSONUtil.getJSONArray(child, "tag");
                        List<TagBean> mTagBeans = new ArrayList<>();
                        for (int i = ZERO; tag != null && i < tag.length(); i++) {
                            TagBean mTagbean = new TagBean();
                            mTagbean.id = JSONUtil.getString(JSONUtil.getJSONObject(tag, i), "id");
                            mTagbean.tag = JSONUtil.getString(JSONUtil.getJSONObject(tag, i), "tag");
                            mTagBeans.add(mTagbean);
                        }
                        if (tag != null) {
                            work.tag = mTagBeans;
                        }

                        searchResultAdapter.update(keyword);
                        mRefreshLayout.setVisibility(View.VISIBLE);
                        mScrollLayout.setVisibility(View.GONE);

                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.FAIL, msg);
                    }
                } else {
                    NetRequest.error(SearchActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (null != this.getCurrentFocus()) {
            InputMethodManager manager = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
            manager.hideSoftInputFromWindow(
                    SearchActivity.this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS
            );
        }
        if (mRefreshLayout.getVisibility() == View.VISIBLE) {
            mETSearch.setText(BLANK);
        } else {
            super.onBackPressed();
        }
    }
}
