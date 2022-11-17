package life.forever.cf.publics.weight.poputil;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.weight.WrapListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OSheet extends PopupWindow implements Constant {

    @BindView(R.id.listView)
    WrapListView mListView;
    @BindView(R.id.cancel)
    TextView mCancel;

    private final Activity activity;
    private final String[] itemNames;

    public OSheet(Activity activity, String[] itemNames, AdapterView.OnItemClickListener onItemClickListener) {
        this.activity = activity;
        this.itemNames = itemNames;

        View root = LayoutInflater.from(activity).inflate(R.layout.layouottom_list_sheet, null);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_slide_alpha_bottom_style);
        setOutsideTouchable(false);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());
        setContentView(root);
        ButterKnife.bind(this, root);

        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setAdapter(new ItemAdapter());
    }

    public void show(View parent) {
        showAtLocation(parent, Gravity.BOTTOM, ZERO, ZERO);
        ComYou.setWindowAlpha(activity, DOT_FIVE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ComYou.setWindowAlpha(activity, ONE);
    }

    @OnClick(R.id.cancel)
    void onCancelClick() {
        dismiss();
    }

    class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemNames.length;
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
            TextView item = (TextView) LayoutInflater.from(activity).inflate(R.layout.item_bottom_list_sheet, null);
            item.setText(itemNames[position]);
            if (itemNames.length == ONE) {
                item.setBackgroundResource(R.drawable.selector_list_selector_corner_15dp);
            } else if (position == ZERO) {
                item.setBackgroundResource(R.drawable.selector_list_selector_top_corner_15dp);
            } else if (position == itemNames.length - 1) {
                item.setBackgroundResource(R.drawable.selector_list_selector_bottom_corner_15dp);
            } else {
                item.setBackgroundResource(R.drawable.selector_list_selector);
            }
            return item;
        }
    }

}
