package life.forever.cf.publics.weight.poputil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import life.forever.cf.R;


public class AnimotionPopupWindow extends PopupWindow implements View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;
    private View view;
    private AnimotionPopupWindowOnClickListener listener;

    public AnimotionPopupWindow(Activity mContext) {
        super(mContext);
        mActivity = mContext;
        init();
    }

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    private void init() {
        LayoutInflater inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_anim, null);
        //设置SelectPicPopupWindow的View
        this.setContentView(view);

        view.findViewById(R.id.layout_update).setOnClickListener(this);
        view.findViewById(R.id.layout_edit).setOnClickListener(this);
        view.findViewById(R.id.layout_read).setOnClickListener(this);
        view.findViewById(R.id.img_cancels).setOnClickListener(this);

        //设置SelectPicPopupWindow弹出窗体的宽
        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int heights = wm.getDefaultDisplay().getHeight();
        /*Display display = mActivity.getWindowManager().getDefaultDisplay();*/
        this.setWidth(width);
        //设置SelectPicPopupWindow弹出窗体的高
        if (heights > 2214) {
            this.setHeight(550);
        } else {
            this.setHeight(450);
        }
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);

        //设置动画
        this.setAnimationStyle(R.style.dialog_style);
        //实例化一个ColorDrawable颜色为全透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //popupWindow弹出后屏幕半透明
        BackgroudAlpha((float) 0.5);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        view.setOnTouchListener((v, event) -> {
            int height = view.findViewById(R.id.layout_info).getTop();
            int y = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (y < height) {
                    BackgroudAlpha((float) 1);
                    dismiss();
                }
            }
            return true;
        });


        /* LayoutAnimation */
        // 从自已3倍的位置下面移到自己原来的位置
//        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
//                0f, Animation.RELATIVE_TO_SELF,3f, Animation.RELATIVE_TO_SELF, 0);
//        animation.setInterpolator(new DecelerateInterpolator());
//        animation.setDuration(400);
//        animation.setStartOffset(150);
//        mLac = new LayoutAnimationController(animation, 0.12f);
//        mLac.setInterpolator(new DecelerateInterpolator());
    }

    //设置屏幕背景透明度
    public static void BackgroudAlpha(float alpha) {
        WindowManager.LayoutParams l = mActivity.getWindow().getAttributes();
        l.alpha = alpha;
        mActivity.getWindow().setAttributes(l);
    }

    public void show() {
        Rect rect = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        this.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_update:
                if (listener != null) {
                    listener.onPopWindowClickListener("Update reminder");
                    BackgroudAlpha((float) 1);
                    dismiss();
                }
                break;
            case R.id.layout_edit:
                if (listener != null) {
                    listener.onPopWindowClickListener("Edit Library");
                    BackgroudAlpha((float) 1);
                    dismiss();
                }
                break;
            case R.id.layout_read:
                if (listener != null) {
                    listener.onPopWindowClickListener("Read History");
                    BackgroudAlpha((float) 1);
                    dismiss();
                }
                break;
            case R.id.img_cancels:
                BackgroudAlpha((float) 1);
                dismiss();
                break;
        }
    }

    public interface AnimotionPopupWindowOnClickListener {
        void onPopWindowClickListener(String name);
    }

    //设置监听事件
    public void setAnimotionPopupWindowOnClickListener(AnimotionPopupWindowOnClickListener listener) {
        this.listener = listener;
    }
}