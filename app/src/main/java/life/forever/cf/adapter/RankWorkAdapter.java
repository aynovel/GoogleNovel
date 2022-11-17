package life.forever.cf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.R;
import life.forever.cf.entry.Work;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RankWorkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final Context context;
    private final List<Work> works;
    private final String type;

    public RankWorkAdapter(Context context, List<Work> works, String type) {
        this.context = context;
        this.works = works;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ZERO) {
            return new NoneViewHolder(context, parent);
        }
        return new RankWorkHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_ranking_work, parent, FALSE));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoneViewHolder) {
            NoneViewHolder viewHolder = (NoneViewHolder) holder;
            viewHolder.description.setText(context.getString(R.string.entries_yet));
            return;
        }
        RankWorkHolder viewHolder = (RankWorkHolder) holder;
        Work work = works.get(position);

        holder.itemView.setOnClickListener(new OnItemClick(work.wid));

        GlideUtil.picCache(context,work.cover,work.wid + "ranks",R.drawable.default_work_cover, viewHolder.cover);

//        String cover = PlotRead.getConfig().getString(work.wid + "ranks", "");
//        if (TextUtils.isEmpty(cover)) {
//            GlideUtil.recommentLoad(context, work.wid + "ranks", work.cover, work.cover, R.drawable.default_work_cover, viewHolder.cover);
//        } else {
//            GlideUtil.recommentLoad(context, "", cover, work.cover, R.drawable.default_work_cover, viewHolder.cover);
//        }

        viewHolder.title.setText(work.title);
        viewHolder.description.setText(work.description);
        viewHolder.author.setText(work.author);
        viewHolder.tv_score.setText(work.score + "");
        viewHolder.tv_award.setText(ComYou.formatNum(work.pv));

        viewHolder.iv_start.setVisibility(View.GONE);
        viewHolder.tv_num.setText(work.sortTitle);
        viewHolder.tv_num.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(type) && type.equals("0")) {
            viewHolder.sortTitle.setVisibility(View.GONE);
            viewHolder.ll_ranking.setVisibility(View.GONE);
            viewHolder.sortTitle.setText(work.sortTitle);
        } else {
            viewHolder.sortTitle.setVisibility(View.GONE);
            viewHolder.ll_ranking.setVisibility(View.GONE);
            viewHolder.tv_sum_type.setText(work.sortTitle);
            if (!TextUtils.isEmpty(type) && type.equals("1")) {
                viewHolder.iv_type.setImageResource(R.drawable.ranking_monthly_ticket);
            } else if (!TextUtils.isEmpty(type) && type.equals("2")) {
                viewHolder.iv_type.setImageResource(R.drawable.ranking_the_heat);
            } else if (!TextUtils.isEmpty(type) && type.equals("3")) {
                viewHolder.iv_type.setImageResource(R.drawable.ranking_marshalling);
            }
        }
        if (position == ZERO) {
            viewHolder.sort.setText(BLANK);
            viewHolder.sort.setBackgroundResource(R.drawable.ranking_one);
        } else if (position == ONE) {
            viewHolder.sort.setText(BLANK);
            viewHolder.sort.setBackgroundResource(R.drawable.ranking_two);
        } else if (position == TWO) {
            viewHolder.sort.setText(BLANK);
            viewHolder.sort.setBackgroundResource(R.drawable.ranking_three);
        } else if (position == THREE) {
            viewHolder.sort.setText(BLANK);
            viewHolder.sort.setBackgroundResource(R.drawable.ranking_four);
        } else {
            viewHolder.sort.setBackgroundResource(R.drawable.ranking_other);
            viewHolder.sort.setText(String.valueOf(position + ONE));
        }
    }

    @Override
    public int getItemCount() {
        if (works.size() == ZERO) {
            return ONE;
        }
        return works.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (works.size() == ZERO) {
            return ZERO;
        }
        return ONE;
    }

    /**
     * ViewHolder
     *
     * @author haojie
     */
    static class RankWorkHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.sort)
        TextView sort;
        @BindView(R.id.sortTitle_type)
        TextView sortTitle;
        @BindView(R.id.iv_start)
        ImageView iv_start;
        @BindView(R.id.tv_num)
        TextView tv_num;

        @BindView(R.id.ll_ranking)
        LinearLayout ll_ranking;
        @BindView(R.id.iv_type)
        ImageView iv_type;
        @BindView(R.id.tv_sum_type)
        TextView tv_sum_type;

        @BindView(R.id.tv_score)
        TextView tv_score;
        @BindView(R.id.tv_award)
        TextView tv_award;

        @BindView(R.id.iv_bang)
        ImageView iv_bang;

        RankWorkHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 条目点击
     *
     * @author haojie
     */
    private class OnItemClick implements View.OnClickListener {

        private final int wid;

        OnItemClick(int wid) {
            this.wid = wid;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, WorkDetailActivity.class);
            intent.putExtra("wid", wid);
            context.startActivity(intent);
        }
    }
}
