package life.forever.cf.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.DisplayUtil;

import java.util.List;


public class ReadPreferenceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final int HEADER = ZERO;
    private final int NORMAL = ONE;

    Context context;
    List<Preference> preferences;

    public ReadPreferenceAdapter(Context context, List<Preference> preferences) {
        this.context = context;
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            TextView textView = new TextView(context);
            textView.setText(context.getString(R.string.choose_your_reading));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FOURTEEN);
            textView.setTextColor(GRAY_1);
            int padding = DisplayUtil.dp2px(context, TWENTY);
            textView.setPadding(padding, padding, padding, ZERO);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderViewHolder(textView);
        }
        return new NormalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_read_preference_setting, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return preferences.size() + ONE;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == ZERO) {
            return HEADER;
        }
        return NORMAL;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {

        public NormalViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class Preference {

        int id;
        String logo;
        String name;
        boolean check;
    }
}
