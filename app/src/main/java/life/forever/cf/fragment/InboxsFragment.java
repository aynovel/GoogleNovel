package life.forever.cf.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.WerActivity;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.InboxBean;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.adapter.InboxAdapter;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.activtiy.ReadActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import life.forever.cf.activtiy.Cods;


public class InboxsFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.noneView)
    View mNoneView;

    private InboxAdapter mInboxAdapter;
    public boolean isMessageAlert;
    private List<InboxBean.ResultData.Lists.Rec_list> mProjectList = new ArrayList<>();
    private final List<InboxBean.ResultData.Lists.Rec_list> mReCordProjectList = new ArrayList<>();
    public SharedPreferences config;
    @SuppressLint("StaticFieldLeak")
    private static InboxsFragment instance;

    public static InboxsFragment get() {
        instance = new InboxsFragment();
        return instance;
    }

    @Override
    protected void bindView() {
        mTitleBar.setVisibility(View.GONE);
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_inboxs, mContentLayout, TRUE);
        ButterKnife.bind(this, root);
        mNoneView.setOnClickListener(onRefreshClick);
        mWrongLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWrongLayout.setVisibility(View.GONE);
                mLoadingLayout.setVisibility(View.VISIBLE);
                projectlist();
            }
        });
    }

    @Override
    protected void fetchData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        config = SharedPreferencesUtil.getSharedPreferences(INBOXS + PlotRead.getAppUser().uid);
        // 初始化适配器
        mInboxAdapter = new InboxAdapter(getActivity(), mProjectList);
        mInboxAdapter.setOnItemClickListener(onItemClickListener);
        mRecyclerView.setAdapter(mInboxAdapter);
        // 初始化数据
        projectlist();

    }

    /**
     * 根据作品数量是否为0来切换页面状态
     */
    private void switchPageBySize() {
        if (mProjectList.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
        } else {
            mNoneView.setVisibility(View.GONE);
        }
    }

    private final View.OnClickListener onRefreshClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            projectlist();
        }
    };

    private final InboxAdapter.OnItemClickListener onItemClickListener = position -> {
        InboxBean.ResultData.Lists.Rec_list bean = mProjectList.get(position);
        Intent intent = new Intent();
        /*
         * advertise_type: 广告类型 1：作品 2：内部链接 3：外部链接
         * readflag: 0：作品信息 1：阅读
         */
        String advertise_type = bean.advertise_type;
        String recid = bean.rec_id;
        if ("1".equals(advertise_type)) {
            String readflag = bean.advertise_data.readflag;
            int wids = Integer.parseInt(bean.advertise_data.wid);
            if ("1".equals(readflag)) {
                Work work = new Work();
                work.wid = wids;
                intent.setClass(context, ReadActivity.class);
                intent.putExtra("work", work);

                CollBookBean mCollBook  = new CollBookBean();
                mCollBook.setTitle(work.title);
                mCollBook.set_id(work.wid+"");
                intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
                startActivity(intent);
            } else {
                /*int recids = Integer.parseInt(mList.get(position).advertise_data.rec_id);*/
                intent.setClass(context, WorkDetailActivity.class);
                intent.putExtra("wid", wids);
                intent.putExtra("recid", 0);
            }
            context.startActivity(intent);
        } else if ("2".equals(advertise_type)) {
            String ht = bean.advertise_data.ht;
            String path = bean.advertise_data.path;
            String ps = bean.advertise_data.ps;
            String is = bean.advertise_data.is;
            String su = bean.advertise_data.su;
            String st = bean.advertise_data.st;
            String ifreash = bean.advertise_data.ifreash;
            intent.setClass(context, WerActivity.class);

            intent.putExtra("index", ht);
            intent.putExtra("path", path);
            intent.putExtra("pagefresh", ps);
            intent.putExtra("share", is);
            intent.putExtra("shareUrl", su);
            intent.putExtra("shareType", st);
            intent.putExtra("sharefresh", ifreash);
            context.startActivity(intent);
        } else if ("3".equals(advertise_type)) {
            String url = bean.advertise_data.url;
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
        DeepLinkUtil.addPermanent(context,"event_inbox_click","专题推荐位","推荐位"+recid,"","","","","","");
    };

    /*
     * 请求专题列表数据
     */
    private void projectlist() {

        NetRequest.projectRequest(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        JSONObject jsonOders = new JSONObject(resultString);
                        String strOrders = jsonOders.getString("list");

                        JSONObject json = new JSONObject(strOrders);
                        String strResult = json.getString("rec_list");

                        Type listType = new TypeToken<List<InboxBean.ResultData.Lists.Rec_list>>() {
                        }.getType();
                        Gson gson = new Gson();
                        mProjectList = gson.fromJson(strResult, listType);
                        switchPageBySize();
                        mInboxAdapter.data(mProjectList);
                        //专题数据存本地
                        Gson gsons = new Gson();
                        String jsonStr = gsons.toJson(mProjectList); //将List转换成Json
                        SharedPreferencesUtil.putString(config, INBOXS + PlotRead.getAppUser().uid, jsonStr);
                        mLoadingLayout.setVisibility(View.GONE);
                        mContentLayout.setVisibility(View.VISIBLE);
                        mWrongLayout.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(String error) {
                mLoadingLayout.setVisibility(View.GONE);
                mContentLayout.setVisibility(View.GONE);
                mWrongLayout.setVisibility(View.VISIBLE);
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
