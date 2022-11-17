package life.forever.cf.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * 阅读页
 * Created by ChenHaizhen on 21-5-10.
 */
public interface IViewHolder<T> {
    View createItemView(ViewGroup parent);
    void initView();
    void onBind(T data,int pos);
    void onClick();
}
