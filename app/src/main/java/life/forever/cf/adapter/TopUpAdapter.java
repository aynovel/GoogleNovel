package life.forever.cf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.TopUpListBean;
import life.forever.cf.publics.OnItemClickListener;

import java.util.List;

public class TopUpAdapter extends RecyclerView.Adapter<TopUpAdapter.RecomHolder> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    List<TopUpListBean.ResultData.Info.Order_data> mList;
    private final OnItemClickListener onItemClickListener;
    public TopUpAdapter(Context context, List<TopUpListBean.ResultData.Info.Order_data> List, OnItemClickListener onItemClickListener) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mList = List;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecomHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View convertView = mInflater.inflate(R.layout.top_up_item, viewGroup, false);
        RecomHolder holder = new RecomHolder(convertView);
        convertView.setTag(holder);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecomHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder);
                }
            }
        });
        holder.mTvCoins.setText(mList.get(position).money);
        if("1".equals(mList.get(position).defaults)){
            holder.mSupport.bringToFront();
            holder.mSupport.setVisibility(View.VISIBLE);
        }else{
            holder.mSupport.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(mList.get(position).giving) || "0".equals(mList.get(position).giving)){
            holder.mTvCoupons.setVisibility(View.GONE);
        }else {
            holder.mTvCoupons.setVisibility(View.VISIBLE);
            holder.mTvCoupons.setText("+"+mList.get(position).giving+" "+mContext.getString(R.string.topup_bouns));
        }
        if (TextUtils.isEmpty(mList.get(position).rec_title)) {
            holder.mTvAdd.setVisibility(View.GONE);
        } else {
            holder.mTvAdd.setVisibility(View.VISIBLE);
            holder.mTvAdd.setText(mList.get(position).rec_title);
        }
        if (mList.get(position).is_rec.equals("0") || mList.get(position).is_rec.equals("1")){
            holder.mTvMoney.setText("$ " + mList.get(position).rmb);
        }else{
            holder.mTvMoney.setText(mList.get(position).is_rec);
        }


//        if (mList != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public static class RecomHolder extends RecyclerView.ViewHolder {

        public TextView mTvCoins, mTvCoupons, mTvAdd, mTvMoney;
        public LinearLayout mLayoutCoupons;
        public ImageView mSupport;

        public RecomHolder(View itemView) {
            super(itemView);
            mSupport = itemView.findViewById(R.id.support);
            mTvCoins = itemView.findViewById(R.id.tv_coins);
            mLayoutCoupons = itemView.findViewById(R.id.layout_coupons);
            mTvCoupons = itemView.findViewById(R.id.tv_coupons);
            mTvAdd = itemView.findViewById(R.id.tv_add);
            mTvMoney = itemView.findViewById(R.id.tv_money);

        }
    }



}
