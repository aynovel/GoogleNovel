package life.forever.cf.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchResultAdapter extends RecyclerView.Adapter implements Constant {

    private final Context context;
    private final List<Work> works;
    private String keyWord = BLANK;

    public SearchResultAdapter(Context context, List<Work> works) {
        this.context = context;
        this.works = works;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ZERO) {
            return new NoneViewHolder(context, parent);
        }
        return new ResultHolder(LayoutInflater.from(context).inflate(R.layout.item_search_child_work, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoneViewHolder) {
            NoneViewHolder viewHolder = (NoneViewHolder) holder;
            viewHolder.description.setText(context.getString(R.string.no_you_like));
            return;
        }
        ResultHolder viewHolder = (ResultHolder) holder;
        Work work = works.get(position);
        List<String> list = new ArrayList<>();
        String tag;
        if (work.tag != null) {
            for (int i = 0; i < work.tag.size(); i++) {
                list.add(work.tag.get(i).tag);
            }
            tag = Joiner.on("·").join(list);
        } else {
            tag = "";
        }

        GlideUtil.picCache(context, work.cover,work.wid+"small",R.drawable.default_work_cover,  viewHolder.cover);
//
//        String cover = PlotRead.getConfig().getString(work.wid+"small","");
//        if (TextUtils.isEmpty(cover)){
////            SharedPreferencesUtil.putString(PlotRead.getConfig(), work.wid+"small",work.cover);
//            GlideUtil.recommentLoad(context,work.wid+"small",work.cover,work.cover,R.drawable.default_work_cover, viewHolder.cover);
//        }else{
//            GlideUtil.recommentLoad(context,"",cover,work.cover,R.drawable.default_work_cover, viewHolder.cover);
//        }
        viewHolder.description.setText(work.description);
        viewHolder.author.setText(work.author);
        viewHolder.sortname.setText(tag);
        viewHolder.itemView.setOnClickListener(new OnItemClick(work));
        if (!TextUtils.isEmpty(keyWord) && work.title.contains(keyWord)) {
            int start = work.title.indexOf(keyWord);
            SpannableString span = new SpannableString(work.title);
            span.setSpan(new ForegroundColorSpan(THEME_COLOR), start, start + keyWord.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            viewHolder.title.setText(span);
        } else {
            viewHolder.title.setText(work.title);
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

    public void update(String key) {
        keyWord = key;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder
     *
     * @author haojie
     */
    class ResultHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.sort_name)
        TextView sortname;

        ResultHolder(View itemView) {
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

        private final Work work;

        OnItemClick(Work work) {
            this.work = work;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, WorkDetailActivity.class);
            intent.putExtra("wid", work.wid);
            intent.putExtra("recid", work.recId);
            context.startActivity(intent);
        }
    }
}
