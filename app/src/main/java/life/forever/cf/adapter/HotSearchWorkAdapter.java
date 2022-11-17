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

import life.forever.cf.R;
import life.forever.cf.entry.Work;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HotSearchWorkAdapter extends RecyclerView.Adapter implements Constant {

    private final Context context;
    private final List<Work> works;

    public HotSearchWorkAdapter(Context context, List<Work> works) {
        this.context = context;
        this.works = works;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HotSearchWorkViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_hot_book, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Work work = works.get(position);
        HotSearchWorkViewHolder viewHolder = (HotSearchWorkViewHolder) holder;

        GlideUtil.picCache(context, work.cover,work.wid+"small",R.drawable.default_work_cover,  viewHolder.cover);

        viewHolder.name.setText(work.title);
        holder.itemView.setOnClickListener(new OnItemClick(work));
    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    class HotSearchWorkViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.name)
        TextView name;

        HotSearchWorkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class OnItemClick implements View.OnClickListener {

        private final Work work;

        public OnItemClick(Work work) {
            this.work = work;
        }

        @Override
        public void onClick(View v) {
            DeepLinkUtil.addPermanent(context, "event_search_recommend", "搜索页", "点击书籍推荐", "", work.wid+"", "", "", "", "");
            Intent intent = new Intent(context, WorkDetailActivity.class);
            intent.putExtra("wid", work.wid);
            intent.putExtra("recid", work.recId);
            context.startActivity(intent);
        }
    }
}
