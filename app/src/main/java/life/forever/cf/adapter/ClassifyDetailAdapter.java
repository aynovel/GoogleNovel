package life.forever.cf.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.R;
import life.forever.cf.entry.Work;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClassifyDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final Context context;
    private final List<Work> works;

    public ClassifyDetailAdapter(Context context, List<Work> works) {
        this.context = context;
        this.works = works;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ZERO) {
            return new NoneViewHolder(context, parent);
        }
        return new ClassifyChildWorkHolder(LayoutInflater.from(context).inflate(R.layout.item_library_classify_child_work, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoneViewHolder) {
            NoneViewHolder viewHolder = (NoneViewHolder) holder;
            viewHolder.description.setText(context.getString(R.string.no_your_requirements));
            return;
        }
        ClassifyChildWorkHolder viewHolder = (ClassifyChildWorkHolder) holder;
        Work work = works.get(position);
        holder.itemView.setOnClickListener(new OnItemClick(work.wid));
        GlideUtil.load(context, work.cover, R.drawable.default_work_cover, viewHolder.cover);
        viewHolder.title.setText(work.title);
        viewHolder.description.setText(work.description);
        viewHolder.author.setText(work.author);
        viewHolder.classify.setText(work.sortTitle);
        viewHolder.clickCount.setText(work.isfinish == ZERO ? context.getString(R.string.serial) : context.getString(R.string.completed));
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
    class ClassifyChildWorkHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.classify)
        TextView classify;
        @BindView(R.id.clickCount)
        TextView clickCount;

        ClassifyChildWorkHolder(View itemView) {
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

        int wid;

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
