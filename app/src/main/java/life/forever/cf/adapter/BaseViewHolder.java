package life.forever.cf.adapter;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


/**
 * 阅读页
 * Created by ChenHaizhen on 21-5-10.
 */
public class BaseViewHolder<T> extends RecyclerView.ViewHolder{
    public IViewHolder<T> holder;

    public BaseViewHolder(View itemView, IViewHolder<T> holder) {
        super(itemView);
        this.holder = holder;
        holder.initView();
    }
}
