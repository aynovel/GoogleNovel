package life.forever.cf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.ParentCondition;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.DisplayUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConditionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final Context context;
    private final List<ParentCondition> conditions;

    public ConditionAdapter(Context context, List<ParentCondition> conditions) {
        this.context = context;
        this.conditions = conditions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConditionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_library_screen_condition, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConditionViewHolder viewHolder = (ConditionViewHolder) holder;
        final ParentCondition condition = conditions.get(position);
        viewHolder.conditionTitle.setText(condition.title);
        if (position == conditions.size() - ONE) {
            viewHolder.itemView.setPadding(ZERO, ZERO, ZERO, DisplayUtil.dp2px(context, TWENTY_FIVE));
        } else {
            viewHolder.itemView.setPadding(ZERO, ZERO, ZERO, ZERO);
        }
        final ChildConditionAdapter adapter = new ChildConditionAdapter(context, condition.conditions, condition.checkId);
        viewHolder.gridView.setAdapter(adapter);
        viewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                condition.checkId = condition.conditions.get(position).id;
                adapter.update(condition.checkId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conditions.size();
    }

    class ConditionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.conditionTitle)
        TextView conditionTitle;
        @BindView(R.id.gridView)
        GridView gridView;

        ConditionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
