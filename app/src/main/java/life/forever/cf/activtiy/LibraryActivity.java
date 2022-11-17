package life.forever.cf.activtiy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.LibraryAdapter;
import life.forever.cf.entry.LibraryBean;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.RefreshHeaderView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.BaseHeaderView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.poputil.TagPopupWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LibraryActivity extends BaseActivity {

    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.refreshHeader)
    RefreshHeaderView mRefreshHeader;
    @BindView(R.id.loadfooter)
    LoadFooterView mLoadFooter;

    @BindView(R.id.gender_all)
    TextView mGenderAll;
    @BindView(R.id.gender_male)
    TextView mGenderMale;
    @BindView(R.id.gender_female)
    TextView mGenderFemale;

    @BindView(R.id.limit_all)
    TextView mLmitAll;
    @BindView(R.id.limit_charge)
    TextView mLmitCharge;
    @BindView(R.id.limit_free)
    TextView mLmitFree;

    @BindView(R.id.end_all)
    TextView mEndAll;
    @BindView(R.id.end_uncompleted)
    TextView mEndUncompleted;
    @BindView(R.id.end_completed)
    TextView mEndCompleted;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.noneView)
    View mNoneView;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    private TextView mMiddleTv;
    private String mTagtpye;
    private List<String> mSortsId;
    private String mTagid;

    private String sex = "";//性别 1 男 2 女
    private String is_vip = "";//是否收费 1 收费 0 免费
    private String is_finish = "";//是否完结 1 完结 0 连载

    private List<LibraryBean.ResultData.Records> mList = new ArrayList<>();
    private LibraryAdapter mLibraryAdapter;


    @Override
    protected void initializeView() {
        @SuppressLint("InflateParams")
        View titleBar = LayoutInflater.from(context).inflate(R.layout.layout_library_title_bar, null, FALSE);
        mTitleBar.setCustomTitleView(titleBar);
        titleBar.findViewById(R.id.left_image).setOnClickListener(onBackClick);
        mMiddleTv = titleBar.findViewById(R.id.middle_tv);
        titleBar.findViewById(R.id.layout_right).setOnClickListener(onTagsClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRefreshHeader.setOnRefreshListener(onRefreshListener);
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    @Override
    protected void initializeData() {
        String mTagName = getIntent().getStringExtra("tagname");
        mTagtpye = getIntent().getStringExtra("tagtype");
        mSortsId = (List<String>) getIntent().getSerializableExtra("sortsid");
        mTagid = getIntent().getStringExtra("tagid");
        mMiddleTv.setText(mTagName);
        mGenderAll.setOnClickListener(onGenderAllClick);
        mGenderMale.setOnClickListener(onGenderMaleClick);
        mGenderFemale.setOnClickListener(onGenderFemaleClick);

        mLmitAll.setOnClickListener(onLmitAllClick);
        mLmitCharge.setOnClickListener(onLmitChargeClick);
        mLmitFree.setOnClickListener(onLmitFreeClick);

        mEndAll.setOnClickListener(onEndAllClick);
        mEndUncompleted.setOnClickListener(onEndUncompletedClick);
        mEndCompleted.setOnClickListener(onEndCompletedClick);

        mLibraryAdapter = new LibraryAdapter(LibraryActivity.this, mList);
        mLibraryAdapter.setOnItemClickListener(onItemClickListener);
        mRecyclerView.setAdapter(mLibraryAdapter);
        fetch(mTagid, mTagtpye, mSortsId, pageIndex);
    }

    /**
     * 根据作品数量是否为0来切换页面状态
     */
    private void switchPageBySize() {
        if (mList.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
        } else {
            mNoneView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();

    /*
     * 左边tags
     */
    private final View.OnClickListener onTagsClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            TagPopupWindow mPopupWindow = new TagPopupWindow(LibraryActivity.this, mTagid);
            mPopupWindow.show();
            mPopupWindow.setPopupWindowOnClick((title, tag_id) -> {
                mMiddleTv.setText(title);
                mTagid = tag_id;
                fetch(mTagid, mTagtpye, mSortsId, ONE);
            });
        }
    };

    private final View.OnClickListener onGenderAllClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            sex = "";
            mGenderAll.setBackgroundResource(R.drawable.bg_button_cancel);
            mGenderAll.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mGenderMale.setBackgroundResource(R.drawable.bg_button_fantasy);
            mGenderMale.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mGenderFemale.setBackgroundResource(R.drawable.bg_button_fantasy);
            mGenderFemale.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };
    private final View.OnClickListener onGenderMaleClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            sex = "1";
            mGenderMale.setBackgroundResource(R.drawable.bg_button_cancel);
            mGenderMale.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mGenderAll.setBackgroundResource(R.drawable.bg_button_fantasy);
            mGenderAll.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mGenderFemale.setBackgroundResource(R.drawable.bg_button_fantasy);
            mGenderFemale.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };
    private final View.OnClickListener onGenderFemaleClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            sex = "2";
            mGenderFemale.setBackgroundResource(R.drawable.bg_button_cancel);
            mGenderFemale.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mGenderAll.setBackgroundResource(R.drawable.bg_button_fantasy);
            mGenderAll.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mGenderMale.setBackgroundResource(R.drawable.bg_button_fantasy);
            mGenderMale.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };


    private final View.OnClickListener onLmitAllClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            is_vip = "";
            mLmitAll.setBackgroundResource(R.drawable.bg_button_cancel);
            mLmitAll.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mLmitCharge.setBackgroundResource(R.drawable.bg_button_fantasy);
            mLmitCharge.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mLmitFree.setBackgroundResource(R.drawable.bg_button_fantasy);
            mLmitFree.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };
    private final View.OnClickListener onLmitChargeClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            is_vip = "1";
            mLmitCharge.setBackgroundResource(R.drawable.bg_button_cancel);
            mLmitCharge.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mLmitAll.setBackgroundResource(R.drawable.bg_button_fantasy);
            mLmitAll.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mLmitFree.setBackgroundResource(R.drawable.bg_button_fantasy);
            mLmitFree.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };
    private final View.OnClickListener onLmitFreeClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            is_vip = "0";
            mLmitFree.setBackgroundResource(R.drawable.bg_button_cancel);
            mLmitFree.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mLmitAll.setBackgroundResource(R.drawable.bg_button_fantasy);
            mLmitAll.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mLmitCharge.setBackgroundResource(R.drawable.bg_button_fantasy);
            mLmitCharge.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };

    private final View.OnClickListener onEndAllClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            is_finish = "";
            mEndAll.setBackgroundResource(R.drawable.bg_button_cancel);
            mEndAll.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mEndUncompleted.setBackgroundResource(R.drawable.bg_button_fantasy);
            mEndUncompleted.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mEndCompleted.setBackgroundResource(R.drawable.bg_button_fantasy);
            mEndCompleted.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };
    private final View.OnClickListener onEndUncompletedClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            is_finish = "0";
            mEndUncompleted.setBackgroundResource(R.drawable.bg_button_cancel);
            mEndUncompleted.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mEndAll.setBackgroundResource(R.drawable.bg_button_fantasy);
            mEndAll.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mEndCompleted.setBackgroundResource(R.drawable.bg_button_fantasy);
            mEndCompleted.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };
    private final View.OnClickListener onEndCompletedClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            is_finish = "1";
            mEndCompleted.setBackgroundResource(R.drawable.bg_button_cancel);
            mEndCompleted.setTextColor(context.getResources().getColor(R.color.color_F97A1C));

            mEndAll.setBackgroundResource(R.drawable.bg_button_fantasy);
            mEndAll.setTextColor(context.getResources().getColor(R.color.colorBlack));

            mEndUncompleted.setBackgroundResource(R.drawable.bg_button_fantasy);
            mEndUncompleted.setTextColor(context.getResources().getColor(R.color.colorBlack));
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };

    private final LibraryAdapter.OnItemClickListener onItemClickListener = position -> {
        if (mList != null && mList.size() > 0) {
            LibraryBean.ResultData.Records recordBean = mList.get(position);
            if(recordBean != null)
            {
                String beanWid = mList.get(position).wid;
                if(beanWid != null)
                {
                    Intent intent = new Intent();
                    intent.setClass(LibraryActivity.this, WorkDetailActivity.class);
                    intent.putExtra("wid", Integer.parseInt(mList.get(position).wid));
                    intent.putExtra("recid", 0);
                    startActivity(intent);
                }
            }
        }
    };


    /**
     * 刷新监听
     */
    private final BaseHeaderView.OnRefreshListener onRefreshListener = new BaseHeaderView.OnRefreshListener() {

        @Override
        public void onRefresh(BaseHeaderView baseHeaderView) {
//            mLibraryAdapter.update();
            if (mList != null) {
                mList.clear();
            }
            fetch(mTagid, mTagtpye, mSortsId, ONE);
        }
    };

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetch(mTagid, mTagtpye, mSortsId, pageIndex);
        }
    };

    private void fetch(String Tagid, String Tagtpye, List<String> sorts_id, int page) {
        if (ComYou.isDestroy(LibraryActivity.this)) {
            return;
        }
        showLoading(getString(R.string.content_loading));
        NetRequest.libraryRequest(Tagid, Tagtpye, sorts_id, page, sex, is_vip, is_finish, new OkHttpResult() {

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
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.stopRefresh();
                        }
                        mRefreshLayout.setHasHeader(TRUE);
                    }
                    try {
                        if (page == ONE) {
                            int count = JSONUtil.getInt(result, "total");
                            totalPage = count % TWENTY == ZERO ? count / TWENTY : count / TWENTY + 1;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }

//                        if (pageIndex == ONE && totalPage == ZERO) {
//                            int count = JSONUtil.getInt(result, "total");
//                            totalPage = count % TWENTY == ZERO ? count / TWENTY : count / TWENTY + ONE;
//                            mRefreshLayout.setHasFooter(totalPage > ONE);
//                        }

                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        JSONObject jsonOders = new JSONObject(resultString);
                        String strResult = jsonOders.getString("records");

                        Type listType = new TypeToken<List<LibraryBean.ResultData.Records>>() {
                        }.getType();
                        Gson gson = new Gson();
//                        mList = gson.fromJson(strResult, listType);
//                        switchPageBySize();
//                        mLibraryAdapter.data(mList);

                        List<LibraryBean.ResultData.Records> List = gson.fromJson(strResult, listType);

                        if (mLibraryAdapter == null) {
                            mList = List;
                        } else {
                            mList.clear();
                            mList.addAll(List);
                        }
                        switchPageBySize();
                        mLibraryAdapter.data(mList);
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    switchPageBySize();
                    NetRequest.error(LibraryActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                switchPageBySize();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.stopRefresh();
                }
                mLoadingLayout.setVisibility(View.GONE);
                mWrongLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
