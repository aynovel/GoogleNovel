
package life.forever.cf.adapter;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class AppendableAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<T> mDataItems = new ArrayList<T>();

    public void setDataItems(List<T> items) {
        mDataItems.clear();
        if (items != null) {
            mDataItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void appendDataItems(List<T> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        if (mDataItems.isEmpty()) {
            mDataItems.addAll(items);
            notifyDataSetChanged();
        } else {
            int positionStart = mDataItems.size();
            mDataItems.addAll(items);
            notifyItemRangeInserted(positionStart, items.size());
        }
    }

    public void instertDataItemsAhead(List<T> dataItems) {
        if (dataItems == null || dataItems.isEmpty()) {
            return;
        }
        final int AHEAD_OFFSET = 0;
        mDataItems.addAll(AHEAD_OFFSET, dataItems);
        notifyItemRangeInserted(AHEAD_OFFSET, dataItems.size());
    }

    @Override
    public int getItemCount() {
        return mDataItems == null ? 0 : mDataItems.size();
    }

    public List<T> getDataItems() {
        return mDataItems;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public OnItemClickLitener mOnItemClickLitener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    // 刷卡点击事件


}
