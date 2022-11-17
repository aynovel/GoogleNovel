package life.forever.cf.activtiy;

import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Person;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseRecyclerViewActivity;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.LevelView;

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

public class FansListActivity extends BaseRecyclerViewActivity {

    private int wid;
    private final List<Person> fans = new ArrayList<>();
    private FansAdapter fansAdapter;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setMiddleText(aiye_STRING_FANS_LIST);
        mTitleBar.setRightText(aiye_STRING_FANS_RULE);
        mTitleBar.showRightImageView(FALSE);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setRightTextViewOnClickListener(onRuleClick);
        mRefreshLayout.setHasHeader(FALSE);
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        wid = getIntent().getIntExtra("wid", ZERO);
        fansAdapter = new FansAdapter();
        mRecyclerView.setAdapter(fansAdapter);
        getFansList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_LOG_IN) {
            fans.clear();
            fansAdapter.notifyDataSetChanged();
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
        getFansList();
    }

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            getFansList();
        }
    };

    private void getFansList() {
        NetRequest.fansList(wid, pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                }
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    JSONArray fensiList = JSONUtil.getJSONArray(result, "fensiList");
                    int total = JSONUtil.getInt(result, "total");
                    if (pageIndex == ONE && totalPage == ZERO) {
                        totalPage = total % TWENTY == ZERO ? total / TWENTY : total / TWENTY + ONE;
                        mRefreshLayout.setHasFooter(totalPage > ONE);
                    }
                    for (int i = ZERO; fensiList != null && i < fensiList.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(fensiList, i);
                        Person person = BeanParser.getPerson(child);
                        fans.add(person);
                    }
                    fansAdapter.notifyDataSetChanged();
                    pageIndex++;
                    mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                } else {
                    NetRequest.error(FansListActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class FansAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ZERO) {
                return new NoneViewHolder(context, parent);
            }
            return new FansViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fans, parent, FALSE));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NoneViewHolder) {
                NoneViewHolder viewHolder = (NoneViewHolder) holder;
                viewHolder.description.setText(getString(R.string.this_work_noyet));
                return;
            }
            FansViewHolder viewHolder = (FansViewHolder) holder;
            Person person = fans.get(position);
            GlideUtil.load(context, person.logo, R.drawable.default_user_logo, viewHolder.head);
            viewHolder.name.setText(person.nickname);
            viewHolder.level.setFansLevel(person.honorid);
            viewHolder.level.setText(person.honor);
            viewHolder.fansValue.setText(String.format(Locale.getDefault(), getString(R.string.fan_value), ComYou.formatNum(person.fansValue)));
            if (position == ZERO) {
                viewHolder.sort.setBackgroundResource(R.drawable.ng_first_icon);
                viewHolder.sort.setText(BLANK);
            } else if (position == ONE) {
                viewHolder.sort.setBackgroundResource(R.drawable.ic_album);
                viewHolder.sort.setText(BLANK);
            } else if (position == TWO) {
                viewHolder.sort.setBackgroundResource(R.drawable.piii);
                viewHolder.sort.setText(BLANK);
            } else {
                viewHolder.sort.setBackgroundResource(ZERO);
                viewHolder.sort.setText(String.valueOf(position + ONE));
            }
        }

        @Override
        public int getItemCount() {
            if (fans.size() == ZERO) {
                return ONE;
            }
            return fans.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (fans.size() == ZERO) {
                return ZERO;
            }
            return ONE;
        }
    }

    class FansViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sort)
        TextView sort;
        @BindView(R.id.head)
        ImageView head;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.level)
        LevelView level;
        @BindView(R.id.fansValue)
        TextView fansValue;

        FansViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final View.OnClickListener onRuleClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, FansRuleActivity.class);
            startActivity(intent);
        }
    };
}
