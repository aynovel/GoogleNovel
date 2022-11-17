package life.forever.cf.adapter.person.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.R;
import life.forever.cf.entry.Voucher;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VoucherAdapter extends RecyclerView.Adapter implements Constant {

    private final int NONE = ZERO;
    private final int ITEM = ONE;

    private final Context context;
    private final List<Voucher> vouchers;

    VoucherAdapter(Context context, List<Voucher> vouchers) {
        this.context = context;
        this.vouchers = vouchers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == NONE) {
            return new NoneViewHolder(context, parent);
        }
        return new VoucherViewHolder(LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VoucherViewHolder) {
            Voucher voucher = vouchers.get(position);
            VoucherViewHolder viewHolder = (VoucherViewHolder) holder;
            viewHolder.mValue.setText(String.valueOf(voucher.value));
            viewHolder.mLeftValue.setText(String.format(Locale.getDefault(), context.getString(R.string.remaining_beans), voucher.left));
            viewHolder.mValueFrom.setText(voucher.name);
            viewHolder.mEnableDate.setText(String.format(Locale.getDefault(), ACCOUNT_STRING_VOUCHER_DATE, ComYou.timeFormat(voucher.endtime, DATE_FORMATTER_1)));

            viewHolder.mValue.setTextColor(voucher.status == ONE ? THEME_COLOR : GRAY_1);
            viewHolder.mName.setTextColor(voucher.status == ONE ? DARK_2 : GRAY_1);
            viewHolder.mLeftValue.setTextColor(voucher.status == ONE ? DARK_2 : GRAY_1);
            viewHolder.mValueFrom.setTextColor(voucher.status == ONE ? DARK_2 : GRAY_1);
            viewHolder.mEnableDate.setTextColor(voucher.status == ONE ? DARK_2 : GRAY_1);

            if (voucher.status == TWO) {
                viewHolder.mIcon.setVisibility(View.VISIBLE);
                if (voucher.left == ZERO) {
                    viewHolder.mValue.setText(String.valueOf(voucher.value));
                    viewHolder.mIcon.setImageResource(R.drawable.userd);
                } else {
                    viewHolder.mValue.setText(String.valueOf(voucher.value));
                    viewHolder.mIcon.setImageResource(R.drawable.vou);
                }
            } else {
                viewHolder.mIcon.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (vouchers.size() == ZERO) {
            return ONE;
        }
        return vouchers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (vouchers.size() == ZERO) {
            return NONE;
        } else {
            return ITEM;
        }
    }

    class VoucherViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.value)
        TextView mValue;
        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.leftValue)
        TextView mLeftValue;
        @BindView(R.id.voucherFrom)
        TextView mValueFrom;
        @BindView(R.id.enableDate)
        TextView mEnableDate;
        @BindView(R.id.overdueIcon)
        ImageView mIcon;

        VoucherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
