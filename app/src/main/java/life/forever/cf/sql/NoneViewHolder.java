package life.forever.cf.sql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NoneViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.description)
    public TextView description;

    public NoneViewHolder(Context context, ViewGroup parent) {
        this(LayoutInflater.from(context).inflate(R.layout.layout_list_is_none, parent, false));
    }

    public NoneViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
