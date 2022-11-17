package life.forever.cf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import life.forever.cf.R;
import life.forever.cf.entry.ChildCondition;
import life.forever.cf.publics.Constant;

import java.util.List;


public class ChildConditionAdapter extends BaseAdapter implements Constant {

    private final Context context;
    private final List<ChildCondition> conditions;
    private int checkId;

    ChildConditionAdapter(Context context, List<ChildCondition> conditions, int checkId) {
        this.context = context;
        this.conditions = conditions;
        this.checkId = checkId;
    }

    public void update(int checkId) {
        this.checkId = checkId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return conditions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_library_screening, parent, FALSE);
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        ChildCondition condition = conditions.get(position);
        checkBox.setText(condition.title);
        checkBox.setChecked(checkId == condition.id);

        return view;
    }

}
