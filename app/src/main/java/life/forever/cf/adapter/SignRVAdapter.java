package life.forever.cf.adapter;

import static life.forever.cf.publics.Constant.DATE_FORMATTER_8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.SignBean;
import life.forever.cf.publics.tool.ComYou;

public class SignRVAdapter extends RecyclerView.Adapter<SignRVAdapter.RecomHolder> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final SignBean.ResultData mSignBean;

    public SignRVAdapter(Context context, SignBean.ResultData signbean) {
        super();
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mSignBean = signbean;
    }

    @NonNull
    @Override
    public RecomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View convertView = mInflater.inflate(R.layout.sign_item, viewGroup, false);
        RecomHolder holder = new RecomHolder(convertView);
        convertView.setTag(holder);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecomHolder holder, final int position) {
        SignBean.ResultData.Info.Sign mSign = mSignBean.info.sign.get(position);
        String todaytime = ComYou.timeFormat(ComYou.currentTimeSeconds(), DATE_FORMATTER_8);
        if (1 == mSign.is_sign && !mSign.date.equals(todaytime)) {
            holder.mImgSignedIn.setVisibility(View.VISIBLE);
            holder.mTvDate.setTextColor(mContext.getResources().getColor(R.color.color_E9FCFF));
            holder.mTvCoins.setTextColor(mContext.getResources().getColor(R.color.color_E9FCFF));
            holder.mLayoutSign.setBackground(mContext.getResources().getDrawable(R.drawable.bg_signed_in));
        } else if (1 == mSign.is_sign && mSign.date.equals(todaytime)) {
            holder.mImgSignedIn.setVisibility(View.VISIBLE);
            holder.mTvDate.setTextColor(mContext.getResources().getColor(R.color.color_E9FCFF));
            holder.mTvCoins.setTextColor(mContext.getResources().getColor(R.color.color_E9FCFF));
            holder.mLayoutSign.setBackground(mContext.getResources().getDrawable(R.drawable.bg_sign_today));
        } else {
            holder.mImgSignedIn.setVisibility(View.GONE);
            holder.mLayoutSign.setBackground(mContext.getResources().getDrawable(R.drawable.bg_sign));
        }

        String date = mSign.date;
        String mDate = date.substring(date.substring(0, date.indexOf(".")).length() + 1);
        holder.mTvDate.setText(mDate);// 签到日期
        holder.mTvCoins.setText(mSignBean.info.sign_price.get(position) + " "+mContext.getString(R.string.library_sign_coins));// 签到币数
        if (position + 1 == mSignBean.info.sign.size()) {
            holder.mImgCoins.setVisibility(View.GONE);
            holder.mTvCoins.setText(mContext.getString(R.string.library_sign_surprise));
            holder.mTvCoins.setTextColor(mContext.getResources().getColor(R.color.theme_color));
            holder.mImgGife.setVisibility(View.VISIBLE);
        }


        if (mSignBean != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mSignBean.info.sign.size();
    }


    public static class RecomHolder extends RecyclerView.ViewHolder {

        TextView mTvDate, mTvCoins;
        ImageView mImgGife;
        ImageView mImgCoins;
        ImageView mImgSignedIn;
        LinearLayout mLayoutSign;


        public RecomHolder(View itemView) {
            super(itemView);
            mLayoutSign = itemView.findViewById(R.id.layout_sign);
            mTvDate = itemView.findViewById(R.id.tv_date);
            mImgCoins = itemView.findViewById(R.id.img_coins);
            mTvCoins = itemView.findViewById(R.id.tv_coins);
            mImgGife = itemView.findViewById(R.id.img_gife);
            mImgSignedIn = itemView.findViewById(R.id.img_signed_in);
        }
    }
}
