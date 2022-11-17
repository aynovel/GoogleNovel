package life.forever.cf.adapter;

import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.DiscoverBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DiscoverMoreActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    DiscoverMoreAdapter mDiscoverMoreAdapter;

    private int positions;
    private int recid;


    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
//        mTitleBar.setMiddleText(LOGIN_STRING_SERVICE_TERMS);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_discover_more);
        ButterKnife.bind(this);
    }

    @Override
    protected void initializeData() {
        positions = getIntent().getIntExtra("positions", ZERO);
        recid = getIntent().getIntExtra("rec_id", ZERO);
        LinearLayoutManager EightLayoutManager = new LinearLayoutManager(context);
        EightLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(EightLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        more();

    }

    /**
     * 请求发现数据
     */
    private void more() {
        showLoading(getString(R.string.loading));
        NetRequest.moreRequest(recid, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));

                        String resultString = jsonObject.getString("ResultData");
                        JSONObject jsonOders = new JSONObject(resultString);
                        String Titlename = jsonOders.getString("title");
                        mTitleBar.setMiddleText(Titlename);
                        String strOrders = jsonOders.getString("list");
                        String isImg = jsonOders.getString("isimg");
                        String recImg = jsonOders.getString("recimg");

                        Type listType = new TypeToken<List<DiscoverBean.ResultData.list>>() {
                        }.getType();
                        Gson gson = new Gson();
                        List<DiscoverBean.ResultData.list> contactList = gson.fromJson(strOrders, listType);
                        mDiscoverMoreAdapter = new DiscoverMoreAdapter(context, contactList, positions,isImg,recImg);
                        mRecyclerView.setAdapter(mDiscoverMoreAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later！");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
