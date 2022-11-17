package life.forever.cf.bookcase.bookweight;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EditPopup extends PopupWindow implements Constant {

    @BindView(R.id.selectAll)
    TextView mSelectAll;
    @BindView(R.id.delete)
    TextView mDelete;

    private final EditPopup.OnItemClickListener onItemClickListener;

    public EditPopup(Context context, EditPopup.OnItemClickListener onItemClickListener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_book_shelf_edit_popup, null, FALSE);
        setContentView(root);
        ButterKnife.bind(this, root);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_alpha_style);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        this.onItemClickListener = onItemClickListener;
    }

    @OnClick(R.id.selectAll)
    void onSelectAllClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(mSelectAll, ZERO);
        }
    }

    @OnClick(R.id.delete)
    void onDeleteClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(mDelete, ONE);
        }
    }

    public void show(View parent, int total) {
        mSelectAll.setText(aiye_STRING_SELECT_ALL);
        mSelectAll.setEnabled(TRUE);
        mDelete.setEnabled(FALSE);
        mDelete.setText(SHELF_STRING_DELETES);
        showAtLocation(parent, Gravity.BOTTOM, ZERO, ZERO);
    }

    @Override
    public void update(int total, int select) {
        if (select == ZERO) {
            mDelete.setEnabled(FALSE);
        } else {
            mDelete.setEnabled(TRUE);
        }
        if (select == total) {
            mSelectAll.setText(aiye_STRING_CANCEL_SELECT_ALL);
            mDelete.setText(String.format(SHELF_STRING_DELETE, select));
        } else {
            mSelectAll.setText(aiye_STRING_SELECT_ALL);
            mDelete.setText(SHELF_STRING_DELETES);
        }
        if(select==0){
            mDelete.setText(SHELF_STRING_DELETES);
        }else {
            mDelete.setText(String.format(SHELF_STRING_DELETE, select));
        }

    }

    public interface OnItemClickListener {

        void onItemClick(TextView textView, int position);
    }

}
