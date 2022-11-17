package life.forever.cf.adapter.person.account;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseRecyclerViewActivity;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;

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


public class ConsumeRecordActivity extends BaseRecyclerViewActivity {

    private final List<ConsumeRecord> records = new ArrayList<>();
    private ConsumeRecordAdapter recordAdapter;

    private int pageIndex = ONE;
    private int totalPage = ZERO;

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setMiddleText(ACCOUNT_STRING_CONSUME_RECORD);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mRefreshLayout.setHasHeader(FALSE);
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetch();
        }
    };

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        recordAdapter = new ConsumeRecordAdapter();
        mRecyclerView.setAdapter(recordAdapter);
        fetch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_LOG_IN) {
            records.clear();
            recordAdapter.notifyDataSetChanged();
            reload();
        }
    }

    private class ConsumeRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ZERO) {
                return new NoneViewHolder(context, parent);
            }
            return new ConsumeRecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_consume_record, parent, FALSE));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NoneViewHolder) {
                NoneViewHolder viewHolder = (NoneViewHolder) holder;
                viewHolder.description.setText(getString(R.string.no_consume));
                return;
            }
            ConsumeRecordViewHolder viewHolder = (ConsumeRecordViewHolder) holder;
            ConsumeRecord record = records.get(position);
            viewHolder.title.setText(record.title);
            viewHolder.date.setText(ComYou.timeFormat(record.addtime, DATE_FORMATTER_4));
            if (record.account == ONE) {
                viewHolder.money.setText(String.format(Locale.getDefault(), "-%d%s", record.money,getString(R.string.book_money)));
            } else if (record.account == TWO) {
                viewHolder.money.setText(String.format(Locale.getDefault(), "-%d%s", record.money,getString(R.string.bean)));
            } else {
                viewHolder.money.setText(String.format(Locale.getDefault(), "-%d%s", record.money,getString(R.string.book_money)));
            }

        }

        @Override
        public int getItemCount() {
            if (records.size() == ZERO) {
                return ONE;
            }
            return records.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (records.size() == ZERO) {
                return ZERO;
            }
            return ONE;
        }
    }

    class ConsumeRecordViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.date)
        TextView date;

        ConsumeRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class ConsumeRecord {
        int addtime;
        String title;
        int money;

        /**
         * 1：书币 2：书豆 3：现金
         */
        int account;
    }

    private void fetch() {
        NetRequest.userConsumeRecord(pageIndex, new OkHttpResult() {

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
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        int count = JSONUtil.getInt(result, "count");
                        if (pageIndex == ONE && totalPage == ZERO) {
                            totalPage = count % TWENTY == ZERO ? count / TWENTY : count / TWENTY + ONE;
                            mRefreshLayout.setHasFooter(totalPage > ONE);
                        }
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(lists, i);
                            ConsumeRecord record = new ConsumeRecord();
                            record.title = JSONUtil.getString(child, "note");
                            record.addtime = JSONUtil.getInt(child, "addtime");
                            record.account = JSONUtil.getInt(child, "account");
                            record.money = (int) JSONUtil.getDouble(child, "total_price");
                            records.add(record);
                        }
                        recordAdapter.notifyDataSetChanged();
                        pageIndex++;
                        mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(ConsumeRecordActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        pageIndex = ONE;
        totalPage = ZERO;
        fetch();
    }
}
