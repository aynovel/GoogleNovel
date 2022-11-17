package life.forever.cf.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.interfaces.PageStyle;


public class PageStyleAdapter extends BaseListAdapter<Drawable> {
    private int currentChecked;

    @Override
    protected IViewHolder<Drawable> createViewHolder(int viewType) {
        return new PageStyleHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        IViewHolder iHolder = ((BaseViewHolder) holder).holder;
        PageStyleHolder pageStyleHolder = (PageStyleHolder) iHolder;
        if (currentChecked == position){
            pageStyleHolder.setChecked();
        }
    }

    public void setPageStyleChecked(PageStyle pageStyle){
        currentChecked = pageStyle.ordinal();
    }

    @Override
    protected void onItemClick(View v, int pos) {
        super.onItemClick(v, pos);
        currentChecked = pos;
        notifyDataSetChanged();
    }
}
