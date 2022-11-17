package life.forever.cf.adapter;



import static life.forever.cf.publics.Constant.FALSE;
import static life.forever.cf.publics.Constant.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.MoreBuy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreBuyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<MoreBuy> mHRMoreBuy;
    private OnItemClickListener moreBuyItemClick;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.moreBuyItemClick = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder viewHolder);
    }


    public MoreBuyAdapter(Context context, List<MoreBuy> HRMoreBuy) {
        super();
        mContext = context;
        this.mHRMoreBuy = HRMoreBuy;
    }

    public void update() {

        notifyDataSetChanged();
    }

    public MoreBuy getSelectedMoreBuy()
    {
        MoreBuy temp = null;

        if(mHRMoreBuy != null && mHRMoreBuy.size() >0)
        {
            for (MoreBuy item:
                 mHRMoreBuy) {
                if(item.isclick)
                {
                    temp = item;
                    break;
                }
            }
        }

        return  temp;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MoreBuyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_more, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MoreBuyViewHolder viewHolder = (MoreBuyViewHolder) holder;
        MoreBuy HRMoreBuy = mHRMoreBuy.get(position);
        if (HRMoreBuy != null) {
            if (HRMoreBuy.isclick) {
                viewHolder.rl_bg.setBackgroundResource(R.drawable.more_chapter_select);
                viewHolder.tv_prace.setTextColor(application.getResources().getColor(R.color.theme_color));
            } else {
                viewHolder.rl_bg.setBackgroundResource(R.drawable.more_chapter_no_select);
                viewHolder.tv_prace.setTextColor(application.getResources().getColor(R.color.color_333333));
            }
            viewHolder.tv_prace.setText(HRMoreBuy.sum + " " + application.getString(R.string.topup_coins));

//            if (position == mHRMoreBuy.size() - 1) {
//                viewHolder.tv_chapters.setText(application.getString(R.string.chapters_hint));
//            } else {
//                viewHolder.tv_chapters.setText(HRMoreBuy.count + " " + application.getString(R.string.chapters_num));
//            }
//            viewHolder.tv_prace.setText(HRMoreBuy.sum + " " + application.getString(R.string.topup_coins));

            if (position == mHRMoreBuy.size() - 1) {
                viewHolder.tv_chapters.setText(application.getString(R.string.remaining) + application.getString(R.string.chapters));
            } else {
                viewHolder.tv_chapters.setText(HRMoreBuy.count + " " + application.getString(R.string.chapters));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (moreBuyItemClick != null) {
                moreBuyItemClick.onItemClick(viewHolder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHRMoreBuy.size();
    }


    class MoreBuyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rl_bg)
        RelativeLayout rl_bg;
        @BindView(R.id.tv_prace)
        TextView tv_prace;
        @BindView(R.id.tv_chapters)
        TextView tv_chapters;

        public MoreBuyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
