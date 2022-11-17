package life.forever.cf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.BillBean;
import life.forever.cf.publics.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final Context context;
    private List<BillBean.ResultData.Lists> mList;
    private int mType;

    public BillAdapter(Context context, List<BillBean.ResultData.Lists> list, int type) {
        this.context = context;
        this.mList = list;
        this.mType = type;
    }

    public void data(List<BillBean.ResultData.Lists> List, int type) {
        this.mList = List;
        this.mType = type;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_record_list, parent, FALSE));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListViewHolder) {
            ListViewHolder viewHolder = (ListViewHolder) holder;
            BillBean.ResultData.Lists bean = mList.get(position);
            viewHolder.mTvState.setText(bean.name);
            viewHolder.mTvTime.setText(bean.format_time);
            if (mType == ZERO) {
                viewHolder.mTvMoney.setTextColor(context.getResources().getColor(R.color.color_F9791C));
                viewHolder.mTvMoney.setText("+" + bean.price + " " + bean.unit);
            } else {
                viewHolder.mTvMoney.setTextColor(context.getResources().getColor(R.color.color_000001));
                viewHolder.mTvMoney.setText("-" + bean.price + " " + bean.unit);
            }
            if (TextUtils.isEmpty(bean.format_end_time)) {
                viewHolder.mTvExpireTime.setText("");
            } else {
                if (bean.format_end_time.equals("false")){
                    viewHolder.mTvExpireTime.setText("");
                }else {
                    viewHolder.mTvExpireTime.setText(context.getString(R.string.bill_expire_on) + bean.format_end_time);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_state)
        TextView mTvState;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_money)
        TextView mTvMoney;
        @BindView(R.id.tv_expire_time)
        TextView mTvExpireTime;

        private ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
