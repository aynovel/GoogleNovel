package life.forever.cf.activtiy;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.ChapterUnlockBean;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.ChapterUnlockAdapter;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yuanlong on 2021/02/21.
 * 章节解锁记录
 */
public class ChapterUnlockActivity extends BaseActivity {

    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.loadFooter)
    LoadFooterView mLoadFooter;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.noneView)
    View mNoneView;

    private int pageIndex = ONE;
    private int totalPage = ZERO;
    private ChapterUnlockAdapter mChapterUnlockAdapter;
    private List<ChapterUnlockBean.ResultData.Lists> mChapterUnlockList = new ArrayList<>();

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(getString(R.string.bill_activity_chapter_unlock));
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.fragment_bill_recharge);
        ButterKnife.bind(this);
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    @Override
    protected void initializeData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // 初始化适配器
        mChapterUnlockAdapter = new ChapterUnlockAdapter(this, mChapterUnlockList);
        mRecyclerView.setAdapter(mChapterUnlockAdapter);
        mChapterUnlockAdapter.setOnItemClickListener(onItemClickListener);
        // 初始化数据
        workexpend();
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final ChapterUnlockAdapter.OnItemClickListener onItemClickListener = new ChapterUnlockAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(int position) {
            Work work = new Work();
            work.wid = Integer.parseInt(mChapterUnlockList.get(position).wid);
            if (mChapterUnlockList.get(position).lastChapterPos == 0) {
                work.lastChapterOrder = mChapterUnlockList.get(position).lastChapterPos;
            } else {
                work.lastChapterOrder = mChapterUnlockList.get(position).lastChapterPos - ONE;
            }
            startRead(work);
        }
    };


    /**
     * 开始阅读
     *
     * @param work
     */
    private void startRead(Work work) {
        work.updateflag = ZERO;
        work.lasttime = ComYou.currentTimeSeconds();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShelfUtil.insert(ChapterUnlockActivity.this, work,false);

            }
        }, 2000);
        work.toReadType = 0;
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra("work", work);
        CollBookBean mCollBook  = new CollBookBean();
        mCollBook.setTitle(work.title);
        mCollBook.set_id(work.wid+"");
        intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
        context.startActivity(intent);

    }

    /**
     * 根据作品数量是否为0来切换页面状态
     */
    private void switchPageBySize() {
        if (mChapterUnlockList == null || mChapterUnlockList.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
        } else {
            mNoneView.setVisibility(View.GONE);
        }
    }

    /**
     * 加载更多监听
     */
    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            workexpend();
        }
    };

    /*
     * 请求章节解锁记录数据
     */
    private void workexpend() {
        NetRequest.workexpend(pageIndex, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.stopLoad();
                        }
                    }
                    try {

                        if (pageIndex == ONE) {
                            int count = JSONUtil.getInt(result, "count");
                            totalPage = count % TWENTY == ZERO ? count / TWENTY : count / TWENTY + 1;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }

                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        JSONObject jsonOders = new JSONObject(resultString);
                        String strOrders = jsonOders.getString("list");

                        Type listType = new TypeToken<List<ChapterUnlockBean.ResultData.Lists>>() {
                        }.getType();
                        Gson gson = new Gson();

                        List<ChapterUnlockBean.ResultData.Lists> List = gson.fromJson(strOrders, listType);
                        if (List != null) {
                            if (mChapterUnlockAdapter == null) {
                                mChapterUnlockList = List;
                                mChapterUnlockAdapter.data(mChapterUnlockList);
                            } else {
                                mChapterUnlockList.addAll(List);
                                mChapterUnlockAdapter.data(mChapterUnlockList);
                            }
                        }
                        switchPageBySize();
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        switchPageBySize();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                switchPageBySize();
                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later！");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
