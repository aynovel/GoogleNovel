package life.forever.cf.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import life.forever.cf.R;

import java.util.List;


public class HotKeyAdapter implements Adapter {

    private final Context context;
    private final List<String> keys;
    private final OnItemClickListener onItemClickListener;

    public HotKeyAdapter(Context context, List<String> keys, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.keys = keys;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public TextView getItemView(ViewGroup parent, int position) {
        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.item_search_hot_key, parent, false);
        textView.setText(keys.get(position).split("#")[0]);
        if(keys.get(position).contains("1")){
            textView.setTextColor(context.getResources().getColor(R.color.theme_color));
            textView.setBackgroundResource(R.drawable.shape_theme_corner_5dp);
            Drawable drawable= context.getResources().getDrawable(R.drawable.icon_hot_read);

            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
        }
        textView.setOnClickListener(new OnItemClick(position));
        return textView;
    }

    private class OnItemClick implements View.OnClickListener {

        private final int position;

        OnItemClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }
}
