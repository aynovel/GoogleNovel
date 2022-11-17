package life.forever.cf.publics.weight.poputil;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.DisplayUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagePopup extends PopupWindow implements Constant {

    @BindView(R.id.ll_hottest)
    LinearLayout ll_hottest;
    @BindView(R.id.tv_hottest)
    TextView tv_hottest;
    @BindView(R.id.iv_hottest)
    ImageView iv_hottest;

    @BindView(R.id.ll_newest)
    LinearLayout ll_newest;
    @BindView(R.id.tv_newest)
    TextView tv_newest;
    @BindView(R.id.iv_newest)
    ImageView iv_newest;


    private final OnItemClickListener onItemClickListener;
    Context mContext ;

    public ManagePopup(Context context, int showIndex, OnItemClickListener onItemClickListener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_book_shelf_manage_popup, null, FALSE);
        setContentView(root);
        mContext = context;
        ButterKnife.bind(this, root);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_alpha_style);
        setOutsideTouchable(FALSE);
        setFocusable(TRUE);
        setBackgroundDrawable(new ColorDrawable());
        this.onItemClickListener = onItemClickListener;
        showIn(showIndex);

    }

    public void show(View parent) {
        showAsDropDown(parent, - DisplayUtil.dp2px(PlotRead.getApplication(), FIFTY_THREE), DisplayUtil.dp2px(PlotRead.getApplication(), FOUR));
    }

    public interface OnItemClickListener {

        void onItemClick(LinearLayout choose, int position);
    }

    public void showIn(int showIndex){
        if (showIndex == 1){
            iv_hottest.setVisibility(View.VISIBLE);
            iv_newest.setVisibility(View.GONE);
            tv_hottest.setTextColor(mContext.getResources().getColor(R.color.theme_color));
            tv_newest.setTextColor(mContext.getResources().getColor(R.color.color_656667));

        }else{
            iv_newest.setVisibility(View.VISIBLE);
            iv_hottest.setVisibility(View.GONE);
            tv_newest.setTextColor(mContext.getResources().getColor(R.color.theme_color));
            tv_hottest.setTextColor(mContext.getResources().getColor(R.color.color_656667));
        }
    }


    @OnClick(R.id.ll_hottest)
    void onHottestClick() {
        dismiss();
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(ll_hottest, ONE);
        }
    }

    @OnClick(R.id.ll_newest)
    void onNewestClick() {
        dismiss();
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(ll_newest, TWO);
        }
    }


}
