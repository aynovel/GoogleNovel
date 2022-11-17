package life.forever.cf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.SearchKey;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchHistoryAdapter extends RecyclerView.Adapter {

    Context context;
    List<SearchKey> keys;
    OnItemClickListener onItemClick;
    OnItemClearClickListener onItemClearClick;

    public SearchHistoryAdapter(Context context, List<SearchKey> keys) {
        this.context = context;
        this.keys = keys;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        HistoryViewHolder viewHolder = (HistoryViewHolder) holder;
        viewHolder.textView.setText(keys.get(position).keyWord);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onItemClick != null) {
                    onItemClick.onItemClick(position);
                }
            }
        });
        viewHolder.imgclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClearClick != null) {
                    onItemClearClick.onItemClick(keys.get(position).keyWord);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView textView;

        @BindView(R.id.img_clear)
        ImageView imgclear;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClick = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void setOnItemClearClickListener(OnItemClearClickListener onItemClearClickListener) {
        this.onItemClearClick = onItemClearClickListener;
    }

    public interface OnItemClearClickListener {

        void onItemClick(String name);
    }

}
