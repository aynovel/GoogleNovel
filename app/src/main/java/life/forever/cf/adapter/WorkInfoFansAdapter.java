package life.forever.cf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.Person;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkInfoFansAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Person> persons;

    public WorkInfoFansAdapter(Context context, List<Person> persons) {
        this.context = context;
        this.persons = persons;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FansViewHolder(LayoutInflater.from(context).inflate(R.layout.item_work_info_fans, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FansViewHolder viewHolder = (FansViewHolder) holder;
        GlideUtil.load(context, persons.get(position).logo, R.drawable.default_user_logo, viewHolder.head);
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    class FansViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.head)
        ImageView head;

        FansViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
