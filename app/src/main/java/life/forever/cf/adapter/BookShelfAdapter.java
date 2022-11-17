package life.forever.cf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.Work;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BookShelfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    double progress = 0.0;
    private final Context context;
    private final List<Work> works;
    private List<Work> selects;
    private boolean isEditStatus;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public BookShelfAdapter(Context context, List<Work> works) {
        this.context = context;
        this.works = works;
    }

    public void update(boolean isEditStatus, List<Work> selects) {
        this.isEditStatus = isEditStatus;
        this.selects = selects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GridViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_shelf_grid, parent, FALSE));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new OnItemViewClick(position));
        holder.itemView.setOnLongClickListener(new OnItemViewLongClick());
        Work work = works.get(position);
        GridViewHolder viewHolder = (GridViewHolder) holder;
        if (isEditStatus&&work.is_rec == ZERO) {
            viewHolder.read_progress.setVisibility(View.GONE);
            viewHolder.select.setVisibility(View.VISIBLE);
            if (selects.contains(work)) {
                viewHolder.select.setImageResource(R.drawable.book_shelf_item_selected);
            } else {
                viewHolder.select.setImageResource(R.drawable.book_shelf_item_unselected);
            }
        } else {
            if (isEditStatus) {
                holder.itemView.setClickable(false);
                holder.itemView.setLongClickable(false);
            }else {
                holder.itemView.setClickable(true);
                holder.itemView.setLongClickable(true);
            }
            viewHolder.read_progress.setVisibility(View.VISIBLE);
            viewHolder.select.setVisibility(View.GONE);
        }
        GlideUtil.picCache(context,work.cover,work.wid+"shelfpic",R.drawable.default_work_cover, viewHolder.cover);

//        String cover = PlotRead.getConfig().getString(work.wid+"shelfpic","");
//        if (TextUtils.isEmpty(cover)){
//            GlideUtil.recommentLoad(context,work.wid+"shelfpic",work.cover,work.cover,R.drawable.default_work_cover, viewHolder.cover);
//        }else{
//            GlideUtil.recommentLoad(context,"",cover,work.cover,R.drawable.default_work_cover, viewHolder.cover);
//        }
        viewHolder.name.setText(work.title);
        if (work.lastChapterId == ZERO) {
            viewHolder.progress.setText(String.format(Locale.getDefault(), "未阅读／%d章", work.totalChapter));
        } else {
            viewHolder.progress.setText(String.format(Locale.getDefault(), "%d章／%d章", work.lastChapterOrder + ONE, work.totalChapter));
        }
        //设置进度值
        if (work.totalChapter == 0) {
            progress = 0;
            viewHolder.read_progress.setProgress((int) progress);
            viewHolder.progress_percent.setText((int) progress + "%");
        } else {
            progress = ((float) work.lastChapterOrder + ONE) / work.totalChapter * 100;
            viewHolder.read_progress.setProgress((int) progress);
            if (progress > 100) {
                viewHolder.progress_percent.setText("100%");
            } else {
                viewHolder.progress_percent.setText((int) progress + "%");
            }
        }
        if (work.wtype == TWO) {
            viewHolder.type.setImageResource(R.drawable.work_type_dialog);
        } else if (work.wtype == THREE) {
            viewHolder.type.setImageResource(R.drawable.work_type_cartoon);
        } else if (work.wtype == FOUR) {
            viewHolder.type.setImageResource(R.drawable.work_type_audio);
        } else {
            viewHolder.type.setImageResource(ZERO);
        }

        if ( position <= 2 ){
            viewHolder.update.setVisibility( View.GONE);
        }else {
            viewHolder.update.setVisibility(work.updateflag == ONE ? View.VISIBLE : View.GONE);
        }
        viewHolder.imagerec.setVisibility(work.is_rec == ONE ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    class OnItemViewClick implements View.OnClickListener {

        private final int position;

        OnItemViewClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        }
    }

    class OnItemViewLongClick implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick();
            }

            return true;
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.covers)
        ImageView cover;
        @BindView(R.id.type)
        ImageView type;
        @BindView(R.id.select)
        ImageView select;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.image_rec)
        ImageView imagerec;
        @BindView(R.id.progress)
        TextView progress;
        @BindView(R.id.update)
        ImageView update;
        @BindView(R.id.read_progress)
        ProgressBar read_progress;
        @BindView(R.id.progress_percent)
        TextView progress_percent;

        private GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick();
    }

}
